package com.example.cryptoscam.data.repository

import com.example.cryptoscam.data.local.CryptoExchangeDao
import com.example.cryptoscam.data.mapper.toDomain
import com.example.cryptoscam.data.mapper.toEntities
import com.example.cryptoscam.data.remote.CoinGeckoApi
import com.example.cryptoscam.domain.model.CryptoAsset
import com.example.cryptoscam.domain.model.SyncResult
import com.example.cryptoscam.domain.repository.CryptoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException

class CryptoRepositoryImpl(
    private val api: CoinGeckoApi,
    private val dao: CryptoExchangeDao
) : CryptoRepository {
    override fun observeAssets(query: String): Flow<List<CryptoAsset>> =
        dao.observeQueried(query)
            .map { entities ->
                entities.map { it.toDomain() }
            }

    override fun observeLastUpdated(): Flow<Long?> =
        dao.observeLastUpdated()


    override suspend fun sync(): SyncResult {
        val response = try {
            api.getTopCoins()
        } catch (e: CancellationException) {
            throw e
        } catch (e: IOException) {
            return SyncResult.NetworkError
        } catch (e: Exception) {
            return SyncResult.InvalidResponse
        }

        if (!response.isSuccessful) {
            return when (response.code()) {
                401, 403 -> SyncResult.AuthError
                429 -> SyncResult.RateLimited
                in 500..599 -> SyncResult.ServerError
                else -> SyncResult.InvalidResponse
            }
        }


        val body = response.body() ?: return SyncResult.InvalidResponse

        val entities = body.toEntities(now = System.currentTimeMillis())


        if (entities.isEmpty()) return SyncResult.InvalidResponse


        dao.replaceAll(entities)

        return SyncResult.Success
    }
}