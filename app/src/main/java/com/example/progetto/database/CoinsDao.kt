package com.example.progetto.database


import androidx.room.*
import kotlinx.coroutines.flow.Flow


@Dao
interface CoinsDao {

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(coin: Coins)

    @Insert (onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIfNotInDatabase(coin: Coins)

    @Update
    suspend fun update(coin: Coins)

    @Query("SELECT * FROM coins ORDER BY rank IS NULL, rank ASC")
     fun getAllCoins(): Flow<List<Coins>>

    @Query("SELECT ticker FROM coins ORDER BY rank IS NULL, rank ASC")
     fun getAllCoinsTicker(): Flow<List<String>>

    @Query("SELECT * FROM coins WHERE ticker = :ticker")
     fun getCoinByTickerLiveData(ticker: String): Flow<Coins>

    @Query("SELECT * FROM coins WHERE ticker LIKE '%' || :search || '%' OR name LIKE '%' || :search || '%' ORDER BY market_cap DESC")
    suspend fun searchByNameOrTicker(search: String): List<Coins>

    @Query("SELECT * FROM coins WHERE ticker = :ticker")
    fun getCoinByTicker(ticker: String): Coins

    @Query("SELECT price FROM coins WHERE ticker = :ticker")
    suspend fun getCoinPriceByTicker(ticker: String): List<Float>

    @Query("SELECT last_modified  FROM coins WHERE ticker = :ticker")
    suspend fun getLastUpdatedByTicker(ticker: String): List<String>

    @Query("UPDATE coins SET price = :price, market_cap = :marketCap, change1h = :change1h, change24h = :change24h, change7d = :change7d, volume = :volume WHERE ticker = :ticker")
    suspend fun updateByTicker(ticker: String, price: Double?, marketCap: Double?, change1h: Double?, change24h: Double?, change7d: Double?, volume: Double?)



    @Query("DELETE FROM coins WHERE ticker = :ticker")
    suspend fun deleteById(ticker: String)

    @Delete
    suspend fun deleteCoin(vararg coin: Coins)

    @Transaction
    suspend fun insertAndDeleteInTransaction(updatedCoins: List<Coins>) {
        for (updatedCoin in updatedCoins) {
            if (updatedCoin.image == null){
                updateByTicker(updatedCoin.ticker, updatedCoin.price, updatedCoin.marketCap, updatedCoin.change1h, updatedCoin.change24h, updatedCoin.change7d, updatedCoin.volume)
            }else{
                insert(updatedCoin)
            }
        }
    }




}