package com.example.progetto.network

import android.content.Context
import android.os.SystemClock.sleep
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.progetto.database.Transactions
import com.example.progetto.repository.CoinsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BitcoinWorker(private val appContext: Context, private val workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        withContext(Dispatchers.IO) {
            val address = inputData.getString("address")
            //Timestamp dell'ultima transazione inserita
            val lastUpdate = inputData.getLong("last_update", -1)

            val coinRepository = CoinsRepository(appContext)

            if (address == null){
                return@withContext
            }
            var i = 0
            do {
                val transactions = BitcoinApi.retrofitService.getTransactions(address,i*50)
                i ++
                for (tx in transactions.txs){
                    if (tx.doubleSpend) continue
                    //Controllo se la transazione è stata già inserita
                    if (lastUpdate > 0 && tx.time <= lastUpdate) continue

                    //Calcolo del totale della transazione
                    var input: Long = 0
                    var output: Long = 0
                    for (inp in tx.inputs){
                        if(address == inp.prevOut.addr){
                            input += inp.prevOut.value
                        }
                    }
                    for (inp in tx.out){
                        if(address == inp.addr){
                            output += inp.value
                        }
                    }
                    val total: Float = -(input - output).toFloat()/100000000
                    var type = 3
                    if (total < 0) type = 2
                    coinRepository.insertTransactionsWithSameId(listOf(Transactions(date = tx.time, type = type, ticker = "btc", amount = total, commissionTicker = "btc", commission = 0.0f, from = address)))
                }
                //Sleep inserito per evitare il rate limiter
                sleep(11000)
            }
            while (transactions.txs.isNotEmpty())



        }
        return Result.success()
    }
}