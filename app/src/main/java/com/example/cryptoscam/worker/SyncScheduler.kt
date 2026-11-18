package com.example.cryptoscam.worker

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import kotlinx.coroutines.flow.Flow
import java.util.concurrent.TimeUnit

class SyncScheduler(context: Context) {

    private val workManager = WorkManager.getInstance(context)

    private val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    fun schedulePeriodic(periodMinutes: Long = 60){
        var periodMinutes = periodMinutes
        if (periodMinutes < 15) periodMinutes = 15


        val periodicRequest = PeriodicWorkRequestBuilder<SyncWorker>(
            repeatInterval = periodMinutes,
            repeatIntervalTimeUnit = TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .build()


        workManager.enqueueUniquePeriodicWork(
            PERIODIC_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            periodicRequest
        )
    }


    fun syncNow(){
        val request = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(constraints)
            .build()

        workManager.enqueueUniqueWork(
            MANUAL_WORK_NAME,
            ExistingWorkPolicy.KEEP,
            request
        )
    }

    fun observeManualSync(): Flow<List<WorkInfo>> =
        workManager.getWorkInfosForUniqueWorkFlow(MANUAL_WORK_NAME)

    companion object {
        const val PERIODIC_WORK_NAME = "crypto_periodic_sync"
        const val MANUAL_WORK_NAME = "crypto_manual_sync"
    }

}