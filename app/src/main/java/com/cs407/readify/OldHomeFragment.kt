package com.cs407.readify


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.cs407.readify.databinding.FragmentHomeBinding
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.fragment.findNavController


class HomeFragment : androidx.fragment.app.Fragment() {

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

        // Handle "+" button click
        val addButton = view.findViewById<ImageButton>(R.id.addButton)
        val popupCard = view.findViewById<CardView>(R.id.popupCard)
        val profile = view.findViewById<ImageButton>(R.id.profileButton)
        //val overlayOption = view.findViewById<LinearLayout>(R.id.overlayOption)
        //val scanDocumentButton = view.findViewById<LinearLayout>(R.id.scanDocumentButton)


        // Set the click listener for the "+" button
        addButton.setOnClickListener {
            // Toggle visibility of the popup card
            if (popupCard.visibility == View.GONE) {
                // Show the popup card just above the button
                popupCard.visibility = View.VISIBLE
                val params = popupCard.layoutParams as FrameLayout.LayoutParams
                params.bottomMargin = resources.getDimensionPixelSize(R.dimen.popup_card_margin) // Adjust margin as needed
                popupCard.layoutParams = params
            } else {
                // Hide the popup card when clicked again
                popupCard.visibility = View.GONE
            }
        }

        profile.setOnClickListener {
            // Navigate to ProfileFragment
            findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
        }

        // Navigate to OverlayFragment
        // overlayOption.setOnClickListener {
        //  findNavController().navigate(R.id.action_homeFragment_to_overlayFragment)
        //}

        // Navigate to cameraFragment
        //scanDocumentButton.setOnClickListener {
        //  findNavController().navigate(R.id.action_homeFragment_to_cameraFragment)
        //}
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}