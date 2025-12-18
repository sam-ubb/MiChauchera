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
import com.spidersam.michauchera.MainActivity
import com.spidersam.michauchera.R
import com.spidersam.michauchera.data.BaseDeDatos
import com.spidersam.michauchera.data.RepositorioPresupuestos
import com.spidersam.michauchera.model.NivelAlertaPresupuesto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.NumberFormat
import java.util.*
class MonitoreoPresupuestosWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {
    companion object {
        const val WORK_NAME = "monitoreo_presupuestos_work"
        const val NOTIFICATION_CHANNEL_ID = "presupuestos_categorias"
        const val NOTIFICATION_CHANNEL_NAME = "Monitoreo de Presupuestos"
        const val NOTIFICATION_BASE_ID = 2000
        const val TAG_ADVERTENCIA = "advertencia_presupuesto"
        const val TAG_EXCEDIDO = "excedido_presupuesto"
    }
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val database = BaseDeDatos.obtenerBaseDeDatos(context)
            val repositorio = RepositorioPresupuestos(
                presupuestoDao = database.presupuestoDao(),
                transaccionDao = database.transaccionDao()
            )
            val usuarioDao = database.usuarioDao()
            val usuario = usuarioDao.obtenerUsuario()
            val notificacionesActivas = usuario?.notificacionesActivas ?: true
            if (!notificacionesActivas) {
                return@withContext Result.success()
            }
            val calendario = Calendar.getInstance()
            val mes = calendario.get(Calendar.MONTH) + 1
            val anio = calendario.get(Calendar.YEAR)
            val presupuestosConGasto = repositorio.obtenerPresupuestosConGasto(mes, anio)
            if (presupuestosConGasto.isEmpty()) {
                return@withContext Result.success()
            }
            crearCanalNotificacion()
            presupuestosConGasto.forEachIndexed { index, presupuestoConGasto ->
                when (presupuestoConGasto.nivelAlerta) {
                    NivelAlertaPresupuesto.ROJO -> {
                        enviarNotificacionCritica(presupuestoConGasto, index)
                    }
                    NivelAlertaPresupuesto.NARANJA -> {
                        enviarNotificacionAdvertenciaAlta(presupuestoConGasto, index)
                    }
                    NivelAlertaPresupuesto.AMARILLO -> {
                        enviarNotificacionAdvertenciaMedia(presupuestoConGasto, index)
                    }
                    NivelAlertaPresupuesto.VERDE -> {
                    }
                }
            }
            val presupuestosEnRiesgo = presupuestosConGasto.filter {
                it.nivelAlerta != NivelAlertaPresupuesto.VERDE
            }
            if (presupuestosEnRiesgo.size >= 3) {
                enviarNotificacionResumen(presupuestosEnRiesgo)
            }
            Result.success()
        } catch (e: Exception) {
            if (runAttemptCount < 3) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }
    private fun enviarNotificacionCritica(
        presupuestoConGasto: com.spidersam.michauchera.model.PresupuestoConGasto,
        index: Int
    ) {
        val presupuesto = presupuestoConGasto.presupuesto
        val porcentaje = presupuestoConGasto.porcentajeUsado.toInt()
        val exceso = presupuestoConGasto.gastoActual - presupuesto.montoLimite
        enviarNotificacion(
            id = NOTIFICATION_BASE_ID + index,
            titulo = "🚨 ¡PRESUPUESTO EXCEDIDO!",
            mensaje = "${presupuesto.categoria}: Has gastado ${formatearMonto(presupuestoConGasto.gastoActual)} " +
                    "de ${formatearMonto(presupuesto.montoLimite)} ($porcentaje%). " +
                    "Excediste en ${formatearMonto(exceso)}.",
            prioridad = NotificationCompat.PRIORITY_HIGH,
            tag = TAG_EXCEDIDO
        )
    }
    private fun enviarNotificacionAdvertenciaAlta(
        presupuestoConGasto: com.spidersam.michauchera.model.PresupuestoConGasto,
        index: Int
    ) {
        val presupuesto = presupuestoConGasto.presupuesto
        val porcentaje = presupuestoConGasto.porcentajeUsado.toInt()
        val disponible = presupuestoConGasto.montoDisponible
        enviarNotificacion(
            id = NOTIFICATION_BASE_ID + index,
            titulo = "⚠️ Casi sin Presupuesto",
            mensaje = "${presupuesto.categoria}: Usaste $porcentaje%. " +
                    "Solo quedan ${formatearMonto(disponible)} disponibles.",
            prioridad = NotificationCompat.PRIORITY_DEFAULT,
            tag = TAG_ADVERTENCIA
        )
    }
    private fun enviarNotificacionAdvertenciaMedia(
        presupuestoConGasto: com.spidersam.michauchera.model.PresupuestoConGasto,
        index: Int
    ) {
        val presupuesto = presupuestoConGasto.presupuesto
        val porcentaje = presupuestoConGasto.porcentajeUsado.toInt()
        enviarNotificacion(
            id = NOTIFICATION_BASE_ID + index,
            titulo = "💡 Precaución con tu Presupuesto",
            mensaje = "${presupuesto.categoria}: Ya usaste $porcentaje% del presupuesto del mes.",
            prioridad = NotificationCompat.PRIORITY_LOW,
            tag = TAG_ADVERTENCIA
        )
    }
    private fun enviarNotificacionResumen(
        presupuestosEnRiesgo: List<com.spidersam.michauchera.model.PresupuestoConGasto>
    ) {
        val excedidos = presupuestosEnRiesgo.count { it.estaExcedido }
        val advertencia = presupuestosEnRiesgo.size - excedidos
        val mensaje = buildString {
            if (excedidos > 0) {
                append("$excedidos presupuesto${if (excedidos > 1) "s" else ""} excedido${if (excedidos > 1) "s" else ""}. ")
            }
            if (advertencia > 0) {
                append("$advertencia en zona de advertencia.")
            }
        }
        enviarNotificacion(
            id = NOTIFICATION_BASE_ID + 999,
            titulo = "📊 Resumen de Presupuestos",
            mensaje = mensaje,
            prioridad = NotificationCompat.PRIORITY_DEFAULT,
            tag = "resumen"
        )
    }
    private fun enviarNotificacion(
        id: Int,
        titulo: String,
        mensaje: String,
        prioridad: Int,
        tag: String
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
        }
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("abrir_presupuestos", true)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(titulo)
            .setContentText(mensaje)
            .setStyle(NotificationCompat.BigTextStyle().bigText(mensaje))
            .setPriority(prioridad)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .build()
        with(NotificationManagerCompat.from(context)) {
            notify(tag, id, notification)
        }
    }
    private fun crearCanalNotificacion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notificaciones sobre el estado de tus presupuestos por categoría"
                enableVibration(true)
                enableLights(true)
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    private fun formatearMonto(monto: Double): String {
        val formato = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-CL"))
        return formato.format(monto)
    }
}
