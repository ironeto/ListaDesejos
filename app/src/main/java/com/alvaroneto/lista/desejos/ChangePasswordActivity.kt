package com.alvaroneto.lista.desejos

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class ChangePasswordActivity : AppCompatActivity() {
    lateinit var etPassword: EditText
    lateinit var etConfirmPassword: EditText
    val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        etPassword = findViewById<EditText>(R.id.etPassword)
        etConfirmPassword = findViewById<EditText>(R.id.etConfirmPassword)


        findViewById<View>(R.id.btnChangePassword).setOnClickListener {
            changePassword()
        }
    }

    fun changePassword(){
        if(PasswordValidations.notEmpty(etPassword, etConfirmPassword)) {
            if (PasswordValidations.verifySizePassword(etPassword)) {
                if (PasswordValidations.verifyIdenticalPassword(etPassword,etConfirmPassword)) {
                    val user = firebaseAuth.currentUser

                    user!!.updatePassword(etPassword.text.toString().trim())
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(this, R.string.senha_alterada, Toast.LENGTH_SHORT).show()
                            }
                        }

                    val activity = Intent(this, ProfileActivity::class.java);
                    startActivity(activity)
                    finish()
                } else {
                    Toast.makeText(this, R.string.senhas_nao_conferem, Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this, R.string.senha_menor_que_6, Toast.LENGTH_SHORT).show()
            }
        }else{
            Toast.makeText(this, R.string.preencha_todos_os_campos, Toast.LENGTH_SHORT).show()
        }
    }
}