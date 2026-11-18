package com.example.cryptoscam.worker

import android.content.Context
import android.speech.SpeechRecognizer.ERROR_NETWORK
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.cryptoscam.CryptoApp
import com.example.cryptoscam.domain.model.SyncResult
import kotlin.coroutines.cancellation.CancellationException

class SyncWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams){

    override suspend fun doWork(): Result {
        return try {
            val syncCryptoAssets = (applicationContext as CryptoApp).container.syncCryptoAssets

            return when (syncCryptoAssets()) {
                SyncResult.Success -> Result.success()

                SyncResult.NetworkError,
                SyncResult.ServerError,
                SyncResult.RateLimited ->
                    if (runAttemptCount < MAX_RETRIES) Result.retry()
                    else Result.failure(workDataOf(KEY_ERROR to ERROR_NETWORK))

                SyncResult.AuthError ->
                    Result.failure(workDataOf(KEY_ERROR to ERROR_AUTH))

                SyncResult.InvalidResponse ->
                    Result.failure(workDataOf(KEY_ERROR to ERROR_INVALID))
            }


            // Якщо задача виконана успішно
            Result.success()

        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e("WorkManager", "Неочікувана помилка синхронізації", e)

            Result.failure(workDataOf(KEY_ERROR to ERROR_UNKNOWN))
        }
    }

    companion object {
        const val MAX_RETRIES = 3

        const val KEY_ERROR = "error"
        const val ERROR_NETWORK = "network"
        const val ERROR_AUTH = "auth"
        const val ERROR_INVALID = "invalid"
        const val ERROR_UNKNOWN = "unknown"
    }


}

