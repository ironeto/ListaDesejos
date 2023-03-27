package com.alvaroneto.lista.desejos.utils

import android.widget.EditText

class PasswordValidations {
    companion object {
        @JvmStatic
        fun notEmpty(etPassword: EditText, etConfirmPassword: EditText): Boolean {
            return etPassword.text.toString().trim().isNotEmpty() &&
                    etConfirmPassword.text.toString().trim().isNotEmpty()
        }

        @JvmStatic
        fun verifySizePassword(etPassword: EditText): Boolean {
            return etPassword.text.toString().trim().length >= 6
        }

        @JvmStatic
        fun verifyIdenticalPassword(etPassword : EditText, etConfirmPassword: EditText): Boolean = etPassword.text.toString().trim() == etConfirmPassword.text.toString().trim()

    }
}
