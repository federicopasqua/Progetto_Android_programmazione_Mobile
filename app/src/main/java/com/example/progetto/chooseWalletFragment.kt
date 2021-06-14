package com.example.progetto

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.example.progetto.databinding.FragmentAddTransactionBinding
import com.example.progetto.databinding.FragmentChooseWalletBinding

class chooseWalletFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: FragmentChooseWalletBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_choose_wallet, container, false)

        binding.addBitcoinButton.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("coin", "bitcoin")
            bundle.putString("address", null)
            binding.root.findNavController().navigate(R.id.action_chooseWalletFragment_to_addWalletFragment, bundle)
        }

        binding.addArkButton.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("coin", "ark")
            bundle.putString("address", null)
            binding.root.findNavController().navigate(R.id.action_chooseWalletFragment_to_addWalletFragment, bundle)
        }




        return binding.root
    }
}