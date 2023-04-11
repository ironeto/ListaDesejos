package com.alvaroneto.lista.desejos.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.alvaroneto.lista.desejos.R

class EmailFragment : Fragment() {

    lateinit var emailEditText: EditText
    private lateinit var emailValidityTextView: TextView


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_email, container, false)

        emailEditText = view.findViewById(R.id.email_edit_text)
        emailValidityTextView = view.findViewById(R.id.email_validity_text_view)

        emailEditText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                validateEmail()
            } else {
                emailValidityTextView.visibility = View.INVISIBLE
            }
        }

        return view
    }

    private fun validateEmail() {
        val email = emailEditText.text.toString()

        if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailValidityTextView.visibility = View.VISIBLE
            emailValidityTextView.setTextColor(resources.getColor(android.R.color.holo_green_dark))
            emailValidityTextView.text = getString(R.string.valid_email)
        } else {
            emailValidityTextView.visibility = View.VISIBLE
            emailValidityTextView.setTextColor(resources.getColor(android.R.color.holo_red_dark))
            emailValidityTextView.text = getString(R.string.invalid_email)
        }
    }

    fun isValid(): Boolean {
        val email = emailEditText.text.toString()
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}