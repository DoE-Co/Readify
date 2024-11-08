package com.cs407.readify

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class ProfileFragment : Fragment() {

    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var nameTextView: TextView
    private lateinit var emailTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = inflater.inflate(R.layout.fragment_profile, container, false)

        // Initialize the TextViews
        nameTextView = binding.findViewById(R.id.nameTextView)
        emailTextView = binding.findViewById(R.id.emailTextView)

        // Get the SharedPreferences
        sharedPreferences = requireActivity().getSharedPreferences("user_prefs", 0)

        // Get the stored username and email from SharedPreferences
        val username = sharedPreferences.getString("username", "No Name")
        val email = sharedPreferences.getString("email", "No Email")

        // Set the TextViews with the retrieved values
        nameTextView.text = "Name: $username"
        emailTextView.text = "Email: $email"

        return binding
    }
}