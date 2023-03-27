package com.alvaroneto.lista.desejos

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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

class MainActivity : AppCompatActivity() {
    lateinit var mGoogleSignClient: GoogleSignInClient;

    val uid = FirebaseAuth.getInstance().currentUser?.uid
    val ref = FirebaseDatabase.getInstance().getReference("/users/$uid/tasks")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
            finish()
        }

        findViewById<View>(R.id.mais_desejados).setOnClickListener{
            val activity = Intent(this, ItemsMaisDesejadosActivity::class.java);
            startActivity(activity)
            finish()
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
}