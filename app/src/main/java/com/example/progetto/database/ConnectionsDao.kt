package com.example.progetto.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ConnectionsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(connection: Connections)


    @Query("SELECT * FROM connections WHERE name = :name")
    suspend fun getConnectionByName(name: String): List<Connections>

    @Query("SELECT * FROM connections WHERE `key` = :address")
    suspend fun getConnectionByAddress(address: String): List<Connections>

    @Query("SELECT * FROM connections")
    fun getAllConnectionsAsLiveData(): Flow<List<Connections>>


    @Query("SELECT id, name, `key`, private_key, (SELECT MAX(date) FROM transactions WHERE transactions.`from` == name OR transactions.`from` == `key`) as last_update FROM connections")
    fun getAllConnections(): List<Connections>



    @Query("DELETE FROM connections WHERE id = :id")
    suspend fun deleteConnectionById(id: Long)



}