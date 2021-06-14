package com.example.progetto.adapter


import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.progetto.MarketFragmentDirections
import com.example.progetto.R
import com.example.progetto.database.Coins
import java.text.NumberFormat
import java.util.*

//Adapter per la visualizzazione delle monete nella schermata Market
class marketAdapter(): RecyclerView.Adapter<marketAdapter.marketViewHolder>() {

    //Lista di tutte le monete da visualizzare
    var data =  listOf<Coins>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): marketViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.coin_details_item, parent, false)
        return marketViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: marketViewHolder, position: Int) {
        val currentItem = data[position]
        val nf = NumberFormat.getCurrencyInstance(Locale.ITALY)
        val pf = NumberFormat.getInstance()
        val rank = currentItem.rank.toString()
        val name = currentItem.name.capitalize(Locale.ROOT)
        val displayName = "$rank. $name"

        holder.coin_name.text = displayName
        holder.price.text = if (currentItem.price != null) nf.format(currentItem.price) else "und"
        holder.marketCap.text = if (currentItem.marketCap != null) nf.format(currentItem.marketCap) else "und"
        holder.percentage.text = if (currentItem.change24h != null) pf.format(currentItem.change24h).plus("%") else "und"

        //Colorazione condizionale delle percentuali a seconda del loro segno
        if (currentItem.change24h != null && currentItem.change24h!!.toFloat() > 0){
            holder.percentage.setTextColor(Color.GREEN)
        }else if (currentItem.change24h != null && currentItem.change24h!!.toFloat() < 0){
            holder.percentage.setTextColor(Color.RED)
        }

        Glide.with(holder.image.context).load(currentItem.image).into(holder.image)

        //listener per il click di una singola riga che porta all schermata con più dettagli
        holder.itemView.setOnClickListener {
            v -> v.findNavController().navigate(MarketFragmentDirections.actionMarketFragmentToCoinDisplayFragment2(currentItem.ticker))
        }
    }

    override fun getItemCount() = data.size

    class marketViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val coin_name: TextView = itemView.findViewById(R.id.coin_name)
        val price: TextView = itemView.findViewById(R.id.topRight)
        val image: ImageView = itemView.findViewById(R.id.coinImageDetails)
        val percentage: TextView = itemView.findViewById(R.id.bottomRight)
        val marketCap: TextView = itemView.findViewById(R.id.bottomLeft)

    }
}