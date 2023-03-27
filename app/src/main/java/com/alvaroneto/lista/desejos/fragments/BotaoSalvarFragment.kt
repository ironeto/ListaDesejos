package com.alvaroneto.lista.desejos.fragments

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.alvaroneto.lista.desejos.R

class BotaoSalvarFragment : Fragment() {

    lateinit var botao_salvar: Button
    private var delegate: BotaoSalvarClickDelegate? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_botao_salvar, container, false)

        // Get the button view from the layout
        val button = view.findViewById<Button>(R.id.my_button)
        button.setOnClickListener{
            delegate?.salvar()
        }
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BotaoSalvarClickDelegate) {
            delegate = context
        } else {
            throw RuntimeException("$context must implement MyDelegate")
        }
    }
}


