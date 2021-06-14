package com.example.progetto.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.progetto.R
import com.example.progetto.database.Coins
import java.util.*

//Adapter per la visualizzazione delle monete corrispondenti alla ricerca nella schermata Search
class searchAdapter(): RecyclerView.Adapter<searchAdapter.searchViewHolder>() {

    //Lista delle monete che rispettano i criteri di ricerca
    var data =  listOf<Coins>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): searchViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.coin_details_item, parent, false)
        return searchViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: searchViewHolder, position: Int) {
        val currentItem = data[position]
        val rank = currentItem.rank.toString()
        val name = currentItem.name.capitalize(Locale.ROOT)
        val displayName = "$rank. $name"

        holder.coinName.text = displayName
        Glide.with(holder.image.context).load(currentItem.image).into(holder.image)

        //Listener per ogni riga che porta al dettaglio della singola moneta
        val bundle = Bundle()
        bundle.putString("ticker",currentItem.ticker)
        holder.itemView.setOnClickListener {
                v -> v.findNavController().navigate(R.id.action_searchFragment_to_coin_display_fragment, bundle)
        }
    }

    override fun getItemCount() = data.size

    class searchViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val coinName: TextView = itemView.findViewById(R.id.coin_name)
        val price: TextView = itemView.findViewById(R.id.topRight)
        val image: ImageView = itemView.findViewById(R.id.coinImageDetails)


    }
}