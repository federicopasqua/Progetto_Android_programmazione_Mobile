package com.example.progetto.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.progetto.PortfolioFragmentDirections
import com.example.progetto.R
import com.example.progetto.database.coinsWithBalance
import java.text.NumberFormat
import java.util.*

//Adapter per la visualizzazione delle monete aggiunte nella schermata Portafoglio
class portfolioAdapter(): RecyclerView.Adapter<portfolioAdapter.portfolioViewHolder>() {

    //Lista di tutte le monete aggiunte e il relativo saldo
    var data: List<coinsWithBalance> =  listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    /*
    Variabile utilizzata per indicare il timeframe a cui fa riferimento la percentuale di crescita/perdita
    di una moneta. Questa variabile viene cambiata con la pressione dei bottoni nella schermata Portafoglio
    */
    var timeframe: String =  "1h"
        set(value) {
            field = value
            notifyDataSetChanged()
        }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): portfolioViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.coin_details_item, parent, false)
        return portfolioViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: portfolioViewHolder, position: Int) {
        val currentItem = data[position]
        val nf = NumberFormat.getCurrencyInstance(Locale.ITALY)
        val pf = NumberFormat.getInstance()
        val balance = pf.format(currentItem.balance)
        val price = if (currentItem.price != null) nf.format(currentItem.price) else "und"

        val displayBottomLeft = "$balance | $price"

        holder.coin_name.text = currentItem.name.capitalize(Locale.ROOT)
        holder.total.text =  if (currentItem.price != null) nf.format(currentItem.price?.times(currentItem.balance)) else "und"
        holder.amount.text =  displayBottomLeft

        //Logica che mostra la percentuale desiderata a seconda del bottone premuto
        val displayPercentage: Double? = when (timeframe) {
            "1h" -> currentItem.change1h
            "24h" -> currentItem.change24h
            else -> currentItem.change7d
        }
        if (displayPercentage != null){
            holder.percentage.text = pf.format(displayPercentage).plus("%")
            //Colorazione condizionale in base al segno della percentuale
            if (displayPercentage > 0){
                holder.percentage.setTextColor(Color.GREEN)
            }else if (displayPercentage < 0){
                holder.percentage.setTextColor(Color.RED)
            }
        }else{
            holder.percentage.text = "und"
        }

        Glide.with(holder.image.context).load(currentItem.image).into(holder.image)

        //listener per ogni singola riga che porta al dettaglio della singola moneta
        holder.itemView.setOnClickListener {
                v -> v.findNavController().navigate(PortfolioFragmentDirections.actionPortfolioFragmentToCoinDisplayFragment2(currentItem.ticker))
        }
    }

    override fun getItemCount() = data.size

    class portfolioViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val coin_name: TextView = itemView.findViewById(R.id.coin_name)
        val total: TextView = itemView.findViewById(R.id.topRight)
        val image: ImageView = itemView.findViewById(R.id.coinImageDetails)
        val amount: TextView = itemView.findViewById(R.id.bottomLeft)
        val percentage: TextView = itemView.findViewById(R.id.bottomRight)


    }
}