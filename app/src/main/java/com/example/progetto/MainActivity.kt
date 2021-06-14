package com.example.progetto

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.progetto.network.ArkWorker
import com.example.progetto.network.BinanceWorker
import com.example.progetto.network.BitcoinWorker
import com.example.progetto.repository.CoinsRepository
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val repository = CoinsRepository(application)
        //All'avvio, si aggiornano tutte le connessioni
        GlobalScope.launch {
            repository.refreshCoinsList()
            val connections = repository.getAllConnections()
            for (connection in connections) {
                when {
                    connection.name == "ark" -> {
                        val arkWorker = OneTimeWorkRequest.Builder(ArkWorker::class.java)
                            .setInputData(
                                workDataOf(
                                    "address" to connection.key,
                                    "last_update" to connection.lastUpdate
                                )
                            ).build()
                        WorkManager.getInstance(application).enqueue(arkWorker)

                    }
                    connection.name == "binance" -> {
                        val coins = repository.getInsertedCoins().toTypedArray()
                        val binanceWorker = OneTimeWorkRequest.Builder(BinanceWorker::class.java)
                            .setInputData(
                                workDataOf(
                                    "key" to connection.key,
                                    "secret" to connection.privateKey,
                                    "coins" to coins,
                                    "last_update" to connection.lastUpdate
                                )
                            ).build()
                        WorkManager.getInstance(application).enqueue(binanceWorker)
                    }
                    else -> {
                        val bitcoinWorker = OneTimeWorkRequest.Builder(BitcoinWorker::class.java)
                            .setInputData(
                                workDataOf(
                                    "address" to connection.key,
                                    "last_update" to connection.lastUpdate
                                )
                            ).build()
                        WorkManager.getInstance(application).enqueue(bitcoinWorker)

                    }
                }


            }
        }



        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.Nav_host) as NavHostFragment
        val navController = navHostFragment.navController
        findViewById<BottomNavigationView>(R.id.bottom_navigation).setupWithNavController(navController)



    }


}