package com.example.progetto

import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.progetto.adapter.searchAdapter
import com.example.progetto.databinding.FragmentSearchBinding
import com.example.progetto.models.searchViewModel
import com.example.progetto.repository.CoinsRepository


class SearchFragment : Fragment() {
    var cTimer: CountDownTimer? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentSearchBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_search, container, false)

        val application = requireNotNull(this.activity).application
        val repository = CoinsRepository(application)
        val viewModel = searchViewModel(repository)
        binding.lifecycleOwner = this

        val adapter = searchAdapter()
        binding.searchOutput.adapter = adapter

        //listener che aggiorna la listview man mano che si scrive nella barra di ricerca
        //è stato inserito un tempo di ritardo da quando si finisce a scrivere in modo da non
        //sovraccaricare con le troppe richieste
        binding.searchFIeld.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                cTimer?.cancel()
                cTimer = object : CountDownTimer(500,10000) {
                    override fun onTick(millisUntilFinished: Long) {}
                    override fun onFinish() {
                        viewModel.updateResearch(s.toString())
                    }
                }
                cTimer?.start()

            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })

        viewModel.coins.observe(viewLifecycleOwner, {
            adapter.data = it
        })

        //listener che aggiorna la lista man mano che si scrolla
        binding.searchOutput.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if ((recyclerView.layoutManager  as LinearLayoutManager?)!!.findFirstCompletelyVisibleItemPosition() > (viewModel.page-2)*50) {
                    viewModel.addPage()
                    viewModel.updateCoins()
                }
            }
        })


        return binding.root
    }


}