package com.example.progetto.network


import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

//Interceptor per firmare le transazioni verso Binance
class AuthenticationInterceptor(private val apiKey: String, secret: String) : Interceptor {

    private val hmacSha256 = HmacSHA256(secret)

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val newRequestBuilder = original.newBuilder()


        newRequestBuilder.addHeader("X-MBX-APIKEY", apiKey)


        val payload = original.url().query()
        if (payload != null && payload.isNotBlank()) {
            val signature = hmacSha256.encode(payload)
            val signedUrl = original.url().newBuilder().addQueryParameter("signature", signature).build()
            newRequestBuilder.url(signedUrl)
        }

        val newRequest = newRequestBuilder.build()
        return chain.proceed(newRequest)
    }
}