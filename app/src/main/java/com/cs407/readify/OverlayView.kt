package com.cs407.readify

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

data class RecognizedBlock(
    val boundingBox: Rect,
    val translatedText: String
)

class OverlayView(context: Context, attrs: AttributeSet?) : View(context, attrs) {
    private val paintBox = Paint().apply {
        color = Color.GREEN
        style = Paint.Style.STROKE
        strokeWidth = 4f
    }
    private val paintText = Paint().apply {
        color = Color.WHITE
        textSize = 40f
        style = Paint.Style.FILL
    }

    private val recognizedBlocks = mutableListOf<RecognizedBlock>()

    fun updateBlocks(blocks: List<RecognizedBlock>) {
        recognizedBlocks.clear()
        recognizedBlocks.addAll(blocks)
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for (block in recognizedBlocks) {
            canvas.drawRect(block.boundingBox, paintBox)
            // Draw translated text above the bounding box
            canvas.drawText(block.translatedText, block.boundingBox.left.toFloat(), (block.boundingBox.top - 10).toFloat(), paintText)
        }
    }
}
