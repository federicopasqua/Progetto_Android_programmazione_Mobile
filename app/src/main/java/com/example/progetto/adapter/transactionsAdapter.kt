package com.example.progetto.adapter

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.progetto.R
import com.example.progetto.database.Transactions
import com.example.progetto.database.coinsWithBalance
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.util.*
import kotlin.math.abs

//Adapter per la visualizzazione delle transazioni inserite nella schermata di dettaglio
class transactionsAdapter(): RecyclerView.Adapter<transactionsAdapter.transactionViewHolder>() {

    //Lista di tutte le transazioni della specifica moneta
    var data: List<Transactions> =  listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    //Informazioni della moneta
    var coin: coinsWithBalance =  coinsWithBalance(name = "", ticker = "", price = 0.0, change1h = null, null, null, null, null, high = null, low = null, supply = null, volume = null, balance = 0.0f)
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    //Lista di tutti i possibili tipi di transazione
    val types = listOf("BUY", "SELL", "OUT", "IN", "Mining", "Staking")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): transactionViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.transactions_details, parent, false)
        return transactionViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: transactionViewHolder, position: Int) {
        val currentItem = data[position]

        //Dichiarazione dei vari formatter
        val ef = NumberFormat.getCurrencyInstance(Locale.ITALY)
        val nf = NumberFormat.getCurrencyInstance()
        val dfs = DecimalFormatSymbols()
        dfs.currencySymbol = currentItem.ticker.toUpperCase(Locale.ITALY)
        dfs.groupingSeparator = '.'
        dfs.monetaryDecimalSeparator = '.'
        (nf as DecimalFormat).decimalFormatSymbols = dfs

        holder.type.text = types[currentItem.type]
        holder.quantita.text =  nf.format(abs(currentItem.amount))
        if (coin.price != null){
            holder.controvalore.text =  ef.format(abs(currentItem.amount*coin.price!!))
        }
        else{
            holder.controvalore.text = "€und"
        }

        //listener aggiunto ad ogni riga che permette di portare alla pagina di modifica transazione
        val bundle = Bundle()
        bundle.putLong("modifyId", currentItem.id)
        bundle.putString("buySellTicker", currentItem.ticker)
        holder.itemView.setOnClickListener {
                v ->  v.findNavController().navigate(R.id.action_coin_display_fragment_to_newTransactionFragment, bundle)
        }
    }

    override fun getItemCount() = data.size

    class transactionViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val type: TextView = itemView.findViewById(R.id.typeDisplay)
        val quantita: TextView = itemView.findViewById(R.id.transactionQuantityDisplay)
        val controvalore: TextView = itemView.findViewById(R.id.transactionValueDisplay)
    }
}