package com.alvaroneto.lista.desejos

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.alvaroneto.lista.desejos.fragments.EmailFragment
import com.alvaroneto.lista.desejos.utils.PasswordValidations
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.alvaroneto.lista.desejos.fragments.SenhaDificuldade
import com.google.firebase.crashlytics.FirebaseCrashlytics

class CreateAccount : AppCompatActivity() {
    lateinit var etEmail: EmailFragment
    lateinit var etPassword: SenhaDificuldade
    lateinit var etConfirmPassword: EditText
    lateinit var createAccountInputArray: Array<Any>
    lateinit var mGoogleSignInClient: GoogleSignInClient;
    private lateinit var firebaseAuth: FirebaseAuth;

    val Req_Code:Int=123;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)
        supportActionBar?.hide()

        mapComponents()
        initializeFirebase()
        createAccountInputArray = arrayOf(etEmail, etPassword, etConfirmPassword)

        initializeGoogle()


        findViewById<View>(R.id.loginView).setOnClickListener{
            val activity = Intent(this, LoginScreen::class.java);
            startActivity(activity);
        }

        findViewById<View>(R.id.signin).setOnClickListener {
            Toast.makeText(this, R.string.registro_com_google, Toast.LENGTH_SHORT).show()
            signInGoogle();
        }

        findViewById<View>(R.id.btnCreateAccount).setOnClickListener {
            signUp()
        }
    }

    private fun mapComponents(){
        etEmail = supportFragmentManager.findFragmentById(R.id.etEmail) as EmailFragment
        etPassword = supportFragmentManager.findFragmentById(R.id.etPassword) as SenhaDificuldade
        etConfirmPassword = findViewById<EditText>(R.id.etConfirmPassword)
    }

    private fun initializeFirebase(){
        FirebaseApp.initializeApp(this)
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
    }

    private fun initializeGoogle(){
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private fun signInGoogle(){
        val signIntent: Intent = mGoogleSignInClient.signInIntent;
        startActivityForResult(signIntent, Req_Code);
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == Req_Code){
            val result = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleResult(result);
        }
    }

    private fun handleResult(completedTask: Task<GoogleSignInAccount>){
        try{
            val account: GoogleSignInAccount? = completedTask.getResult(ApiException::class.java);

            if(account != null){
                Toast.makeText(this, "$account.displayName logado com sucesso!", Toast.LENGTH_SHORT).show();
                UpdateUser(account)
            }
            else
            {
                Toast.makeText(this,"Erro ao obter a conta", Toast.LENGTH_SHORT).show()
            }

        }
        catch (e: ApiException){
            println(e)
            Toast.makeText(this, "Falha ao logar", Toast.LENGTH_SHORT).show();
        }
    }

    private fun UpdateUser(account: GoogleSignInAccount){
        val credential = GoogleAuthProvider.getCredential(account.idToken, null);

        firebaseAuth.signInWithCredential(credential).addOnCompleteListener {task ->
            if(task.isSuccessful){
                val intent = Intent(this, MainActivity::class.java);
                startActivity(intent);
                finish()
            }
        }
    }

    fun verifyPassword(): Boolean {
        var isValid = false;
        if(PasswordValidations.notEmpty(etPassword,etConfirmPassword)){
            if(PasswordValidations.verifyIdenticalPassword(etPassword,etConfirmPassword)){
                if(PasswordValidations.verifySizePassword(etPassword)){
                    isValid = true
                }else{
                    Toast.makeText(this, "As senhas são iguais mas de tamanho inadequado.", Toast.LENGTH_SHORT).show();
                    isValid = false
                }
            }else{
                Toast.makeText(this, "As senhas não coincidem.", Toast.LENGTH_SHORT).show()
                isValid = false
            }
        }else{
            Toast.makeText(this, "Preencha todos os campos.", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        return isValid
    }

    private fun signUp(){
        if(!etEmail.isValid()){
            Toast.makeText(this, "Email invalido.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (verifyPassword()) {
            val userEmail = etEmail.emailEditText.text.toString().trim()
            val userPassword = etPassword.text.toString().trim()

            firebaseAuth.createUserWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener{task ->
                if(task.isSuccessful){
                    sendEmailVerification()
                    Toast.makeText(this, "Usuário $userEmail criado com sucesso. Verifique sua caixa de e-mail", Toast.LENGTH_SHORT).show();
                    finish()
                }else{
                    val exception = task.exception;
                    if(exception is FirebaseAuthException && exception.errorCode == "ERROR_EMAIL_ALREADY_IN_USE"){
                        Toast.makeText(this, "Email já cadastrado.", Toast.LENGTH_SHORT).show();
                    }else if(exception is FirebaseAuthException && exception.errorCode == "ERROR_WEAK_PASSWORD"){
                        Toast.makeText(this, "Senha fraca.", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(this, "Erro ao criar usuário.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    private fun sendEmailVerification(){
        val firebaseUser: FirebaseUser? = firebaseAuth.currentUser

        firebaseUser?.let {
            it.sendEmailVerification().addOnCompleteListener { task ->
                if(task.isSuccessful){
                    Toast.makeText(this, "E-mail enviado com sucesso", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}