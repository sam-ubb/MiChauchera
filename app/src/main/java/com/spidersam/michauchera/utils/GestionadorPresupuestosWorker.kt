package com.spidersam.michauchera.utils
import android.content.Context
import androidx.work.*
import com.spidersam.michauchera.workers.MonitoreoPresupuestosWorker
import java.util.concurrent.TimeUnit
object GestionadorPresupuestosWorker {
    fun programarMonitoreoPeriodico(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresCharging(false)
            .setRequiresStorageNotLow(false)
            .build()
        val trabajoPeriodico = PeriodicWorkRequestBuilder<MonitoreoPresupuestosWorker>(
            repeatInterval = 1,
            repeatIntervalTimeUnit = TimeUnit.DAYS,
            flexTimeInterval = 2,
            flexTimeIntervalUnit = TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .addTag(MonitoreoPresupuestosWorker.WORK_NAME)
            .addTag(MonitoreoPresupuestosWorker.TAG_ADVERTENCIA)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                WorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .build()
        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(
                MonitoreoPresupuestosWorker.WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                trabajoPeriodico
            )
    }
    fun programarMonitoreoInmediato(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(false)
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .build()
        val trabajoUnico = OneTimeWorkRequestBuilder<MonitoreoPresupuestosWorker>()
            .setConstraints(constraints)
            .addTag("monitoreo_inmediato")
            .setInitialDelay(5, TimeUnit.SECONDS)
            .build()
        WorkManager.getInstance(context)
            .enqueue(trabajoUnico)
    }
    fun programarMonitoreoConDelay(context: Context, delayMinutos: Long) {
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val trabajoConDelay = OneTimeWorkRequestBuilder<MonitoreoPresupuestosWorker>()
            .setConstraints(constraints)
            .setInitialDelay(delayMinutos, TimeUnit.MINUTES)
            .addTag("monitoreo_programado")
            .build()
        WorkManager.getInstance(context)
            .enqueue(trabajoConDelay)
    }
    fun cancelarMonitoreoPeriodico(context: Context) {
        WorkManager.getInstance(context)
            .cancelUniqueWork(MonitoreoPresupuestosWorker.WORK_NAME)
    }
    fun cancelarTodosLosMonitoreos(context: Context) {
        WorkManager.getInstance(context)
            .cancelAllWorkByTag(MonitoreoPresupuestosWorker.WORK_NAME)
    }
    fun obtenerEstadoMonitoreo(context: Context) =
        WorkManager.getInstance(context)
            .getWorkInfosForUniqueWorkLiveData(MonitoreoPresupuestosWorker.WORK_NAME)
    fun estaMonitoreoActivo(context: Context, callback: (Boolean) -> Unit) {
        val workInfos = WorkManager.getInstance(context)
            .getWorkInfosForUniqueWork(MonitoreoPresupuestosWorker.WORK_NAME)
        workInfos.addListener({
            try {
                val lista = workInfos.get()
                val workInfo = if (lista.isNotEmpty()) lista[0] else null
                val activo = workInfo?.state == WorkInfo.State.ENQUEUED ||
                             workInfo?.state == WorkInfo.State.RUNNING
                callback(activo)
            } catch (e: Exception) {
                callback(false)
            }
        }, { it.run() })
    }
}

