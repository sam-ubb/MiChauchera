package com.spidersam.michauchera.workers

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.ListenableWorker
import com.spidersam.michauchera.MainActivity
import com.spidersam.michauchera.R
import com.spidersam.michauchera.data.BaseDeDatos
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.NumberFormat
import java.util.*

/**
 * Worker que verifica periódicamente si el gasto mensual del usuario
 * ha superado el límite establecido y envía una notificación de alerta.
 *
 * Cumple con los requisitos de WorkManager de la Entrega 3:
 * - Hereda de CoroutineWorker para operaciones asíncronas
 * - Gestiona condiciones de batería y red mediante Constraints
 * - Ejecuta tareas en segundo plano (notificaciones)
 */
class PresupuestoAlertWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "presupuesto_alerts"
        const val NOTIFICATION_CHANNEL_NAME = "Alertas de Presupuesto"
        const val NOTIFICATION_ID = 1001
        const val WORK_NAME = "presupuesto_alert_work"
    }

    override suspend fun doWork(): ListenableWorker.Result = withContext(Dispatchers.IO) {
        try {
            // Obtener base de datos
            val database = BaseDeDatos.obtenerBaseDeDatos(context)
            val usuarioDao = database.usuarioDao()
            val repositorio = com.spidersam.michauchera.data.RepositorioTransacciones(
                database.transaccionDao()
            )

            // Obtener configuración del usuario
            val usuario = usuarioDao.obtenerUsuario()
            val limiteGastoMensual = usuario?.limiteGastoMensual ?: 0.0
            val notificacionesActivas = usuario?.notificacionesActivas ?: true

            // Verificar si las notificaciones están activas
            if (!notificacionesActivas) {
                return@withContext ListenableWorker.Result.success()
            }

            // Si no hay límite establecido, no hay nada que verificar
            if (limiteGastoMensual <= 0) {
                return@withContext ListenableWorker.Result.success()
            }

            // Obtener gasto mensual actual
            val gastosMesActual = repositorio.obtenerGastosMesActual()

            // Calcular porcentaje del límite utilizado
            val porcentajeUtilizado = (gastosMesActual / limiteGastoMensual) * 100

            // Enviar notificación si se ha superado el 80% o el 100% del límite
            when {
                porcentajeUtilizado >= 100 -> {
                    enviarNotificacion(
                        titulo = "⚠️ ¡Límite de Presupuesto Superado!",
                        mensaje = "Has gastado ${formatearMonto(gastosMesActual)} de ${formatearMonto(limiteGastoMensual)} este mes (${porcentajeUtilizado.toInt()}%).",
                        prioridad = NotificationCompat.PRIORITY_HIGH
                    )
                }
                porcentajeUtilizado >= 80 -> {
                    enviarNotificacion(
                        titulo = "⚠️ Acercándote al Límite",
                        mensaje = "Has gastado ${formatearMonto(gastosMesActual)} de ${formatearMonto(limiteGastoMensual)} este mes (${porcentajeUtilizado.toInt()}%).",
                        prioridad = NotificationCompat.PRIORITY_DEFAULT
                    )
                }
            }

            ListenableWorker.Result.success()
        } catch (e: Exception) {
            // En caso de error, reintentar
            if (runAttemptCount < 3) {
                ListenableWorker.Result.retry()
            } else {
                ListenableWorker.Result.failure()
            }
        }
    }

    /**
     * Crea y envía una notificación al usuario
     */
    private fun enviarNotificacion(titulo: String, mensaje: String, prioridad: Int) {
        // Crear canal de notificación (requerido para Android 8.0+)
        crearCanalNotificacion()

        // Verificar permisos de notificación (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
        }

        // Intent para abrir la app al tocar la notificación
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Construir notificación
        val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification) // Asegúrate de tener este ícono
            .setContentTitle(titulo)
            .setContentText(mensaje)
            .setStyle(NotificationCompat.BigTextStyle().bigText(mensaje))
            .setPriority(prioridad)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        // Mostrar notificación
        with(NotificationManagerCompat.from(context)) {
            notify(NOTIFICATION_ID, notification)
        }
    }

    /**
     * Crea el canal de notificación para Android 8.0+
     */
    private fun crearCanalNotificacion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificaciones sobre alertas de presupuesto mensual"
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Formatea un monto a formato de moneda
     */
    private fun formatearMonto(monto: Double): String {
        val formato = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-CL"))
        return formato.format(monto)
    }
}

