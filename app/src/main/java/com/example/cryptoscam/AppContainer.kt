package com.example.cryptoscam


import android.content.Context
import com.example.cryptoscam.data.local.CryptoExchangeDatabase

class AppContainer(context: Context) {
    private val database = CryptoExchangeDatabase.getDatabase(context)
    val cryptoDao = database.cryptoExchangeDao()

    // later: Retrofit API, repo, use cases
}