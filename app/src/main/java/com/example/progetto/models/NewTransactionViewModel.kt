package com.example.progetto.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.progetto.database.Transactions
import com.example.progetto.repository.CoinsRepository
import kotlinx.coroutines.launch

class NewTransactionViewModel(val coinsRepository: CoinsRepository): ViewModel() {

    //Elenco di tutti i ticker disponibili
    private val _tickers: LiveData<List<String>> = coinsRepository.getAllTickers().asLiveData()
    val tickers: LiveData<List<String>>
        get() = _tickers

    //Validazione degli input e aggiunta delle transazioni nel database
    fun validateInput(type: Int, amount: String, coin1: String, coin2: String, price: String, commission: String, commissionTicker: String, modifyId: Long ): String?{

        val validTickers = _tickers.value!! + listOf("EUR")
        if (coin1 !in validTickers){
            return "Ticker non valido"
        }
        if ((type == 0 || type == 1) && (coin2 !in validTickers || commissionTicker !in validTickers)){
            return "Ticker non valido"
        }
        if (amount.isEmpty()){
            return "Fill all the fields"
        }
        val fAmount = amount.toFloat()

        if ((type == 0 || type == 1) && (price.isEmpty() || commission.isEmpty())){
            return "Fill all the fields"
        }
        if ((type == 0 || type == 1) && (coin1 == coin2)){
            return "You can't exchange a coin for the same coin"
        }

        var fPrice = 0.0f
        var fCommission = 0.0f
        if ((type == 0 || type == 1)){
            fPrice = price.toFloat()
            fCommission = commission.toFloat()
        }

        if (fAmount <= 0){
            return "Negative balance"
        }
        if ((type == 0 || type == 1) && (fPrice < 0 || fCommission < 0)){
            return  "Negative balance"
        }

        insertTransaction(type, fAmount, coin1, coin2, fPrice, fCommission, commissionTicker, modifyId)

        return null
    }


    fun insertTransaction(type: Int, amount: Float, coin1: String, coin2: String, price: Float, commission: Float, commissionTicker: String, modifyId: Long) {
        viewModelScope.launch{
            var data = System.currentTimeMillis() / 1000L
            if(modifyId > 0){
                val trans = coinsRepository.getById(modifyId)
                data = trans[0].date
                coinsRepository.deleteById(modifyId)
            }
            val transactions: MutableList<Transactions> = mutableListOf()
            val finalAmount = if (type == 1 || type == 2)  -amount else amount
            transactions.add(Transactions(date = data, type = type, ticker = coin1, amount = finalAmount, commissionTicker = commissionTicker, commission = commission, from = "manual"));
            when (type){
                0 -> transactions.add(Transactions(date = data, type = 1, ticker = coin2, amount = -amount*price, commissionTicker = "EUR", commission = 0.0f, from = "manual"));

                1 -> transactions.add(Transactions(date = data, type = 0, ticker = coin2, amount = amount*price, commissionTicker = "EUR", commission = 0.0f, from = "manual"));

            }
            if (modifyId > 0){
                coinsRepository.insertTransactionsWithSameId(transactions, modifyId)
            }else{
                coinsRepository.insertTransactionsWithSameId(transactions)
            }
        }
    }

    //Rimouve le transazioni per id
    fun deleteById(id: Long){
        viewModelScope.launch{
            coinsRepository.deleteById(id)
        }
    }

}

