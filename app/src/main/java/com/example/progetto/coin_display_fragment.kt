    package com.example.progetto

import android.annotation.SuppressLint
import android.icu.text.CompactDecimalFormat
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.progetto.adapter.transactionsAdapter
import com.example.progetto.databinding.FragmentCoinDisplayFragmentBinding
import com.example.progetto.models.CoinDisplayViewModel
import com.example.progetto.repository.CoinsRepository
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.util.*
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow

class coin_display_fragment() : Fragment() {

    val args: coin_display_fragmentArgs by navArgs()

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentCoinDisplayFragmentBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_coin_display_fragment, container, false)

        binding.lifecycleOwner = this
        val application = requireNotNull(this.activity).application
        val repository = CoinsRepository(application)

        val ticker = args.ticker

        val coinDisplayViewModel = CoinDisplayViewModel(ticker, repository)
        binding.coinDisplayViewModel = coinDisplayViewModel
        val adapter = transactionsAdapter()
        binding.showTransactions.adapter = adapter

        val ef = NumberFormat.getCurrencyInstance(Locale.ITALY)

        coinDisplayViewModel.transaction.observe(viewLifecycleOwner, {
            adapter.data = it
        })
        val image = binding.coinImageDetails

        coinDisplayViewModel.coin.observe(viewLifecycleOwner, {
            adapter.coin = it
            Glide.with(image.context).load(it.image).into(image)
        })

        val bundle = Bundle()
        bundle.putString("buySellTicker", coinDisplayViewModel.ticker)
        binding.floatingActionButton.setOnClickListener(){
            binding.root.findNavController().navigate(R.id.action_coin_display_fragment_to_newTransactionFragment, bundle)
        }

        coinDisplayViewModel.coin.observe(viewLifecycleOwner, {
            val cf = NumberFormat.getCurrencyInstance()
            val dfs = DecimalFormatSymbols()
            dfs.currencySymbol = it.ticker.toUpperCase(Locale.ITALY)
            dfs.groupingSeparator = '.'
            dfs.monetaryDecimalSeparator = '.'
            (cf as DecimalFormat).decimalFormatSymbols = dfs
            binding.displayBalanceDetails.text = cf.format(it.balance)
            binding.displayValueDetails.text =  if (it.price == null) "und" else ef.format(it.price?.toFloat()?.times(it.balance))

            binding.highDisplay.text =  if (it.high == null) "und" else ef.format(it.high)
            binding.lowDisplay.text =  if (it.low == null) "und" else ef.format(it.low)
            binding.priceDisplay.text =  if (it.price == null) "und" else ef.format(it.price)

            binding.marketcapDisplay.text =  if (it.marketCap == null) "und" else prettyCount(it.marketCap!!)
            binding.rankDisplay.text =  if (it.rank == null) "und" else it.rank.toString()
            binding.volumeDisplay.text =  if (it.volume == null) "und" else prettyCount(it.volume!!)
            binding.supplyDisplay.text =  if (it.supply == null) "und" else prettyCount(it.supply!!)
            binding.percentageDisplay.text =  if (it.change24h == null) "und" else NumberFormat.getInstance().format(it.change24h).plus("%")

        })



        //listener dei bottoni che cambiano la tab inferiore
        binding.displayDetails.setOnClickListener{
            binding.displayDetails.isEnabled = false
            binding.displayTransactions.isEnabled = true
            binding.floatingActionButton.hide()
            binding.showCoinDetails.visibility = View.VISIBLE
            binding.showTransactions.visibility = View.GONE
        }

        binding.displayTransactions.setOnClickListener{
            binding.displayTransactions.isEnabled = false
            binding.displayDetails .isEnabled = true
            binding.floatingActionButton.show()
            binding.showCoinDetails.visibility = View.GONE
            binding.showTransactions.visibility = View.VISIBLE

        }
        return binding.root
    }

        fun prettyCount(number: Number): String? {
            val suffix = charArrayOf(' ', 'k', 'M', 'B', 'T', 'P', 'E')
            val numValue = number.toLong()
            val value = floor(log10(numValue.toDouble())).toInt()
            val base = value / 3
            return if (value >= 3 && base < suffix.size) {
                DecimalFormat("#0.0").format(
                    numValue / 10.0.pow((base * 3).toDouble())
                ) + suffix[base]
            } else {
                DecimalFormat("#,##0").format(numValue)
            }
        }

}
