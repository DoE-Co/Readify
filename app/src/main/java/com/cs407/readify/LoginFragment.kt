package com.cs407.readify

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.security.MessageDigest

class LoginFragment(
    private val injectedUserViewModel: UserViewModel? = null
) : Fragment() {

    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var createAccountButton: Button
    private lateinit var errorTextView: TextView

    private lateinit var userViewModel: UserViewModel
    private lateinit var userPasswdKV: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        usernameEditText = view.findViewById(R.id.username_input)
        passwordEditText = view.findViewById(R.id.password_input)
        loginButton = view.findViewById(R.id.login_button)
        createAccountButton = view.findViewById(R.id.navigate_to_create_account_button)
        errorTextView = view.findViewById(R.id.errorTextView)

        userViewModel = injectedUserViewModel ?: ViewModelProvider(requireActivity())[UserViewModel::class.java]

        // Initialize SharedPreferences
        userPasswdKV = requireActivity().getSharedPreferences(getString(R.string.userPasswdKV), Context.MODE_PRIVATE)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Hide error message when user types in any field
        usernameEditText.doAfterTextChanged { errorTextView.visibility = View.GONE }
        passwordEditText.doAfterTextChanged { errorTextView.visibility = View.GONE }

        // Set the login button click action
        loginButton.setOnClickListener {
            val name = usernameEditText.text.toString()
            val passwd = passwordEditText.text.toString()

            if (name.isNotBlank() && passwd.isNotBlank()) {
                viewLifecycleOwner.lifecycleScope.launch {
                    val success = getUserPasswd(name, passwd)
                    if (success) {
                        userViewModel.setUser(UserState(0, name, passwd))
                        withContext(Dispatchers.Main) {
                            findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                        }
                    }
                }
            } else {
                errorTextView.text = "All fields are required."
                errorTextView.visibility = View.VISIBLE
            }
        }

        createAccountButton.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_createAccountFragment)
        }
    }

    private suspend fun getUserPasswd(
        name: String,
        passwdPlain: String
    ): Boolean {
        // Log SharedPreferences keys
        val allUsers = userPasswdKV.all
        Log.d("LoginFragment", "Existing users: $allUsers")

        val passwd = hash(passwdPlain)
        if (userPasswdKV.contains(name)) {
            val passwdInKV = userPasswdKV.getString(name, null)
            if (passwd != passwdInKV) {
                withContext(Dispatchers.Main) {
                    errorTextView.text = getString(R.string.error_invalid_login)
                    errorTextView.visibility = View.VISIBLE
                }
                return false
            }
        } else {
            withContext(Dispatchers.Main) {
                errorTextView.text = getString(R.string.error_user_not_exist)
                errorTextView.visibility = View.VISIBLE
            }
            return false
        }
        Log.d("LoginFragment", "User logged in: $name")
        return true
    }

    private fun hash(input: String): String {
        return MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
            .joinToString("") { "%02x".format(it) }
    }
}