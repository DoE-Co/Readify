package com.cs407.readify

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class TextDetectorService : AccessibilityService() {
    companion object {
        private const val TAG = "TextDetector"
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        val info = AccessibilityServiceInfo()
        info.apply {
            // Listen for all possible text-related events
            eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED or
                    AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED or
                    AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED or
                    AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED or
                    AccessibilityEvent.TYPE_VIEW_CLICKED or
                    AccessibilityEvent.TYPE_VIEW_FOCUSED
            feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
            flags = AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS or
                    AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS
            notificationTimeout = 100
        }
        serviceInfo = info
        Log.d(TAG, "Service Connected")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        // Log the event type
        Log.d(TAG, "Event Type: ${getEventTypeString(event.eventType)}")
        Log.d(TAG, "Package: ${event.packageName}")

        // Log any direct text from the event
        if (!event.text.isNullOrEmpty()) {
            Log.d(TAG, "Direct Event Text: ${event.text}")
        }

        // Try to get text from the source node
        val rootNode = event.source
        if (rootNode != null) {
            Log.d(TAG, "Starting node traversal...")
            findAllText(rootNode)
        }
    }

    private fun findAllText(node: AccessibilityNodeInfo?, depth: Int = 0) {
        if (node == null) return

        try {
            // Log text content if present
            if (!node.text.isNullOrEmpty()) {
                val indent = " ".repeat(depth * 2)
                Log.d(TAG, "$indent→ Found text: '${node.text}'")
                Log.d(TAG, "$indent  Class: ${node.className}")
                Log.d(TAG, "$indent  Clickable: ${node.isClickable}")
                Log.d(TAG, "$indent  Editable: ${node.isEditable}")
                Log.d(TAG, "$indent  Selected: ${node.isSelected}")
            }

            // Check view type
            when (node.className) {
                "android.widget.EditText" -> {
                    Log.d(TAG, "Found EditText with text: ${node.text}")
                }
                "android.widget.TextView" -> {
                    Log.d(TAG, "Found TextView with text: ${node.text}")
                }
                "android.widget.Button" -> {
                    Log.d(TAG, "Found Button with text: ${node.text}")
                }
            }

            // Recursively check all child nodes
            for (i in 0 until node.childCount) {
                findAllText(node.getChild(i), depth + 1)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error processing node: ${e.message}")
        } finally {
            // Don't recycle the root node as it's managed by the system
            if (depth > 0) {
                node.recycle()
            }
        }
    }

    private fun getEventTypeString(eventType: Int): String {
        return when (eventType) {
            AccessibilityEvent.TYPE_VIEW_CLICKED -> "TYPE_VIEW_CLICKED"
            AccessibilityEvent.TYPE_VIEW_LONG_CLICKED -> "TYPE_VIEW_LONG_CLICKED"
            AccessibilityEvent.TYPE_VIEW_FOCUSED -> "TYPE_VIEW_FOCUSED"
            AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED -> "TYPE_VIEW_TEXT_CHANGED"
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> "TYPE_WINDOW_STATE_CHANGED"
            AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED -> "TYPE_NOTIFICATION_STATE_CHANGED"
            AccessibilityEvent.TYPE_VIEW_HOVER_ENTER -> "TYPE_VIEW_HOVER_ENTER"
            AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED -> "TYPE_VIEW_TEXT_SELECTION_CHANGED"
            AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED -> "TYPE_WINDOW_CONTENT_CHANGED"
            else -> "TYPE_UNKNOWN"
        }
    }

    override fun onInterrupt() {
        Log.d(TAG, "Service Interrupted")
    }
}