package com.example.progetto.database

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow


@Dao
interface TransactionsDao {

    @Insert
    suspend fun insert(transaction: Transactions)

    @Update
    suspend fun update(transaction: Transactions)

    @Query("SELECT SUM(amount) FROM transactions WHERE ticker = :ticker")
    fun getBalance(ticker: String): Flow<Float>

    @Query("SELECT MAX(id) FROM transactions")
    fun getLastId(): Long

    @Query("SELECT * FROM transactions WHERE ticker = :ticker ORDER BY date DESC")
    fun getTransactions(ticker: String): Flow<List<Transactions>>

    @Query("SELECT coins.*, (SELECT SUM(transactions.amount) FROM transactions WHERE ticker = coins.ticker) as balance FROM coins WHERE coins.ticker IN (SELECT DISTINCT transactions.ticker FROM transactions) ORDER BY balance ASC")
    fun selectCoinsWithBalance(): Flow<List<coinsWithBalance>>

    @Query("SELECT coins.*, (SELECT SUM(transactions.amount) FROM transactions WHERE ticker = coins.ticker) as balance FROM coins WHERE coins.ticker = :ticker")
    fun selectCoinWithBalance(ticker: String): Flow<coinsWithBalance>

    @Query("SELECT DISTINCT transactions.ticker FROM transactions")
    fun getInsertedCoinsLive(): Flow<List<String>>

    @Query("SELECT DISTINCT transactions.ticker FROM transactions")
    suspend fun getInsertedCoins(): List<String>

    @Query("SELECT name FROM coins WHERE coins.ticker in (SELECT DISTINCT transactions.ticker FROM transactions)")
    suspend fun getInsertedCoinsCoingeckoIds(): List<String>

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getById(id: Long): List<Transactions>

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Delete
    suspend fun deleteTransaction(vararg transaction: Transactions)

    @Transaction
    suspend fun insertTransactionsWithSameId(transactions: List<Transactions>, id: Long? = null) {
        val newId = id ?: getLastId() + 1
        for (transaction in transactions){
            transaction.id = newId
            insert(transaction)
        }
    }

}