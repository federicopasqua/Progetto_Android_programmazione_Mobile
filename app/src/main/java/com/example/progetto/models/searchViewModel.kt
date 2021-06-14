package com.example.progetto.models

import androidx.lifecycle.*
import com.example.progetto.database.Coins
import com.example.progetto.repository.CoinsRepository
import kotlinx.coroutines.launch

class searchViewModel(val coinsRepository: CoinsRepository): ViewModel() {

    //Lista delle monete che rispettano i criteri di ricerca
    private val _coins = MutableLiveData<List<Coins>>()
    val coins: LiveData<List<Coins>>
        get() = _coins

    //Numero della pagina a cui la lista è aggiornata
    private var _page = 1
    val page: Int
        get() = _page

    //Parametro che contiene la ricerca corrente
    private var _search = "";

    //Funzione che aggiorna il database (e di conseguenza la view) quando si cambia la stringa di ricerca
    fun updateResearch(search: String){
        viewModelScope.launch {
            _search = search
            if (search != ""){
                _coins.value = coinsRepository.searchByNameOrTicker(search)
                _page = 1
                coinsRepository.refreshListOfCoins(_coins.value!!.slice(0..minOf(50,_coins.value!!.size - 1)).map { coins ->  coins.name})
                _coins.value = coinsRepository.searchByNameOrTicker(search)
            }else{
                _coins.value = listOf()
            }
        }
    }

    //funzione per aggiornare il numero di pagina
    fun addPage(){
        _page++
    }

    //funzione che aggiorna la lista man a mano che l'utente scorre verso il basso
    fun updateCoins(){
        viewModelScope.launch {
            coinsRepository.refreshListOfCoins(_coins.value!!.slice(minOf((_page - 1) * 50, _coins.value!!.size)..minOf(_page * 50, _coins.value!!.size - 1)).map { coins ->  coins.name})
            _coins.value = coinsRepository.searchByNameOrTicker(_search)
        }
    }




}
