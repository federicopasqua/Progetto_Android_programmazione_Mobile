package com.example.progetto.models

import androidx.lifecycle.*
import com.example.progetto.database.coinsWithBalance
import com.example.progetto.repository.CoinsRepository

class PortfolioViewModel(val coinsRepository: CoinsRepository): ViewModel() {

    //Lista delle monete aggiunte con il rispettivo saldo
    private val _coins: LiveData<List<coinsWithBalance>> = coinsRepository.selectCoinsWithBalance().asLiveData()
    val coins: LiveData<List<coinsWithBalance>>
        get() = _coins

}