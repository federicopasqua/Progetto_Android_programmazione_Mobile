package com.example.progetto

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.example.progetto.databinding.FragmentChooseExchangeBinding


class chooseExchangeFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentChooseExchangeBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_choose_exchange, container, false)

        binding.addBinanceButton.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("exchange", "binance")
            binding.root.findNavController().navigate(R.id.action_chooseExchangeFragment_to_addExchangeFragment, bundle)
        }



        return binding.root
    }
}