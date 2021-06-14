package com.example.progetto.network

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.progetto.database.Transactions
import com.example.progetto.repository.CoinsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

//Worker per inserire le transazioni da binance
class BinanceWorker(private val appContext: Context, private val workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        withContext(Dispatchers.IO) {
            val key = inputData.getString("key")
            val secret = inputData.getString("secret")
            //Timestamp dell'ultima transazione inserita
            val lastUpdate = inputData.getLong("last_update", -1)
            //Monete possedute (aggiunte per un problema intrinseco con le api di Binance spiegato sotto)
            val coins = inputData.getStringArray("coins")

            val coinRepository = CoinsRepository(appContext)

            val listCoins = coins?.toMutableList()
            if (key == null || secret == null)
                return@withContext
            var startTime: Long? = lastUpdate
            if (lastUpdate < 0){
                startTime = null
            }



            val binance = Binance(key, secret).retrofitService
            //Fetcha tutti i depositi fatti sull'exchange e li inserisce come transazioni di "Transfer In"
            val depositHistory = binance.depositHistory(startTime = startTime)
            for (deposit in depositHistory){
                coinRepository.insertTransactionsWithSameId(listOf(Transactions(date = deposit.insertTime.toLong(), type = 3, ticker = deposit.coin.toLowerCase(Locale.ITALY), amount = deposit.amount, commissionTicker = "EUR", commission = 0.0f, from = "binance")))

                //Si aggiungono, se già non ci sono, le monete depositate alla lista delle monete possedute
                if (listCoins !== null && deposit.coin !in listCoins){
                    listCoins.add(deposit.coin)
                }
            }
            //Fetcha tutti i prelievi fatti sull'exchange e li inserisce come transazioni di "Transfer Out"
            val withdrawHistory = binance.withdrawHistory(startTime = startTime)
            for (withdraw in withdrawHistory){
                //Conversione da timestamp a unix
                val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ITALY).parse(withdraw.applyTime)
                coinRepository.insertTransactionsWithSameId(listOf(Transactions(date = date!!.time, type = 2, ticker = withdraw.coin.toLowerCase(Locale.ITALY), amount = -withdraw.amount, commissionTicker = "EUR", commission = 0.0f, from = "binance")))

                //Si aggiungono, se già non ci sono, le monete prelevate alla lista delle monete possedute
                if (listCoins !== null && withdraw.coin !in listCoins){
                    listCoins.add(withdraw.coin)
                }
            }

            if (listCoins !== null){
                for (coin in listCoins){
                    try {
                        /*
                        * Visto che le api di Binance non permette di prendere tutti gli scambi fatti in una sola volta
                        * ma solo mercato per mercato, vengono scaricate sole le transazioni tra le coin possedute e
                        * BTC che sono comunque i mercati pincipali
                        * */
                        val trades = binance.trades(coin.toUpperCase(Locale.ITALY) + "BTC")
                        for (trade in trades){
                            if (trade.time < lastUpdate) continue
                            var type = 1
                            var amount = -trade.quantity
                            var btcAmount = trade.quoteQuantity
                            if (trade.isBuyer){
                                type = 0
                                amount = -amount
                                btcAmount = -btcAmount
                            }
                            coinRepository.insertTransactionsWithSameId(listOf(Transactions(date = trade.time, type = type, ticker = coin.toLowerCase(Locale.ITALY), amount = amount, commissionTicker = trade.commissionAsset, commission = trade.commission, from = "binance"),
                                                                                Transactions(date = trade.time, type = (type + 1) % 2, ticker = "btc", amount = btcAmount, commissionTicker = "EUR", commission = 0.0f,from = "binance")))



                        }

                    }catch (ex: retrofit2.HttpException){
                        //Questa eccezione è lanciata quando il mercato non esiste.Il catch serve solo a non fermare il worker.
                    }
                }
            }
        }
        return Result.success()
    }
}