package com.example.progetto.models

import androidx.lifecycle.*
import com.example.progetto.database.Transactions
import com.example.progetto.database.coinsWithBalance
import com.example.progetto.repository.CoinsRepository


class CoinDisplayViewModel(val ticker: String, val coinsRepository: CoinsRepository): ViewModel() {

    //Dettagli della moneta da mostrare
    private val _coin:LiveData<coinsWithBalance> = coinsRepository.selectCoinWithBalance(ticker).asLiveData()
    val coin: LiveData<coinsWithBalance>
        get() = _coin

    //Lista delle transazioni che riguradano la moneta
    private val _transactions:LiveData<List<Transactions>> = coinsRepository.getTransactions(ticker).asLiveData()
    val transaction: LiveData<List<Transactions>>
        get() = _transactions








}