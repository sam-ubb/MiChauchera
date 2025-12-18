package com.spidersam.michauchera.utils

import android.content.Context
import androidx.work.*
import com.spidersam.michauchera.workers.PresupuestoAlertWorker
import java.util.concurrent.TimeUnit

object WorkManagerUtil {

    fun configurarPresupuestoWorker(context: Context) {

        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()


        val workRequest = PeriodicWorkRequestBuilder<PresupuestoAlertWorker>(
            repeatInterval = 12,
            repeatIntervalTimeUnit = TimeUnit.HOURS,
            flexTimeInterval = 2,
            flexTimeIntervalUnit = TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                WorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .build()


        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            PresupuestoAlertWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }


    fun ejecutarPresupuestoWorkerAhora(context: Context) {
        val workRequest = OneTimeWorkRequestBuilder<PresupuestoAlertWorker>()
            .build()
        
        WorkManager.getInstance(context).enqueue(workRequest)
    }


    fun cancelarPresupuestoWorker(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(PresupuestoAlertWorker.WORK_NAME)
    }
}

