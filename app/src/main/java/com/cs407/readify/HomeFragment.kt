package com.cs407.readify


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

        addButton.setOnClickListener {
            // Toggle visibility of the popup card
            if (popupCard.visibility == View.GONE) {
                popupCard.visibility = View.VISIBLE
            } else {
                popupCard.visibility = View.GONE
            }
        }
        binding.profileButton.setOnClickListener {
            // Navigate to ProfileFragment
            //findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}