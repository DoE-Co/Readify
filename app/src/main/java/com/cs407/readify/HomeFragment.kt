package com.cs407.readify

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.cs407.readify.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val addButton = view.findViewById<ImageButton>(R.id.addButton)
        val popupCard = view.findViewById<CardView>(R.id.popupCard)
        val profile = view.findViewById<ImageButton>(R.id.profileButton)

        addButton.setOnClickListener {
            if (popupCard.visibility == View.GONE) {
                popupCard.visibility = View.VISIBLE
                val params = popupCard.layoutParams as FrameLayout.LayoutParams
                params.bottomMargin = resources.getDimensionPixelSize(R.dimen.popup_card_margin)
                popupCard.layoutParams = params
            } else {
                popupCard.visibility = View.GONE
            }
        }

        profile.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
        }

        val scanLayout = popupCard.findViewById<LinearLayout>(R.id.scanDocumentLinearLayout)
        val overlayLayout = popupCard.findViewById<LinearLayout>(R.id.overlayLinearLayout)
        val addWordLayout = popupCard.findViewById<LinearLayout>(R.id.addWordLinearLayout)

        scanLayout.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_cameraFragment)
        }

        overlayLayout.setOnClickListener {
            showEnableAccessibilityDialog()
        }
    }

    private fun requestOverlayPermission() {
        if (!Settings.canDrawOverlays(requireContext())) {
            AlertDialog.Builder(requireContext())
                .setTitle("Enable Overlay Permission")
                .setMessage("To show translations, please allow display over other apps.")
                .setPositiveButton("Open Settings") { _, _ ->
                    try {
                        val intent = Intent(
                            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:${requireContext().packageName}")
                        )
                        startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), "Unable to open settings", Toast.LENGTH_SHORT).show()
                    }
                }
                .setCancelable(false)
                .show()
        } else {
            // Permission is already granted
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:${requireContext().packageName}")
            )
            startActivity(intent)
            //Toast.makeText(requireContext(), "Overlay permission is already granted", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showEnableAccessibilityDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Enable Translation Service")
            .setMessage("To use the translation overlay, please enable the accessibility service in Settings.")
            .setPositiveButton("Open Settings") { _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                    startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Unable to open settings", Toast.LENGTH_SHORT).show()
                }
            }
            .setCancelable(false)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}