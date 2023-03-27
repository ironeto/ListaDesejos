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

class ItemsMaisDesejadosActivity : AppCompatActivity() {
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    val ref = FirebaseDatabase.getInstance().getReference("/more_wished_items")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_items_mais_desejados)

        var listView = supportFragmentManager.findFragmentById(R.id.listViewTasks) as ListFragment

        ref.addValueEventListener(object: ValueEventListener {
            val ctx = this@ItemsMaisDesejadosActivity;

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                listView.listItems.clear()

                for(child in dataSnapshot.children){
                    listView.listItems.add(child.child("titulo").value.toString())
                }

                listView.adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(ctx, "Erro ao carregar itens", Toast.LENGTH_SHORT).show()
            }
        })
    }
}