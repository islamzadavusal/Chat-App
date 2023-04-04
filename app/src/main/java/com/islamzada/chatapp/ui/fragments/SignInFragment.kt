package com.islamzada.chatapp.ui.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.islamzada.chatapp.R
import com.islamzada.chatapp.databinding.FragmentSignInBinding
import com.islamzada.chatapp.ui.activity.MainActivity

class SignInFragment : Fragment() {
    private lateinit var binding: FragmentSignInBinding

    private lateinit var mAuth: FirebaseAuth

    private lateinit var sharedPref: SharedPreferences

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSignInBinding.inflate(inflater, container, false)

        binding.goToSignUpFragment.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.toSignUp)
        }

        sharedPref = requireActivity().getSharedPreferences("auth_pref", Context.MODE_PRIVATE)

        mAuth = FirebaseAuth.getInstance()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.emailText.setText(sharedPref.getString("email", ""))
        binding.passwordText.setText(sharedPref.getString("password", ""))

        binding.btnSignIn.setOnClickListener {
            val email = binding.emailText.text.toString()
            val password = binding.passwordText.text.toString()
            val rememberMe = binding.checkBoxRemember.isChecked

            if (email.isNotEmpty() && password.isNotEmpty()) {
                if (rememberMe) {
                    with(sharedPref.edit()) {
                        putString("email", email)
                        putString("password", password)
                        apply()
                    }
                } else {
                    with(sharedPref.edit()) {
                        remove("email")
                        remove("password")
                        apply()
                    }
                }

                signin(email, password)
            } else {
                Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun signin(email: String, password: String) {
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val intent = Intent(requireActivity(), MainActivity::class.java)
                    requireActivity().finish()
                    startActivity(intent)
                } else {
                    val exception = task.exception
                    if (exception is FirebaseAuthInvalidUserException) {
                        Toast.makeText(requireContext(), "User does not exist", Toast.LENGTH_SHORT).show()
                    } else if (exception is FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(requireContext(), "Invalid email or password", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Authentication failed", Toast.LENGTH_SHORT).show()
                    }
            }
    }
}
}

