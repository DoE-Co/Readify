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
//Add these when Database is created
//import com.cs407.readify.data.Database
//import com.cs407.readify.data.User
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
    //Add this when Database is created
    //private lateinit var database: Database

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        usernameEditText = view.findViewById(R.id.username_input)
        passwordEditText = view.findViewById(R.id.password_input)
        loginButton = view.findViewById(R.id.login_button)
        createAccountButton = view.findViewById(R.id.navigate_to_create_account_button)
        errorTextView = view.findViewById(R.id.errorTextView)

        userViewModel = if (injectedUserViewModel != null) {
            injectedUserViewModel
        } else {
            ViewModelProvider(requireActivity())[UserViewModel::class.java]
        }

        // Initialize SharedPreferences
        userPasswdKV = requireContext().getSharedPreferences(getString(R.string.userPasswdKV), Context.MODE_PRIVATE)

        // Initialize Room Database
        //Add this when Database is created
        //database = Database.getDatabase(requireContext())

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Hide error message when user types in either field
        usernameEditText.doAfterTextChanged {
            errorTextView.visibility = View.GONE
        }
        passwordEditText.doAfterTextChanged {
            errorTextView.visibility = View.GONE
        }

        // Set the login button click action
        loginButton.setOnClickListener {
            val name = usernameEditText.text.toString()
            val passwd = passwordEditText.text.toString()

            if (name.isNotBlank() && passwd.isNotBlank()) {
                viewLifecycleOwner.lifecycleScope.launch {
                    val success = withContext(Dispatchers.IO) {
                        getUserPasswd(name, passwd)
                    }
                    if (success) {
                        val userId = withContext(Dispatchers.IO) {
                            //Add this when Database is created
                             //val user = database.userDao().getByName(name)
                             //user.userId
                        }
                        // Set the logged-in user in the UserViewModel
                        //userViewModel.setUser(UserState(userId, name, passwd))

                        //findNavController().navigate(R.id.action_loginFragment_to_homefragment)
                    } else {
                        errorTextView.visibility = View.VISIBLE
                    }
                }
            } else {
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
        val passwd = hash(passwdPlain)
        if (userPasswdKV.contains(name)) {
            val passwdInKV = userPasswdKV.getString(name, null)
            errorTextView.text = getString(R.string.error_invalid_login)
            if (passwd != passwdInKV) return false
        } else {
            errorTextView.text = getString(R.string.error_user_not_exist)
            return false
        }
        return true
    }

    private fun hash(input: String): String {
        return MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
            .fold("") { str, it -> str + "%02x".format(it) }
    }
}