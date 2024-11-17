package com.cs407.readify

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class TranslationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Make the activity background transparent
        setTheme(R.style.Theme_TransparentActivity)

        // Handle incoming text immediately
        when (intent?.action) {
            Intent.ACTION_PROCESS_TEXT -> {
                val text = intent?.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT)
                    ?: intent?.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT_READONLY)

                text?.let {
                    showTranslationBottomSheet(it.toString())
                }
            }
            else -> finish() // Close if no text received
        }
    }

    private fun showTranslationBottomSheet(text: String) {
        TestTranslationBottomSheet.newInstance(text)
            .apply {
                // Add dismiss listener to close the activity when bottom sheet is dismissed
                setOnDismissListener {
                    this@TranslationActivity.finish()
                }
            }
            .show(supportFragmentManager, "translation")
    }
}