package com.example.progetto.network

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.progetto.database.Transactions
import com.example.progetto.repository.CoinsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

//Worker che aggiorna le transazioni riguardanti i wallet della moneta ARK
class ArkWorker(private val appContext: Context, private val workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        withContext(Dispatchers.IO) {
            val address = inputData.getString("address")
            //timestamp dell'ultima transazione inserita
            val lastUpdate = inputData.getLong("last_update", -1)

            val coinRepository = CoinsRepository(appContext)

            if (address == null){
                return@withContext
            }
            val transactions = ArkApi.retrofitService.getTransactions(address)
            for (tx in transactions.results){
                //Controlla che la transazione non sia stata già aggiunta
                if (lastUpdate > 0 && tx.timestamp.unix <= lastUpdate) continue
                //Inserimento delle transazioni
                var amount = tx.amount
                var type = 3
                if (tx.typeGroup == 1 && tx.type == 0){
                    if (tx.sender == address){
                        amount = -amount
                        type = 2
                    }
                }else if (tx.typeGroup == 1 && tx.type == 6){
                    amount = 0
                    if (tx.sender == address) {
                        for (trans in tx.asset?.payments!!) {
                            if (trans.recipientId !== address) {
                                amount -= trans.amount

                            }
                        }
                        type = 2
                    }else{
                        for (trans in tx.asset?.payments!!) {
                            if (trans.recipientId == address){
                                amount += trans.amount
                            }
                        }
                    }
                }else{
                    continue
                }

                val total = amount.toFloat()/100000000
                coinRepository.insertTransactionsWithSameId(listOf(Transactions(date = tx.timestamp.unix, type = type, ticker = "ark", amount = total, commissionTicker = "EUR", commission = 0.0f, from = address)))
            }

        }
        return Result.success()
    }
}