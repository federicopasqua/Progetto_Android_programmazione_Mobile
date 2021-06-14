package com.example.progetto.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.progetto.database.Connections
import com.example.progetto.repository.CoinsRepository
import kotlinx.coroutines.launch

class addExchangeViewModel(val coinsRepository: CoinsRepository): ViewModel() {


    //Aggiungere una connessione
    fun insert(conncetions:Connections){
        viewModelScope.launch {
            coinsRepository.insertConnection(conncetions)
        }
    }

    //Get all the inserted coins
    suspend fun getCoins () : List<String> {
        return coinsRepository.getInsertedCoins()
    }

    //Rimuovere una connessione
    fun delete(id: Long){
        viewModelScope.launch {
            coinsRepository.deleteConnectionById(id)
        }
    }


}