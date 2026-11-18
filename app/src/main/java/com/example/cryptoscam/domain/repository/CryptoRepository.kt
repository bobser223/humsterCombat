package com.example.cryptoscam.domain.repository

import com.example.cryptoscam.domain.model.CryptoAsset
import com.example.cryptoscam.domain.model.SyncResult
import kotlinx.coroutines.flow.Flow

interface CryptoRepository {

    /** Збережений список із бази, відсортований за рангом. Оновлюється сам. */
    fun observeAssets(): Flow<List<CryptoAsset>>

    /** Час останнього успішного оновлення; null, якщо даних ще не було. */
    fun observeLastUpdated(): Flow<Long?>

    /** Завантажити свіжі дані з API і, якщо все добре, замінити ними базу. */
    suspend fun sync(): SyncResult
}