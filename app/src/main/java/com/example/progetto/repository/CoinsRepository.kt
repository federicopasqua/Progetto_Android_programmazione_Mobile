package com.example.progetto.repository

import android.content.Context
import com.example.progetto.database.*
import com.example.progetto.network.CoingeckoApi
import com.example.progetto.network.CoingeckoImage
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Response
import java.util.*

class CoinsRepository(val context: Context) {
    private val CoinsDao = AppDatabase.getInstance(context).CoinsDatabaseDao
    private val TransactionsDao = AppDatabase.getInstance(context).TransactionsDatabaseDao
    private val ConnectionsDao = AppDatabase.getInstance(context).ConnectionsDao

    //COINSDAO

    //Ritorna tutte le monete salvate nella tabella Coins
    fun getCoins(): Flow<List<Coins>> {
        return CoinsDao.getAllCoins()
    }

    //Ritorna tutti i ticker di tutte le monete salvate nella tabella Coins
    fun getAllTickers(): Flow<List<String>> {
        return CoinsDao.getAllCoinsTicker()
    }

    //Ricerca monete che abbiano nel ticker o nome la stringa passata
    suspend fun searchByNameOrTicker(search: String) : List<Coins>{
        return CoinsDao.searchByNameOrTicker(search)
    }

    //TRANSACTIONSDAO

    //Inserimento di una transazione
    suspend fun insertTransaction(transaction: Transactions){
        TransactionsDao.insert(transaction)
    }

    //Ritorna tutte le monete con un saldo e fa una richiesta a coingecko per aggiornarne i dati
    fun selectCoinsWithBalance(): Flow<List<coinsWithBalance>>{
        GlobalScope.launch {
            updateCoinsWithBalance()
        }
        return TransactionsDao.selectCoinsWithBalance()
    }

    //Ritorna tutte le transazione per una certa moneta
    fun getTransactions(ticker: String): Flow<List<Transactions>>{
        return TransactionsDao.getTransactions(ticker)
    }

    //seleziona una singola moneta inserita e invia una richiesta a Coinegecko per aggiornarne i dati
    fun selectCoinWithBalance(ticker: String): Flow<coinsWithBalance>{
        GlobalScope.launch {
            updateCoinsWithBalance(ticker)
        }
        return TransactionsDao.selectCoinWithBalance(ticker)
    }

    //Elimina una transazione per id
    suspend fun deleteById(id: Long){
        TransactionsDao.deleteById(id)
    }

    //Ritorna le transazioni con l'id passato
    suspend fun getById(id: Long): List<Transactions>{
        return TransactionsDao.getById(id)
    }

    /*Inserisce la lista di transazioni passate tutte con lo stesso id
    * Le transazioni con lo stesso id sono associate ad una stessa operazione di acquisto o vendita.
    * */
    suspend fun insertTransactionsWithSameId(transactions: List<Transactions>, id: Long? = null){
        TransactionsDao.insertTransactionsWithSameId(transactions, id)
    }

    //Ritorna tutte le monete che sono state inserite
    suspend fun getInsertedCoins(): List<String>{
        return TransactionsDao.getInsertedCoins()
    }

    //Ritorna tutte le connessioni
    fun getAllConnections(): List<Connections>{
        return ConnectionsDao.getAllConnections()
    }

    //Elimina una connessione per id
    suspend fun deleteConnectionById(id: Long){
        ConnectionsDao.deleteConnectionById(id)
    }


    //Ritorna tutte le connessioni come LiveData
    fun getAllConnectionsAsLiveData(): Flow<List<Connections>>{
        return ConnectionsDao.getAllConnectionsAsLiveData()
    }

    //Inserisce una connessione
    suspend fun insertConnection(connection: Connections){
        ConnectionsDao.insert(connection)
    }

    //Ritorna una connessione con il nome passato
    suspend fun getConnectionByName(name: String) : List<Connections>{
        return ConnectionsDao.getConnectionByName(name)
    }

    suspend fun getConnectionByAddress(address: String) : List<Connections>{
        return ConnectionsDao.getConnectionByAddress(address)
    }


    //Aggiorna tutte le monete che sono state inserite. Questa funzione viene invocata quando si apre il Portfolio
    private suspend fun updateCoinsWithBalance(ticker: String? = null){
        val coins: List<String> =
            //Se viene passato un ticker, viene aggiornato una sola moneta, tutte quelle possedute altrimenti
            if (ticker == null)
            TransactionsDao.getInsertedCoinsCoingeckoIds()
            else
            listOf(CoinsDao.getCoinByTicker(ticker).name)

        try {

            val data = CoingeckoApi.retrofitService.getDetailedCoinList(
                ids = coins.joinToString(",").toLowerCase(Locale.ITALY)
            )

            val newCoins: MutableList<Coins> = mutableListOf()
            for (result in data) {
                //Viene preso dal db l'ultima volta che l'immagine è stata aggiornata
                val lastUpdateDb = CoinsDao.getLastUpdatedByTicker(result.symbol)
                var lastUpdate = "00"
                if (lastUpdateDb.isNotEmpty()) {
                    lastUpdate = lastUpdateDb[0]
                }

                //Si invia la richiesta per l'immagine con l'header If-Modified-Since
                val request = if (result.imgSrcUrl != null)
                    CoingeckoImage.imageRetrofitService.getImage(
                        result.imgSrcUrl.replace(
                            "/large/",
                            "/small/"
                        ), lastUpdate
                    )
                else
                    null
                //Se la richiesta ritorna 200, l'immagine non è aggiornata e si provvede a farlo
                if (request != null && request.code() == 200) {
                    val lastmodified = request.headers().get("last-modified")
                    val image = request.body()?.bytes()
                    newCoins.add(
                        Coins(
                            name = result.id,
                            ticker = result.symbol,
                            price = result.current_price,
                            change1h = result.change1h,
                            change24h = result.change24h,
                            change7d = result.change7d,
                            marketCap = result.market_cap,
                            volume = result.total_volume,
                            rank = result.market_cap_rank,
                            high = result.high_24h,
                            low = result.low_24h,
                            supply = result.circulating_supply,
                            lastModified = lastmodified,
                            image = image
                        )
                    )
                }
                //Se la richiesta ritorna 304, l'immagine è aggiornata. Si aggiorna solo il resto
                else {
                    newCoins.add(
                        Coins(
                            name = result.id,
                            ticker = result.symbol,
                            price = result.current_price,
                            change1h = result.change1h,
                            change24h = result.change24h,
                            change7d = result.change7d,
                            marketCap = result.market_cap,
                            volume = result.total_volume,
                            rank = result.market_cap_rank,
                            high = result.high_24h,
                            low = result.low_24h,
                            supply = result.circulating_supply
                        )
                    )
                }
            }
            CoinsDao.insertAndDeleteInTransaction(newCoins)
        }catch (e: Exception){
            //
        }
    }

    //Aggiorna le coin per pgaina
    suspend fun refreshCoins(page: Int) {
        try {
            val listResult = CoingeckoApi.retrofitService.getDetailedCoinList(page = page)
            if(listResult.isEmpty()){
                return
            }
            val newCoins: MutableList<Coins> = mutableListOf()
            for (result in listResult){
                val lastUpdateDb = CoinsDao.getLastUpdatedByTicker(result.symbol)
                var lastUpdate = "00"
                if (lastUpdateDb.isNotEmpty()) {
                    lastUpdate = lastUpdateDb[0]
                }

                //Si invia la richiesta per l'immagine con l'header If-Modified-Since
                val request = if (result.imgSrcUrl != null)
                    CoingeckoImage.imageRetrofitService.getImage(result.imgSrcUrl.replace("/large/", "/small/"), lastUpdate)
                else
                    null
                //Se la richiesta ritorna 200, l'immagine non è aggiornata e si provvede a farlo
                if (request != null && request.code() == 200){
                    val lastmodified = request.headers().get("last-modified")
                    val image = request.body()?.bytes()
                    newCoins.add(Coins(name = result.id, ticker = result.symbol, price = result.current_price, change1h = result.change1h, change24h = result.change24h, change7d = result.change7d, marketCap = result.market_cap, volume = result.total_volume, rank = result.market_cap_rank, high = result.high_24h, low = result.low_24h, supply = result.circulating_supply, lastModified = lastmodified, image = image))
                }
                //Se la richiesta ritorna 304, l'immagine è aggiornata. Si aggiorna solo il resto
                else{
                    newCoins.add(Coins(name = result.id, ticker = result.symbol, price = result.current_price, change1h = result.change1h, change24h = result.change24h, change7d = result.change7d, marketCap = result.market_cap, volume = result.total_volume, rank = result.market_cap_rank, high = result.high_24h, low = result.low_24h, supply = result.circulating_supply))
                }
            }
            CoinsDao.insertAndDeleteInTransaction(newCoins)
        }
        catch(e: Exception){
            //_response.value = "Failure: ${e.message}"
            e.printStackTrace()

        }
    }

    //Aggiorna le coin nel database con solo nome, ticker
    suspend fun refreshCoinsList(){
        try {
            val listResult = CoingeckoApi.retrofitService.getCoinsList()
            if(listResult.isEmpty()){
                return
            }
            for (result in listResult) {
                CoinsDao.insertIfNotInDatabase(Coins(name = result.id, ticker = result.symbol, price = null, change1h = null, change24h = null, change7d = null, marketCap = null, volume = null, rank = null, high = null, low = null, supply = null, lastModified = null, image = null))
            }
        }catch (e: Exception){
            //
        }


    }

    //Aggiorna una lista specifica di monete
    suspend fun refreshListOfCoins(coins: List<String>){

        try {
            val listResult = CoingeckoApi.retrofitService.getDetailedCoinList(ids = coins.joinToString(",").toLowerCase(Locale.ROOT))
            if(listResult.isEmpty()){
                return
            }
            val newCoins: MutableList<Coins> = mutableListOf()
            for (result in listResult){
                val lastUpdateDb = CoinsDao.getLastUpdatedByTicker(result.symbol)
                var lastUpdate = "00"
                if (lastUpdateDb.isNotEmpty()) {
                    lastUpdate = lastUpdateDb[0]
                }

                //Si invia la richiesta per l'immagine con l'header If-Modified-Since
                val request = if (result.imgSrcUrl != null)
                    CoingeckoImage.imageRetrofitService.getImage(result.imgSrcUrl.replace("/large/", "/small/"), lastUpdate)
                else
                    null
                //Se la richiesta ritorna 200, l'immagine non è aggiornata e si provvede a farlo
                if (request != null && request.code() == 200){
                    val lastmodified = request.headers().get("last-modified")
                    val image = request.body()?.bytes()
                    newCoins.add(Coins(name = result.id, ticker = result.symbol, price = result.current_price, change1h = result.change1h, change24h = result.change24h, change7d = result.change7d, marketCap = result.market_cap, volume = result.total_volume, rank = result.market_cap_rank, high = result.high_24h, low = result.low_24h, supply = result.circulating_supply, lastModified = lastmodified, image = image))
                }
                //Se la richiesta ritorna 304, l'immagine è aggiornata. Si aggiorna solo il resto
                else{
                    newCoins.add(Coins(name = result.id, ticker = result.symbol, price = result.current_price, change1h = result.change1h, change24h = result.change24h, change7d = result.change7d, marketCap = result.market_cap, volume = result.total_volume, rank = result.market_cap_rank, high = result.high_24h, low = result.low_24h, supply = result.circulating_supply))
                }
            }
            CoinsDao.insertAndDeleteInTransaction(newCoins)
        }
        catch(e: Exception){
            e.printStackTrace()

        }

    }

}

