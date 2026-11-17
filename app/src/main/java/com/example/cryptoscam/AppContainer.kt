package com.example.cryptoscam


import android.content.Context
import com.example.cryptoscam.data.local.CryptoExchangeDatabase
import com.example.cryptoscam.data.remote.CoinGeckoApi
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AppContainer(context: Context) {
    private val database = CryptoExchangeDatabase.getDatabase(context)
    val cryptoDao = database.cryptoExchangeDao()



    private val authInterceptor = Interceptor { chain ->
        val request = chain.request().newBuilder()
            .header("x-cg-demo-api-key", BuildConfig.COINGECKO_API_KEY)
            .build()
        chain.proceed(request)
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.coingecko.com/api/v3/")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val coinApi: CoinGeckoApi = retrofit.create(CoinGeckoApi::class.java)
    // later: repo, use cases
}