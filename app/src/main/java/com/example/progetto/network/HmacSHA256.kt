package com.example.progetto.network

import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.security.SignatureException
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

//Classe per fare firmare le richieste verso Binance
class HmacSHA256(secret: String) {

    private val mac: Mac

    init {
        val hmacSHA256 = "HmacSHA256"
        val secretBytes = secret.toByteArray()
        val secretKeySpec = SecretKeySpec(secretBytes, hmacSHA256)
        mac = Mac.getInstance(hmacSHA256)
        mac.init(secretKeySpec)

    }

    @Throws(SignatureException::class, NoSuchAlgorithmException::class, InvalidKeyException::class)
    fun encode(message: String): String {
        val encodedArray = mac.doFinal(message.toByteArray())
        return encodedArray.joinToString("") { "%02x".format(it) }
    }
}