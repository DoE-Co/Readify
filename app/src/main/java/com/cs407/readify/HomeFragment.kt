package com.cs407.readify

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.cs407.readify.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    // Note: The code references some views by findViewById. That's okay.

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

        // Set the click listener for the "+" button
        addButton.setOnClickListener {
            // Toggle visibility of the popup card
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
            // Navigate to ProfileFragment
            findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
        }

        // Handle popupCard item clicks:
        // For simplicity, we assume the first linear layout is "Scan Document", second is "Overlay" and third is "Add a Word".
        val scanLayout = popupCard.findViewById<LinearLayout>(R.id.scanDocumentLinearLayout)
        val overlayLayout = popupCard.findViewById<LinearLayout>(R.id.overlayLinearLayout)
        val addWordLayout = popupCard.findViewById<LinearLayout>(R.id.addWordLinearLayout)

        // Navigate to cameraFragment
        scanLayout.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_cameraFragment)
        }

        // If you want to add overlay or add word features later, handle them similarly:
        // overlayLayout.setOnClickListener { ... }
        // addWordLayout.setOnClickListener { ... }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
