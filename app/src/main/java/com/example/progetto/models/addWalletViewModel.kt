package com.example.progetto.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.progetto.database.Connections
import com.example.progetto.repository.CoinsRepository
import kotlinx.coroutines.launch


class addWalletViewModel(val coinsRepository: CoinsRepository): ViewModel() {

    //Insert Connection
    fun insert(conncetions: Connections){
        viewModelScope.launch {
            coinsRepository.insertConnection(conncetions)
        }
    }

    //Delete Connection
    fun delete(id: Long){
        viewModelScope.launch {
            coinsRepository.deleteConnectionById(id)
        }
    }



}