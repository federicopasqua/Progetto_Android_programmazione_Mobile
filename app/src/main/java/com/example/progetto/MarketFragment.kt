package com.example.progetto

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.progetto.adapter.marketAdapter
import com.example.progetto.database.Coins
import com.example.progetto.databinding.FragmentMarketBinding
import com.example.progetto.models.MarketViewModel
import com.example.progetto.repository.CoinsRepository

class MarketFragment : Fragment() {



    val viewModel: MarketViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: FragmentMarketBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_market, container, false)

        val application = requireNotNull(this.activity).application
        val repository = CoinsRepository(application)
        val marketModel = MarketViewModel(repository)

        binding.marketModelView = marketModel
        binding.lifecycleOwner = this

        val adapter = marketAdapter()
        binding.coinList.adapter = adapter

        marketModel.coins.observe(viewLifecycleOwner, {
            adapter.data = it
        })

        binding.searchButtonMarket.setOnClickListener {
            binding.root.findNavController().navigate(R.id.action_marketFragment_to_searchFragment)
        }

        //Listener che si occupa di aggiornare le monete man mano che l'utente scrolla
        binding.coinList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            var page = 1;
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if ((recyclerView.layoutManager  as LinearLayoutManager?)!!.findFirstCompletelyVisibleItemPosition() > (page-2)*50) {
                    page++
                    marketModel.updateCoins(page - 1)
                }
            }
        })

        return binding.root
       // return inflater.inflate(R.layout.fragment_market, container, false)
    }
}