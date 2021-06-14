package com.example.progetto

import android.os.Bundle
import android.util.Log
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
import com.example.progetto.databinding.FragmentAddWalletBinding
import com.example.progetto.models.addWalletViewModel
import com.example.progetto.network.ArkWorker
import com.example.progetto.network.BitcoinWorker
import com.example.progetto.repository.CoinsRepository
import kotlinx.coroutines.runBlocking
import java.security.MessageDigest
import java.util.*


class addWalletFragment : Fragment() {

    private val args: addWalletFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentAddWalletBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_add_wallet, container, false)
        val application = requireNotNull(this.activity).application
        val repository = CoinsRepository(application)

        val coin = args.coin
        val modifyId = args.modifyId
        val argsAddress = args.address

        val viewModel = addWalletViewModel(repository)

        binding.showCoinNameAddWallet.text = coin.capitalize(Locale.ITALY)

        //Se modifyId è minore di 0, si permette la creazione. Nel caso contrario, la visualizzazione e eliminazione
        if (modifyId < 0){
            binding.confirmAddWalletButton.setOnClickListener {
                val address = binding.addWalletAddress.text.toString()

                //Controllo per verificare che l'indirizzo non sia già stato inserito
                var isUsed = true
                runBlocking {
                    val name = repository.getConnectionByAddress(address)
                    if (name.isEmpty()){
                        isUsed = false
                    }
                }
                var valid = false
                if (coin == "bitcoin"){
                    valid = Bitcoin.validateAddress(address)
                }else if (coin == "ark"){
                    valid = Ark.validateAddress(address)
                }
                //Nel caso non siano già presenti, viene lanciato il worker che scarica le transazioni
                if (!isUsed && valid){
                    viewModel.insert(Connections(name = coin, key = address, privateKey = null))

                    if (coin == "ark"){
                        val arkWorker = OneTimeWorkRequest.Builder(ArkWorker::class.java)
                            .setInputData(
                                workDataOf(
                                    "address" to address
                                )
                            ).build()
                        WorkManager.getInstance(application).enqueue(arkWorker)

                    }else if (coin == "bitcoin"){
                        val bitcoinWorker = OneTimeWorkRequest.Builder(BitcoinWorker::class.java)
                            .setInputData(
                                workDataOf(
                                    "address" to address
                                )
                            ).build()
                        WorkManager.getInstance(application).enqueue(bitcoinWorker)

                    }


                    binding.root.findNavController().navigate(R.id.action_addWalletFragment_to_portfolioFragment)
                }else if (isUsed){
                    binding.showErrorWallet.text = getString(R.string.walletinserito)
                }else{
                    binding.showErrorWallet.text = getString(R.string.indirizzoNonValido)
                }

            }

        }else{
            //Se viene passato un modifyId, si permette l'eliminazione
            binding.addWalletAddress.isEnabled = false
            binding.confirmAddWalletButton.text = getString(R.string.delete)
            binding.addWalletAddress.setText(argsAddress)
            binding.confirmAddWalletButton.setOnClickListener {
                viewModel.delete(modifyId)
                binding.root.findNavController().navigate(R.id.action_addWalletFragment_pop)
            }
        }

        return binding.root
    }



    object Bitcoin {
        private const val ALPHABET = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz"

        private fun ByteArray.contentEquals(other: ByteArray): Boolean {
            if (this.size != other.size) return false
            return (0 until this.size).none { this[it] != other[it] }
        }

        private fun decodeBase58(input: String): ByteArray? {
            val output = ByteArray(25)
            for (c in input) {
                var p = ALPHABET.indexOf(c)
                if (p == -1) return null
                for (j in 24 downTo 1) {
                    p += 58 * (output[j].toInt() and 0xff)
                    output[j] = (p % 256).toByte()
                    p = p shr 8
                }
                if (p != 0) return null
            }
            return output
        }

        private fun sha256(data: ByteArray, start: Int, len: Int, recursion: Int): ByteArray {
            if (recursion == 0) return data
            val md = MessageDigest.getInstance("SHA-256")
            md.update(data.sliceArray(start until start + len))
            return sha256(md.digest(), 0, 32, recursion - 1)
        }

        fun validateAddress(address: String): Boolean {
            if (address.length !in 26..35) return false
            val decoded = decodeBase58(address)
            if (decoded == null) return false
            val hash = sha256(decoded, 0, 21, 2)
            return hash.sliceArray(0..3).contentEquals(decoded.sliceArray(21..24))
        }
    }

    object Ark {
        private const val ALPHABET = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz"


        private fun decodeBase58(input: String): ByteArray? {
            val output = ByteArray(25)
            for (c in input) {
                var p = ALPHABET.indexOf(c)
                if (p == -1) return null
                for (j in 24 downTo 1) {
                    p += 58 * (output[j].toInt() and 0xff)
                    output[j] = (p % 256).toByte()
                    p = p shr 8
                }
                //if (p != 0) return null
            }
            return output
        }


        fun validateAddress(address: String): Boolean {
            if (address.length != 34) return false
            if (!address.startsWith("A")) return false
            val decoded = decodeBase58(address) ?: return false

            return true
        }
    }
}