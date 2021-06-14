package com.example.progetto.models


import androidx.lifecycle.*
import com.example.progetto.database.Coins
import com.example.progetto.repository.CoinsRepository
import kotlinx.coroutines.launch

class MarketViewModel(val coinsRepository: CoinsRepository): ViewModel() {

    //Lista di tutte le monete da visualizzare
    private val _coins: LiveData<List<Coins>> = coinsRepository.getCoins().asLiveData()
    val coins: LiveData<List<Coins>>
        get() = _coins

    /*
    Metodo per aggiornare delle monete in base alla pagina in cui si trovano.
    Usato per aggiornare la lista man mano che l'utente scorre la lista
    */
    fun updateCoins(page: Int){
        viewModelScope.launch{
            coinsRepository.refreshCoins(page)
        }
    }
}
