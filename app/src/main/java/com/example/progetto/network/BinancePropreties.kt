package com.example.progetto.network

import com.squareup.moshi.Json

data class Trade(
    val id: Long,
    val orderId: Long,
    val price: Float,
    @Json(name="qty") val quantity: Float,
    @Json(name="quoteQty") val quoteQuantity: Float,
    val commission: Float,
    val commissionAsset: String,
    val time: Long,
    val isBuyer: Boolean,
    val isMaker: Boolean,
    val isBestMatch: Boolean
)


data class Deposit(
    val coin: String,
    val amount: Float,
    val insertTime: String,
    val address: String,
    val txId: String,
    val status: Int
    )




    data class Withdraw(
        val id: String,
        val amount: Float,
        val address: String,
        val coin: String,
        val txId: String,
        val applyTime: String,
        val status: Int
    )
