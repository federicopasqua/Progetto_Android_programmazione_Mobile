package com.example.progetto

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.example.progetto.adapter.marketAdapter
import com.example.progetto.database.Coins
import com.example.progetto.databinding.FragmentMarketBinding
import com.example.progetto.databinding.FragmentSettingsBinding
import com.example.progetto.models.MarketViewModel
import com.example.progetto.repository.CoinsRepository

class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentSettingsBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_settings, container, false)


        binding.ConnectionsButton.setOnClickListener {
            binding.root.findNavController().navigate(R.id.action_settingsFragment_to_connectionsDisplayFragment)
        }


        return binding.root
    }
}