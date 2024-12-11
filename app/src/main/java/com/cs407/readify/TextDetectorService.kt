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
            // Log ALL nodes, not just ones with text
            val indent = " ".repeat(depth * 2)
            Log.d(TAG, "$indent→ Node Class: ${node.className}")
            Log.d(TAG, "$indent  Text: '${node.text}'")
            Log.d(TAG, "$indent  ContentDescription: '${node.contentDescription}'")
            Log.d(TAG, "$indent  ViewId: '${node.viewIdResourceName}'")
            Log.d(TAG, "$indent  Clickable: ${node.isClickable}")
            Log.d(TAG, "$indent  Visible: ${node.isVisibleToUser}")

            // Special check for specific view types that might be used for subtitles
            when (node.className) {
                "android.view.View",
                "android.widget.TextView",
                "android.widget.FrameLayout",
                "android.view.ViewGroup" -> {
                    Log.d(TAG, "$indent  Potential subtitle container found")
                }
            }

            // Recursively check all child nodes
            for (i in 0 until node.childCount) {
                findAllText(node.getChild(i), depth + 1)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error processing node: ${e.message}")
        } finally {
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