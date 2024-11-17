package com.cs407.readify

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class TestTranslationBottomSheet : BottomSheetDialogFragment() {

    private var onDismissListener: (() -> Unit)? = null

    fun setOnDismissListener(listener: () -> Unit) {
        onDismissListener = listener
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDismissListener?.invoke()
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_translation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val originalText = view.findViewById<TextView>(R.id.originalText)
        val translatedText = view.findViewById<TextView>(R.id.translatedText)

        // Get the text passed from MainActivity
        arguments?.getString(ARG_TEXT)?.let { japaneseText ->
            originalText.text = japaneseText
            // Simulate translation with a simple mapping
            translatedText.text = simulateTranslation(japaneseText)
        }
    }

    private fun simulateTranslation(text: String): String {
        // Expanded dictionary for testing
        return when (text) {
            "こんにちは" -> "Hello"
            "さようなら" -> "Goodbye"
            "ありがとう" -> "Thank you"
            "おはよう" -> "Good Morning"
            "こんばんは" -> "Good Evening"
            "はい" -> "Yes"
            "いいえ" -> "No"
            // Add fallback for real text selections
            else -> "Selected text: $text\n(Translation would go here)"
        }
    }

    companion object {
        private const val ARG_TEXT = "text"

        fun newInstance(text: String): TestTranslationBottomSheet {
            return TestTranslationBottomSheet().apply {
                arguments = Bundle().apply {
                    putString(ARG_TEXT, text)
                }
            }
        }
    }
}
