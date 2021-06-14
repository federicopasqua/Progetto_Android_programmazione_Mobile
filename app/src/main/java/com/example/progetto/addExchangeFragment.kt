package com.example.progetto

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.progetto.database.Connections
import com.example.progetto.databinding.FragmentAddExchangeBinding
import com.example.progetto.models.addExchangeViewModel
import com.example.progetto.network.BinanceWorker
import com.example.progetto.repository.CoinsRepository
import kotlinx.coroutines.runBlocking


class addExchangeFragment : Fragment() {

    val args: addExchangeFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentAddExchangeBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_add_exchange, container, false)
        val application = requireNotNull(this.activity).application
        val repository = CoinsRepository(application)

        val modifyId = args.modifyId

        val viewModel = addExchangeViewModel(repository)

        //Se modifyId è minore di 0 si tratta di un inserimento. Se è maggiore si tratta di un edit/delete
        if (modifyId < 0){
            binding.confirmAddExchangeButton.text =  getString(R.string.add)
            binding.confirmAddExchangeButton.setOnClickListener {
                val key = binding.addExchangeKey.text.toString()
                val secret = binding.addExchangePrivateKey.text.toString()

                //Si verifica che non sia già stato inserito l'exchange e si inserisce nel caso non lo sia
                var isUsed = true
                runBlocking {
                    val name = repository.getConnectionByName("binance")
                    if (name.isEmpty()){
                        isUsed = false
                    }
                }
                if (!isUsed) {
                    viewModel.insert(
                        Connections(
                            name = "binance",
                            key = key,
                            privateKey = secret
                        )
                    )
                    //Si fa partire il worker
                    runBlocking {
                        val coins = viewModel.getCoins().toTypedArray()
                        val binanceWorker = OneTimeWorkRequest.Builder(BinanceWorker::class.java)
                            .setInputData(
                                workDataOf(
                                    "key" to key,
                                    "secret" to secret,
                                    "coins" to coins
                                )
                            ).build()
                        WorkManager.getInstance(application).enqueue(binanceWorker)
                    }
                    //Si torna indietro
                    binding.root.findNavController()
                        .navigate(R.id.action_addExchangeFragment_to_portfolioFragment)
                }else{
                    binding.showErrorExchange.text = getString(R.string.exchangeinserito)
                }
            }
        }else{
            //Si permette l'eliminazione della connessione
            binding.addExchangeKey.isEnabled = false
            binding.addExchangePrivateKey.isEnabled = false
            binding.confirmAddExchangeButton.text = getString(R.string.delete)
            binding.confirmAddExchangeButton.setOnClickListener {
                viewModel.delete(modifyId)
                binding.root.findNavController().navigate(R.id.action_addExchangeFragment_pop)
            }
        }

        return binding.root
    }
}