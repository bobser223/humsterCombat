package com.example.cryptoscam.data.local

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import androidx.room3.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface CryptoExchangeDao {

//    @Query("SELECT * FROM crypto_assets ORDER BY rank ASC")
//    fun observeAll(): Flow<List<CryptoExchangeEntity>>

    @Query("SELECT MAX(updatedAt) FROM crypto_assets")
    fun observeLastUpdated(): Flow<Long?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<CryptoExchangeEntity>)

    @Query("DELETE FROM crypto_assets")
    suspend fun deleteAll()

    @Transaction
    suspend fun replaceAll(items: List<CryptoExchangeEntity>) {
        deleteAll()
        insertAll(items)
    }

    // Instead of observeAll
    @Query("""
        SELECT * FROM crypto_assets
        WHERE currency LIKE '%' || :query || '%'
            OR currencyAbbreviation LIKE '%' || :query || '%'
        ORDER BY rank ASC
    """)
    fun observeQueried(query: String): Flow<List<CryptoExchangeEntity>>
}