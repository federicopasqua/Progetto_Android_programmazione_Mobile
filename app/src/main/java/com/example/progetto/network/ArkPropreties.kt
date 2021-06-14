package com.example.progetto.network


data class arkTransactions(
    val results: List<transaction>
)


data class transaction(
    val type: Int,
    val typeGroup: Int,
    val amount: Long,
    val fee: Long,
    val sender: String,
    val recipient: String,
    val asset: asset?,
    val timestamp: timestamp
)

data class asset(
    val payments: List<payment>?
)

data class payment(
    val amount: Long,
    val recipientId: String
)

data class timestamp(
    val unix: Long
)