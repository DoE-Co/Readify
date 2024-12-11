package com.cs407.readify
import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.accessibility.AccessibilityManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment


class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Check permissions immediately
        //checkAndRequestPermissions()

        // Setup Navigation
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController



        // Check if accessibility service is enabled
        if (!isAccessibilityServiceEnabled()) {
            //showEnableAccessibilityDialog()
        }




        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets

        }

        // Check for overlay permission
        if (!Settings.canDrawOverlays(this)) {
            requestOverlayPermission()
        }

        // Handle text from browser selection
        handleIncomingText()

    }

    private fun handleIncomingText() {
        when (intent?.action) {
            Intent.ACTION_PROCESS_TEXT -> {
                val text = intent?.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT)
                    ?: intent?.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT_READONLY)

                text?.let {
                    // Navigate to appropriate fragment with text
                    val bundle = Bundle().apply {
                        putString("text", it.toString())
                    }
                    // You'll need to add this destination to your nav graph
                    //navController.navigate(R.id.translationFragment, bundle)
                }
            }
            Intent.ACTION_SEND -> {
                if (intent.type == "text/plain") {
                    intent.getStringExtra(Intent.EXTRA_TEXT)?.let { text ->
                        val bundle = Bundle().apply {
                            putString("text", text)
                        }
                       // navController.navigate(R.id.translationFragment, bundle)
                    }
                }
            }
        }
    }


    private fun showTranslationBottomSheet(text: String) {
        TestTranslationBottomSheet.newInstance(text)
            .show(supportFragmentManager, "translation")
    }


    private fun checkAndRequestPermissions() {
        // Check if accessibility service is enabled
        if (!isAccessibilityServiceEnabled()) {
            //showEnableAccessibilityDialog()
        }

        // Check overlay permission
        if (!Settings.canDrawOverlays(this)) {
            requestOverlayPermission()
        }
    }

    private fun isAccessibilityServiceEnabled(): Boolean {
        val expectedComponentName = ComponentName(this, TranslationOverlayService::class.java)

        val enabledServices = Settings.Secure.getString(
            contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        )

        return enabledServices?.contains(expectedComponentName.flattenToString()) == true
    }

//    private fun showEnableAccessibilityDialog() {
//        AlertDialog.Builder(this)
//            .setTitle("Enable Translation Service")
//            .setMessage("To use the translation overlay, please enable the accessibility service in Settings.")
//            .setPositiveButton("Open Settings") { _, _ ->
//                try {
//                    val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
//                    startActivity(intent)
//                } catch (e: Exception) {
//                    Toast.makeText(this, "Unable to open settings", Toast.LENGTH_SHORT).show()
//                }
//            }
//            .setCancelable(false)
//            .show()
//    }

    private fun requestOverlayPermission() {
        if (!Settings.canDrawOverlays(this)) {
            AlertDialog.Builder(this)
                .setTitle("Enable Overlay Permission")
                .setMessage("To show translations, please allow display over other apps.")
                .setPositiveButton("Open Settings") { _, _ ->
                    try {
                        val intent = Intent(
                            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:$packageName")
                        )
                        startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(this, "Unable to open settings", Toast.LENGTH_SHORT).show()
                    }
                }
                .setCancelable(false)
                .show()
        }
    }
}