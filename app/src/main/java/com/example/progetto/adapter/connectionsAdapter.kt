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
import com.example.progetto.database.Connections

//Adapter per la visualizzazione delle connessioni attive nella schermata Connections
class connectionsAdapter(): RecyclerView.Adapter<connectionsAdapter.connectionViewHolder>() {

    //Lista delle connessioni attive
    var connections: List<Connections> =  listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): connectionViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.connections_item, parent, false)
        return connectionViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: connectionViewHolder, position: Int) {
        val currentItem = connections[position]
        holder.type.text = currentItem.name

        /*
        Scriviamo la key solo in caso non si tratti di Binance per evitare di visualizzare
        dati sensibili
        */
        if (currentItem.name != "binance"){
            holder.name.text = currentItem.key
        }

        val bundle = Bundle()

        //Listener per il click di ogni singola riga che porta alla schermata di modifica/eliminazione
        bundle.putLong("modifyId", currentItem.id)
        if (currentItem.name == "binance"){
            bundle.putString("exchange", "binance")
            holder.itemView.setOnClickListener {
                    v ->  v.findNavController().navigate(R.id.action_connectionsDisplayFragment_to_addExchangeFragment, bundle)
            }
        }else{
            bundle.putString("address", currentItem.key)
            bundle.putString("coin", currentItem.name)
            holder.itemView.setOnClickListener {
                    v ->  v.findNavController().navigate(R.id.action_connectionsDisplayFragment_to_addWalletFragment, bundle)
            }
        }

    }

    override fun getItemCount() = connections.size

    class connectionViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val type: TextView = itemView.findViewById(R.id.connectionType)
        val name: TextView = itemView.findViewById(R.id.connectionName)


    }
}
