package com.cs407.readify

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.google.mlkit.common.model.DownloadConditions

import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.TranslatorOptions
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.japanese.JapaneseTextRecognizerOptions
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class CameraFragment : Fragment() {

    companion object {
        private const val CAMERA_PERMISSION_CODE = 1001
        private const val CAMERA_REQUEST_CODE = 1002
        private const val TAG = "CameraFragment"
    }

    private var imageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_camera, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val captureButton = view.findViewById<Button>(R.id.captureButton)
        captureButton.setOnClickListener {
            checkCameraPermission()
        }
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_CODE
            )
        } else {
            openCamera()
        }
    }

    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Check if there's an app to handle camera intent
        if (cameraIntent.resolveActivity(requireActivity().packageManager) != null) {
            val imageFile = createImageFile() ?: run {
                Toast.makeText(requireContext(), "Error creating image file", Toast.LENGTH_SHORT).show()
                return
            }
            imageUri = FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.provider",
                imageFile
            )
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
            try {
                startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE)
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "No Camera App Found", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "No Camera App Found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createImageFile(): File? {
        return try {
            val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val storageDir: File? = requireActivity().getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES)
            File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (imageUri != null) {
                processImage(imageUri!!)
            } else {
                Toast.makeText(requireContext(), "Image URI is null", Toast.LENGTH_SHORT).show()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
            Toast.makeText(requireContext(), "Image capture failed or canceled", Toast.LENGTH_SHORT).show()
        }
    }

    private fun processImage(uri: Uri) {
        try {
            val image = InputImage.fromFilePath(requireContext(), uri)
            val recognizer = TextRecognition.getClient(JapaneseTextRecognizerOptions.Builder().build())

            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    val recognizedText = visionText.text.trim()
                    if (recognizedText.isNotBlank()) {
                        translateText(recognizedText)
                    } else {
                        Toast.makeText(requireContext(), "No text recognized", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Failed to recognize text", Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Error processing image", Toast.LENGTH_SHORT).show()
        }
    }

    private fun translateText(text: String) {
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.JAPANESE)
            .setTargetLanguage(TranslateLanguage.ENGLISH)
            .build()

        val translator = Translation.getClient(options)
        val conditions = DownloadConditions.Builder().requireWifi().build()

        translator.downloadModelIfNeeded(conditions)
            .addOnSuccessListener {
                translator.translate(text)
                    .addOnSuccessListener { translatedText ->
                        showTranslationResult(translatedText)
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(requireContext(), "Translation failed", Toast.LENGTH_SHORT).show()
                        e.printStackTrace()
                    }
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Failed to download translation model", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
    }

    private fun showTranslationResult(translatedText: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Translated Text")
            .setMessage(translatedText)
            .setPositiveButton("OK", null)
            .show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                openCamera()
            } else {
                Toast.makeText(requireContext(), "Camera permission is required", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
