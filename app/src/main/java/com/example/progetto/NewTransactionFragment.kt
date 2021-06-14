package com.example.progetto

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.progetto.database.AppDatabase
import com.example.progetto.database.Transactions
import com.example.progetto.databinding.FragmentNewTransactionBinding
import com.example.progetto.models.NewTransactionViewModel
import com.example.progetto.network.CoingeckoApi
import com.example.progetto.network.CoingeckoImage
import com.example.progetto.repository.CoinsRepository
import kotlinx.coroutines.runBlocking
import kotlin.math.abs

class NewTransactionFragment : Fragment() {

    val args: NewTransactionFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentNewTransactionBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_new_transaction, container, false)
        val application = requireNotNull(this.activity).application
        val repository = CoinsRepository(application)
        val newTransactionModel = NewTransactionViewModel(repository)


        val modifyId = args.modifyId
        val buySellTicker = args.buySellTicker
        binding.buySellTicker.setText(buySellTicker)
        binding.condirmNewTransactionButton.text = getString(R.string.add)
        binding.buySellTicker.isEnabled = false
        //Se l'id è maggiore di 0, si tratta di una modifica
        if (modifyId >= 0){
            val transactions: List<Transactions>
            runBlocking {
                transactions = repository.getById(modifyId)
            }
            val thisTransaction = transactions.find{ v -> v.ticker == buySellTicker}
            val otherTransaction = transactions.find{ v -> v.ticker != buySellTicker}

            val typeP = thisTransaction?.type
            val buySell = abs(thisTransaction?.amount!!)

            binding.deleteButton.visibility = View.VISIBLE
            binding.typeSpinner.setSelection(typeP!!)
            binding.typeSpinner.isEnabled = false
            binding.buySellAmount.setText(buySell.toString())
            if (typeP in 0..1){
                val exchangeOf = otherTransaction?.ticker
                val prezzo = abs(otherTransaction?.amount!! / thisTransaction.amount)
                val commissionP = thisTransaction.commission
                val commissionTickerP = thisTransaction.commissionTicker
                binding.exchangeOfTicker.setText(exchangeOf)
                binding.exchangeOfTicker.isEnabled = false
                binding.priceAmount.setText(prezzo.toString())
                binding.commissionTicker.setText(commissionTickerP)
                binding.commissionAMount.setText(commissionP.toString())
            }
            binding.condirmNewTransactionButton.text = getString(R.string.edit)
            binding.newTransactionTopText.text = getString(R.string.editTransaction)

        }

        binding.deleteButton.setOnClickListener{
            newTransactionModel.deleteById(modifyId)
            binding.root.findNavController().navigate(R.id.action_newTransactionFragment_pop)
        }


        binding.newTransactionModel = newTransactionModel
        binding.lifecycleOwner = this

        newTransactionModel.tickers.observe(viewLifecycleOwner, Observer<List<String>> {
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, it + listOf("EUR"))
            binding.buySellTicker.setAdapter(adapter)
            binding.commissionTicker.setAdapter(adapter)
            binding.exchangeOfTicker.setAdapter(adapter)
        })

        binding.typeSpinner.onItemSelectedListener = SpinnerActivity(binding, getString(R.string.buy), getString(R.string.sell), getString(R.string.amount))

        //Listener del bottone di conferma. Gli inpu vengono validati e si procede all'aggiornamento
        binding.condirmNewTransactionButton.setOnClickListener{
            val type = binding.typeSpinner.selectedItemPosition
            val amount = binding.buySellAmount.text.toString()
            val coin1 = binding.buySellTicker.text.toString()
            val coin2 = binding.exchangeOfTicker.text.toString()
            val price = binding.priceAmount.text.toString()
            val commission = binding.commissionAMount.text.toString()
            val commissionTicker = binding.commissionTicker.text.toString()
            val result = newTransactionModel.validateInput(type, amount, coin1, coin2, price, commission, commissionTicker, modifyId)
            if (result == null){
                binding.root.findNavController().navigate(R.id.action_newTransactionFragment_pop)
            }
            else{
                binding.errorMessage.text = result
                binding.errorMessage.visibility = View.VISIBLE
            }

        }






        return binding.root
    }

}

//Classe che cambia il numero dei field in base alla selezione dello spinner
class SpinnerActivity(val binding: FragmentNewTransactionBinding, val buyString: String, val sellString: String, val amountString: String) : Activity(), AdapterView.OnItemSelectedListener {

    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
        when (pos){
            0 -> {
                binding.buySellPart.visibility = View.VISIBLE
                binding.BuySellText.text = buyString
            }
            1 -> {
                binding.buySellPart.visibility = View.VISIBLE
                binding.BuySellText.text = sellString
            }
            else -> {
                binding.buySellPart.visibility = View.GONE
                binding.BuySellText.text = amountString
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
        //
    }
}