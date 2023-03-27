package com.alvaroneto.lista.desejos.utils

import android.widget.EditText
import com.alvaroneto.lista.desejos.fragments.SenhaDificuldade

class PasswordValidations {
    companion object {
        @JvmStatic
        fun notEmpty(etPassword: SenhaDificuldade, etConfirmPassword: EditText): Boolean {
            return etPassword.text.toString().trim().isNotEmpty() &&
                    etConfirmPassword.text.toString().trim().isNotEmpty()
        }

        @JvmStatic
        fun verifySizePassword(etPassword: SenhaDificuldade): Boolean {
            return etPassword.text.toString().trim().length >= 6
        }

        @JvmStatic
        fun verifyIdenticalPassword(etPassword : SenhaDificuldade, etConfirmPassword: EditText): Boolean = etPassword.text.toString().trim() == etConfirmPassword.text.toString().trim()

    }
}
