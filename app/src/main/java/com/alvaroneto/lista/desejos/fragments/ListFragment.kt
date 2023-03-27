package com.alvaroneto.lista.desejos.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.alvaroneto.lista.desejos.R

class ListFragment : Fragment() {

    lateinit var list: ListView
    lateinit var adapter: ArrayAdapter<String>
    val listItems = ArrayList<String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_list, container, false)

        // Get the ListView view from the layout
        list = view.findViewById<ListView>(R.id.listView)

        adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, listItems)
        list.adapter = adapter

        return view
    }
}
