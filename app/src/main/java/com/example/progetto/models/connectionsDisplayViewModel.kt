package com.example.progetto.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.progetto.database.Connections
import com.example.progetto.repository.CoinsRepository

class connectionsDisplayViewModel(val coinsRepository: CoinsRepository): ViewModel() {

    //Lista di tutte le connessioni attive
    private val _connections: LiveData<List<Connections>> = coinsRepository.getAllConnectionsAsLiveData().asLiveData()
    val connections: LiveData<List<Connections>>
        get() = _connections

}