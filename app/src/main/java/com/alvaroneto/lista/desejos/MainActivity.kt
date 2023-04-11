package com.alvaroneto.lista.desejos

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.alvaroneto.lista.desejos.fragments.ListFragment
import com.alvaroneto.lista.desejos.fragments.SenhaDificuldade
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.alvaroneto.lista.desejos.fragments.WeatherFragments
import com.google.firebase.messaging.FirebaseMessaging
import android.Manifest


class MainActivity : AppCompatActivity() {
    lateinit var mGoogleSignClient: GoogleSignInClient;

    val uid = FirebaseAuth.getInstance().currentUser?.uid
    val ref = FirebaseDatabase.getInstance().getReference("/users/$uid/tasks")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                println("Token Push Notification: $token")
            } else {
                Toast.makeText(this, "Erro ao inicializar as Notificações por Push", Toast.LENGTH_SHORT).show()
            }
        }

        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, WeatherFragments()).commit()

        supportActionBar?.hide()

        var listView = supportFragmentManager.findFragmentById(R.id.listViewTasks) as ListFragment

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build();
        mGoogleSignClient = GoogleSignIn.getClient(this, gso);

        val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance();
        val firebaseUser: FirebaseUser? = firebaseAuth.currentUser
        if(firebaseUser == null){
            val intent = Intent(this, LoginScreen::class.java);
            startActivity(intent);
        }

        findViewById<View>(R.id.logout).setOnClickListener{
            firebaseAuth.signOut();
            mGoogleSignClient.signOut();

            val activity = Intent(this, LoginScreen::class.java);
            startActivity(activity)
            finish()
        }

        findViewById<View>(R.id.profile).setOnClickListener{
            val activity = Intent(this, ProfileActivity::class.java);
            startActivity(activity)
            finish()
        }

        findViewById<View>(R.id.fab_add_task).setOnClickListener{
            val activity = Intent(this, TaskActivity::class.java);
            startActivity(activity)
        }

        findViewById<View>(R.id.mais_desejados).setOnClickListener{
            val activity = Intent(this, ItemsMaisDesejadosActivity::class.java);
            startActivity(activity)
        }

        findViewById<View>(R.id.mais_desejados).setOnClickListener{
            val activity = Intent(this, ItemsMaisDesejadosActivity::class.java);
            startActivity(activity)
        }

        ref.addValueEventListener(object: ValueEventListener {
            val ctx = this@MainActivity;

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                listView.listItems.clear()

                for(child in dataSnapshot.children){
                    listView.listItems.add(child.child("titulo").value.toString())
                }

                listView.adapter.notifyDataSetChanged()

                listView.list.setOnItemLongClickListener { parent, view, position, id ->
                    val itemId =  dataSnapshot.children.toList()[position].key

                    if(itemId != null){
                        AlertDialog.Builder(ctx)
                            .setTitle("Deletar item")
                            .setMessage("Deseja deletar a item?")
                            .setPositiveButton("Sim"){ dialog, which ->
                                ref.child(itemId).removeValue()
                                Toast.makeText(ctx, "Item deletado com sucesso", Toast.LENGTH_SHORT).show()
                            }
                            .setNegativeButton("Não"){ dialog, which ->
                                dialog.dismiss()
                            }
                            .show()
                    }

                    true
                }
                listView.list.setOnItemClickListener { parent, view, position, id ->
                    val itemId =  dataSnapshot.children.toList()[position].key

                    val activity = Intent(ctx, TaskActivity::class.java)
                    activity.putExtra("id", itemId)
                    startActivity(activity)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(ctx, "Erro ao carregar itens", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, register for FCM token
                    FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val token = task.result
                            // Use token for sending messages
                        } else {
                            // Handle token generation failure
                        }
                    }
                } else {
                    // Handle permission denial
                }
            }
        }
    }

    // Request permission
    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.RECEIVE_SMS),
            PERMISSIONS_REQUEST_CODE
        )
    }

    companion object {
        const val PERMISSIONS_REQUEST_CODE = 100
    }
}