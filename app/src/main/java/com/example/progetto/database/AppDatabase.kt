package com.example.progetto.database


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [Transactions::class, Coins::class, Connections::class], version = 29, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {


    abstract val TransactionsDatabaseDao: TransactionsDao
    abstract val CoinsDatabaseDao: CoinsDao
    abstract val ConnectionsDao: ConnectionsDao

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {

            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "sleep_history_database"
                    ).fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}