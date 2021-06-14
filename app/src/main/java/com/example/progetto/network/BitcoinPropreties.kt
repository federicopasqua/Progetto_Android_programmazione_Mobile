package com.example.progetto.network

import com.squareup.moshi.Json

data class bitcoinAddress(
    @Json(name="n_tx") val nTx: Int,
    val txs: List<txs>
)

data class txs(
    @Json(name="double_spend") val doubleSpend: Boolean,
    val time: Long,
    val fee: Long,
    val inputs: List<inputs>,
    val out: List<out>
)

data class inputs (
    @Json(name="prev_out") val prevOut: tx
        )

data class out (
    val value: Long,
    val addr: String
)

data class tx(
    val value: Long,
    val addr: String
)