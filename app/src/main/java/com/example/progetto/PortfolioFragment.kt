package com.example.progetto

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.example.progetto.adapter.portfolioAdapter
import com.example.progetto.database.coinsWithBalance
import com.example.progetto.databinding.FragmentPortfolioBinding
import com.example.progetto.models.PortfolioViewModel
import com.example.progetto.repository.CoinsRepository
import java.text.NumberFormat
import java.util.*

class PortfolioFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentPortfolioBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_portfolio, container, false)

        val application = requireNotNull(this.activity).application
        val repository = CoinsRepository(application)
        val portfolioModel = PortfolioViewModel(repository)
        binding.portfolioModelView = portfolioModel
        binding.lifecycleOwner = this
        val adapter = portfolioAdapter()
        binding.showOwned.adapter = adapter

        val nf = NumberFormat.getCurrencyInstance(Locale.ITALY)
        val pf = NumberFormat.getInstance()

        var timeframe1h = 0.0f
        var timeframe24h = 0.0f
        var timeframe7d = 0.0f
        var timeframe = "1h"

        //Observer che aggiorna la lista di coin inserite e il totale mostrato in alto
        portfolioModel.coins.observe(viewLifecycleOwner, {
            adapter.data = it
            var total = 0.0f

            //Calcolo delle percentuali di guadagno/perdita
            var totaltimeframe1h = 0.0f
            var totaltimeframe24h = 0.0f
            var totaltimeframe7d = 0.0f
            for (coin in it){
                if (coin.price != null){
                    total += (coin.balance*coin.price!!).toFloat()
                    if (coin.change1h != null)
                    totaltimeframe1h += ((coin.balance*coin.price!!).toFloat())*  coin.change1h!!.toFloat()
                    if (coin.change24h != null)
                    totaltimeframe24h += (coin.balance*coin.price!!).toFloat()*  coin.change24h!!.toFloat()
                    if (coin.change7d != null)
                    totaltimeframe7d += (coin.balance*coin.price!!).toFloat()*  coin.change7d!!.toFloat()

                }
            }
            timeframe1h = totaltimeframe1h/total
            timeframe24h = totaltimeframe24h/total
            timeframe7d = totaltimeframe7d/total

            //Assegnazione del timeframe giusto in base alla scelta dell'utente
            val displayPercentage: Float = when (timeframe) {
                "1h" -> timeframe1h
                "24h" -> timeframe24h
                else -> timeframe7d
            }
            binding.gains.text = pf.format(displayPercentage).plus("%")
            if (displayPercentage > 0){
                binding.gains.setTextColor(Color.GREEN)
            }else if (displayPercentage < 0){
                binding.gains.setTextColor(Color.RED)
            }

            binding.currentValue.text = nf.format(total)
        })

        binding.floatingActionButton2.setOnClickListener {
            binding.root.findNavController().navigate(R.id.action_portfolioFragment_to_addTransaction)
        }

        binding.searchButtonPortfolio.setOnClickListener {
            binding.root.findNavController().navigate(R.id.action_portfolioFragment_to_searchFragment)
        }

        //Listener dei bottoni per la scelta de timeframe
        binding.timeframe1hButton.setOnClickListener {
            binding.timeframe1hButton.isEnabled = false
            binding.timeframe24hButton.isEnabled = true
            binding.timeframe7dButton.isEnabled = true
            adapter.timeframe = "1h"
            timeframe = "1h"
            binding.gains.text = pf.format(timeframe1h).plus("%")
            if (timeframe1h > 0){
                binding.gains.setTextColor(Color.GREEN)
            }else if (timeframe1h < 0){
                binding.gains.setTextColor(Color.RED)
            }
        }

        binding.timeframe24hButton.setOnClickListener {
            binding.timeframe1hButton.isEnabled = true
            binding.timeframe24hButton.isEnabled = false
            binding.timeframe7dButton.isEnabled = true
            adapter.timeframe = "24h"
            timeframe = "27h"
            binding.gains.text = pf.format(timeframe24h).plus("%")
            if (timeframe24h > 0){
                binding.gains.setTextColor(Color.GREEN)
            }else if (timeframe24h < 0){
                binding.gains.setTextColor(Color.RED)
            }
        }

        binding.timeframe7dButton.setOnClickListener {
            binding.timeframe1hButton.isEnabled = true
            binding.timeframe24hButton.isEnabled = true
            binding.timeframe7dButton.isEnabled = false
            adapter.timeframe = "7d"
            timeframe = "7d"
            binding.gains.text = pf.format(timeframe7d).plus("%")
            if (timeframe7d > 0){
                binding.gains.setTextColor(Color.GREEN)
            }else if (timeframe7d < 0){
                binding.gains.setTextColor(Color.RED)
            }
        }

        return binding.root
    }


}