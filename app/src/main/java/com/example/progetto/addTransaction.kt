package com.example.progetto

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.example.progetto.databinding.FragmentAddTransactionBinding

class addTransaction : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentAddTransactionBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_add_transaction, container, false)



        binding.AddNewWalletButton.setOnClickListener {
            binding.root.findNavController().navigate(R.id.action_addTransaction_to_chooseWalletFragment)
        }

        binding.addNewExchangeButton.setOnClickListener {
            binding.root.findNavController().navigate(R.id.action_addTransaction_to_chooseExchangeFragment)
        }

        binding.addTransactionButton.setOnClickListener {
            binding.root.findNavController().navigate(R.id.action_addTransaction_to_searchFragment)

        }

        return binding.root
    }

}