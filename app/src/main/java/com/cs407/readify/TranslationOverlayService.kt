package com.cs407.readify

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.annotation.SuppressLint
import android.content.ClipboardManager
import android.graphics.PixelFormat
import android.os.Build
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView


class TranslationOverlayService : AccessibilityService() {

    companion object {
        private const val TAG = "TranslationService"
        private const val YOUTUBE_MUSIC_PACKAGE = "com.google.android.apps.youtube.music"
        private const val YOUTUBE_PACKAGE = "com.google.android.youtube"
    }

    private var windowManager: WindowManager? = null
    private var overlayView: View? = null
    private var lastSelectedText: String? = null
    private var currentWordIndex = 0
    private var japaneseWords = listOf<String>()
    private var lastProcessedTime = 0L
    private val COOLDOWN_MS = 1000 // 1 second cooldown
    private var isVideoPaused = false
    private var currentSubtitleText: String? = null // Store current subtitle
    private var lastPauseCheckTime = 0L
    private val PAUSE_CHECK_COOLDOWN = 100L // milliseconds


    override fun onServiceConnected() {
        super.onServiceConnected()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        Log.d(TAG, "Service Connected")

        val info = AccessibilityServiceInfo()
        info.apply {
            // Add more event types to catch lyrics updates
            eventTypes = AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED or
                    AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED or
                    AccessibilityEvent.TYPE_VIEW_CLICKED
            feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
            flags = AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS or
                    AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS
            notificationTimeout = 100
        }
        this.serviceInfo = info
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        when (event.eventType) {
            AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED -> {
                handleTextSelection(event)
            }
            AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED -> {
                when (event.packageName?.toString()) {
                    YOUTUBE_PACKAGE -> {
                        Log.d("YOUTUBE", "Window content changed in YouTube")
                        updateCurrentSubtitle(event)
                    }
                }
            }
            AccessibilityEvent.TYPE_VIEW_CLICKED -> {
                when (event.packageName?.toString()) {
                    YOUTUBE_PACKAGE -> {
                        val oldPauseState = isVideoPaused
                        updateVideoPauseState(event.source)

                        if (isVideoPaused && !oldPauseState) {
                            Log.d("YOUTUBE", "Video just paused. Current subtitle: $currentSubtitleText")
                            if (!currentSubtitleText.isNullOrEmpty()) {
                                Log.d("YOUTUBE", "About to show overlay with: $currentSubtitleText")
                                safeShowTranslationOverlay(currentSubtitleText!!)
                            } else {
                                Log.d("YOUTUBE", "No subtitle text available to show")
                            }
                        } else if (!isVideoPaused && oldPauseState) {
                            Log.d("YOUTUBE", "Video unpaused, removing overlay")
                            removeExistingOverlay()
                        }
                    }
                    YOUTUBE_MUSIC_PACKAGE -> {
                        val currentTime = System.currentTimeMillis()
                        if (currentTime - lastProcessedTime < COOLDOWN_MS) {
                            return
                        }
                        handleYoutubeMusicLyrics(event)
                    }
                }
            }
        }
    }

    private fun safeShowTranslationOverlay(text: String) {
        Log.d("YOUTUBE", "safeShowTranslationOverlay called with text: $text")
        if (text.isBlank()) {
            Log.d("YOUTUBE", "Attempted to show empty text overlay")
            return
        }

        if (overlayView != null) {
            overlayView?.apply {
                Log.d("YOUTUBE", "Updating existing overlay with text: $text")
                findViewById<TextView>(R.id.originalText).text = text
                findViewById<TextView>(R.id.translatedText).text = simulateTranslation(text)
            }
        } else {
            Log.d("YOUTUBE", "Creating new overlay with text: $text")
            showTranslationOverlay(text)
        }
    }

    private fun updateCurrentSubtitle(event: AccessibilityEvent) {
        try {
            val rootNode = event.source ?: return
            findAndStoreSubtitle(rootNode)
            Log.d("YOUTUBE", "Current subtitle updated to: $currentSubtitleText")
            rootNode.recycle()
        } catch (e: Exception) {
            Log.e("YOUTUBE", "Error updating subtitle: ${e.message}")
        }
    }

    private fun findAndStoreSubtitle(node: AccessibilityNodeInfo?) {
        if (node == null) return

        try {
            if (node.className == "android.view.View" && !node.isClickable) {
                val text = node.text?.toString()
                if (!text.isNullOrEmpty() && containsJapaneseText(text)) {
                    Log.d("YOUTUBE", "Found Japanese text: $text")
                    currentSubtitleText = text
                    japaneseWords = extractJapaneseWords(text)
                    currentWordIndex = 0
                    Log.d("YOUTUBE", "Stored subtitle text: $currentSubtitleText")
                }
            }

            // Check children
            for (i in 0 until node.childCount) {
                val child = node.getChild(i)
                findAndStoreSubtitle(child)
                child?.recycle()
            }
        } catch (e: Exception) {
            Log.e("YOUTUBE", "Error finding subtitle: ${e.message}")
        }
    }

    private fun updateVideoPauseState(rootNode: AccessibilityNodeInfo?) {
        try {
            rootNode?.let { node ->
                // Only look for play button (visible when paused)
                isVideoPaused = node.findAccessibilityNodeInfosByText("Play").isNotEmpty()
                Log.d("YOUTUBE", "Video pause state updated: $isVideoPaused")
            }
        } catch (e: Exception) {
            Log.e("YOUTUBE", "Error checking video state: ${e.message}")
        }
    }

    private fun handleYoutubeSubtitles(event: AccessibilityEvent) {
        try {
            if (!isVideoPaused) return

            val rootNode = event.source ?: return
            findSubtitles(rootNode)
            rootNode.recycle()
        } catch (e: Exception) {
            Log.e(TAG, "Error processing subtitles: ${e.message}")
        }
    }

    private fun findSubtitles(node: AccessibilityNodeInfo?) {
        if (node == null) return

        try {
            if (node.className == "android.view.View" && !node.isClickable) {
                val text = node.text?.toString() ?: return
                if (containsJapaneseText(text) && text != lastSelectedText) {
                    lastSelectedText = text
                    Log.d("YOUTUBE", "Found Japanese subtitle when paused: $text")
                    japaneseWords = extractJapaneseWords(text)
                    currentWordIndex = 0

                    if (japaneseWords.isNotEmpty()) {
                        showTranslationOverlay(japaneseWords[0])
                    }
                }
            }

            // Check children
            for (i in 0 until node.childCount) {
                val child = node.getChild(i)
                findSubtitles(child)
                child?.recycle()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error finding subtitles: ${e.message}")
        }
    }

    private fun handleTextSelection(event: AccessibilityEvent) {
        try {
            val selectedText = event.text?.joinToString(" ") ?: return
            Log.d(TAG, "Selected text: $selectedText")

            // Get selection start position if available
            val selectionStart = event.fromIndex

            // Extract all Japanese words with their positions
            japaneseWords = extractJapaneseWords(selectedText)

            if (japaneseWords.isNotEmpty()) {
                // If we have a valid selection position, find closest word
                currentWordIndex = if (selectionStart >= 0) {
                    findClosestWordIndex(selectedText, selectionStart)
                } else {
                    0 // Fallback to first word if no position info
                }

                showTranslationOverlay(japaneseWords[currentWordIndex])
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error processing selection: ${e.message}")
        }
    }

    private fun findClosestWordIndex(text: String, position: Int): Int {
        var closestIndex = 0
        var minDistance = text.length

        // Find word whose start position is closest to selection
        japaneseWords.forEachIndexed { index, word ->
            val wordStart = text.indexOf(word)
            if (wordStart >= 0) {
                val distance = kotlin.math.abs(wordStart - position)
                if (distance < minDistance) {
                    minDistance = distance
                    closestIndex = index
                }
            }
        }

        return closestIndex
    }

    private fun handleYoutubeMusicLyrics(event: AccessibilityEvent) {
        try {
            val source = event.source ?: return
            if (source.className == "android.view.ViewGroup" && source.isClickable) {
                val text = source.text?.toString() ?: return
                if (containsJapaneseText(text)) {
                    lastProcessedTime = System.currentTimeMillis()
                    Log.d(TAG, "Processing clicked lyrics: $text")
                    japaneseWords = extractJapaneseWords(text)
                    currentWordIndex = 0

                    if (japaneseWords.isNotEmpty()) {
                        showTranslationOverlay(japaneseWords[0])
                    }
                }
            }
            source.recycle()
        } catch (e: Exception) {
            Log.e(TAG, "Error processing lyrics: ${e.message}")
        }
    }



    private fun containsJapaneseText(text: String): Boolean {
        val hiragana = '\u3040'..'\u309F'
        val katakana = '\u30A0'..'\u30FF'
        val kanji = '\u4E00'..'\u9FFF'

        return text.any { char ->
            char in hiragana || char in katakana || char in kanji
        }
    }

    private fun extractJapaneseWords(text: String): List<String> {
        val words = mutableListOf<String>()
        var currentWord = StringBuilder()

        for (char in text) {
            if (isJapaneseChar(char)) {
                currentWord.append(char)
            } else if (currentWord.isNotEmpty()) {
                words.add(currentWord.toString())
                currentWord = StringBuilder()
            }
        }

        if (currentWord.isNotEmpty()) {
            words.add(currentWord.toString())
        }

        return words
    }

    private fun isJapaneseChar(c: Char): Boolean {
        val hiragana = '\u3040'..'\u309F'
        val katakana = '\u30A0'..'\u30FF'
        val kanji = '\u4E00'..'\u9FFF'
        return c in hiragana || c in katakana || c in kanji
    }

    private fun showTranslationOverlay(text: String, youtube: Boolean = false) {
        Log.d("YOUTUBE", "Showing overlay with text: $text")
        removeExistingOverlay()

        if (text.isBlank()) {
            Log.d(TAG, "Attempted to show empty text overlay")
            return
        }

        val inflater = LayoutInflater.from(this)
        overlayView = inflater.inflate(R.layout.translation_overlay, null)

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.CENTER
        }

        overlayView?.apply {// more concise way to write this - don't need to repeat overlayView
            // Update the text views
            updateDisplayedWord()

            // Set up button clicks
            findViewById<Button>(R.id.prevButton).setOnClickListener {
                if (currentWordIndex > 0) {
                    currentWordIndex--
                    updateDisplayedWord()
                }
            }

            findViewById<Button>(R.id.nextButton).setOnClickListener {
                if (currentWordIndex < japaneseWords.size - 1) {
                    currentWordIndex++
                    updateDisplayedWord()
                }
            }

            findViewById<ImageButton>(R.id.closeButton).setOnClickListener {
                removeExistingOverlay()
            }

            // Make overlay draggable
            setOnTouchListener(object : View.OnTouchListener {
                private var initialX: Int = 0
                private var initialY: Int = 0
                private var initialTouchX: Float = 0f
                private var initialTouchY: Float = 0f

                @SuppressLint("ClickableViewAccessibility")
                override fun onTouch(v: View?, event: MotionEvent): Boolean {
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            initialX = params.x
                            initialY = params.y
                            initialTouchX = event.rawX
                            initialTouchY = event.rawY
                            return true
                        }
                        MotionEvent.ACTION_MOVE -> {
                            params.x = initialX + (event.rawX - initialTouchX).toInt()
                            params.y = initialY + (event.rawY - initialTouchY).toInt()
                            windowManager?.updateViewLayout(overlayView, params)
                            return true
                        }
                    }
                    return false
                }
            })
        }

        try {
            windowManager?.addView(overlayView, params) // actually adds the overlay over the screen
            Log.d(TAG, "Successfully added overlay view")
        } catch (e: Exception) {
            Log.e(TAG, "Error showing overlay: ${e.message}")
            e.printStackTrace()
        }
    }

    private fun updateDisplayedWord() {
        overlayView?.apply {
            val currentWord = japaneseWords.getOrNull(currentWordIndex) ?: return
            findViewById<TextView>(R.id.originalText).text = currentWord
            findViewById<TextView>(R.id.translatedText).text = simulateTranslation(currentWord)

            // Update button states
            findViewById<Button>(R.id.prevButton).isEnabled = currentWordIndex > 0
            findViewById<Button>(R.id.nextButton).isEnabled = currentWordIndex < japaneseWords.size - 1
        }
    }

    private fun simulateTranslation(text: String): String {
        return when (text) {
            "こんにちは" -> "Hello"
            "さようなら" -> "Goodbye"
            "ありがとう" -> "Thank you"
            "おはよう" -> "Good Morning"
            "こんばんは" -> "Good Evening"
            "私" -> "I/Me (わたし)"
            "です" -> "to be/is (polite)"
            "の" -> "of/possessive particle"
            "に" -> "to/at/in (particle)"
            "は" -> "topic marker (particle)"
            "を" -> "object marker (particle)"
            "が" -> "subject marker (particle)"
            "と" -> "with/and (particle)"
            "で" -> "at/by/with (particle)"
            "へ" -> "to/towards (particle)"
            "から" -> "from (particle)"
            "まで" -> "until (particle)"
            "も" -> "also/too (particle)"
            "な" -> "descriptive particle"
            "い" -> "adjective ending"
            "ます" -> "polite verb ending"
            "した" -> "past tense marker"
            "ません" -> "polite negative"
            "ない" -> "negative adjective"
            "たい" -> "want to ~"
            "人" -> "person (ひと)"
            "日本" -> "Japan (にほん)"
            "言葉" -> "language/word (ことば)"
            "本" -> "book (ほん)"
            "時間" -> "time (じかん)"
            "今日" -> "today (きょう)"
            "明日" -> "tomorrow (あした)"
            "昨日" -> "yesterday (きのう)"
            else -> "Translation not available"
        }
    }

    private fun removeExistingOverlay() {
        try {
            if (overlayView != null) {
                Log.d(TAG, "Removing overlay")
                windowManager?.removeView(overlayView)
                overlayView = null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error removing overlay: ${e.message}")
        }
    }

    override fun onInterrupt() {
        removeExistingOverlay()
    }
}