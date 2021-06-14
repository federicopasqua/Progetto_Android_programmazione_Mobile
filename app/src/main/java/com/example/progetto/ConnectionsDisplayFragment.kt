package com.example.progetto

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isEmpty
import androidx.databinding.DataBindingUtil
import com.example.progetto.adapter.connectionsAdapter
import com.example.progetto.databinding.FragmentConnectionsDisplayBinding
import com.example.progetto.models.connectionsDisplayViewModel
import com.example.progetto.repository.CoinsRepository


class ConnectionsDisplayFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: FragmentConnectionsDisplayBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_connections_display, container, false)

        val application = requireNotNull(this.activity).application
        val repository = CoinsRepository(application)

        val viewModel = connectionsDisplayViewModel(repository)
        binding.lifecycleOwner = this

        val adapter = connectionsAdapter()
        binding.connectionDisplay.adapter = adapter

        if (binding.connectionDisplay.isEmpty()){
            binding.emptyListViewText.visibility = View.VISIBLE
        }

        viewModel.connections.observe(viewLifecycleOwner, {
            adapter.connections = it
            if (it.isEmpty()){
                binding.emptyListViewText.visibility = View.VISIBLE
            }else{
                binding.emptyListViewText.visibility = View.GONE
            }
        })

        return binding.root
    }

}