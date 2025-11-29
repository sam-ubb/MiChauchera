package com.spidersam.michauchera.data

import androidx.lifecycle.LiveData
import com.spidersam.michauchera.model.Transaccion
import com.spidersam.michauchera.model.TipoTransaccion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Date

class RepositorioTransacciones(private val transaccionDao: TransaccionDao) {

    val todasLasTransacciones: LiveData<List<Transaccion>> = transaccionDao.obtenerTodasLasTransacciones()
    val balanceTotal: LiveData<Double> = transaccionDao.obtenerBalanceTotal()
    val totalIngresos: LiveData<Double> = transaccionDao.obtenerTotalIngresos()
    val totalGastos: LiveData<Double> = transaccionDao.obtenerTotalGastos()

    suspend fun insertarTransaccion(transaccion: Transaccion): Result<Long> {
        return withContext(Dispatchers.IO) {
            try {
                validarTransaccion(transaccion)
                val id = transaccionDao.insertarTransaccion(transaccion)
                Result.success(id)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun actualizarTransaccion(transaccion: Transaccion): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                validarTransaccion(transaccion)
                transaccionDao.actualizarTransaccion(transaccion)
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun eliminarTransaccion(transaccion: Transaccion): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                transaccionDao.eliminarTransaccion(transaccion)
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun eliminarTransaccionPorId(id: Long): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                transaccionDao.eliminarTransaccionPorId(id)
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun obtenerTransaccionPorId(id: Long): Transaccion? {
        return withContext(Dispatchers.IO) {
            transaccionDao.obtenerTransaccionPorId(id)
        }
    }

    fun obtenerTransaccionesPorTipo(tipo: TipoTransaccion): LiveData<List<Transaccion>> {
        return transaccionDao.obtenerTransaccionesPorTipo(tipo)
    }

    suspend fun obtenerBalanceMesActual(): Double {
        return withContext(Dispatchers.IO) {
            val (fechaInicio, fechaFin) = obtenerRangoMesActual()
            transaccionDao.obtenerBalanceMensual(fechaInicio, fechaFin)
        }
    }

    suspend fun obtenerIngresosMesActual(): Double {
        return withContext(Dispatchers.IO) {
            val (fechaInicio, fechaFin) = obtenerRangoMesActual()
            transaccionDao.obtenerIngresosMensuales(fechaInicio, fechaFin)
        }
    }

    suspend fun obtenerGastosMesActual(): Double {
        return withContext(Dispatchers.IO) {
            val (fechaInicio, fechaFin) = obtenerRangoMesActual()
            transaccionDao.obtenerGastosMensuales(fechaInicio, fechaFin)
        }
    }

    fun obtenerTransaccionesMesActual(): LiveData<List<Transaccion>> {
        val (fechaInicio, fechaFin) = obtenerRangoMesActual()
        return transaccionDao.obtenerTransaccionesPorMes(fechaInicio, fechaFin)
    }

    suspend fun obtenerEstadisticasMensuales(): EstadisticasMensuales {
        return withContext(Dispatchers.IO) {
            val (fechaInicio, fechaFin) = obtenerRangoMesActual()
            val ingresos = transaccionDao.obtenerIngresosMensuales(fechaInicio, fechaFin)
            val gastos = transaccionDao.obtenerGastosMensuales(fechaInicio, fechaFin)
            val balance = ingresos - gastos

            EstadisticasMensuales(
                ingresos = ingresos,
                gastos = gastos,
                balance = balance,
                mes = Calendar.getInstance().get(Calendar.MONTH),
                anio = Calendar.getInstance().get(Calendar.YEAR)
            )
        }
    }

    private fun validarTransaccion(transaccion: Transaccion) {
        require(transaccion.monto > 0) { "El monto debe ser mayor a 0" }
        require(transaccion.categoria.isNotBlank()) { "Debe seleccionar una categoría" }
    }

    private fun obtenerRangoMesActual(): Pair<Date, Date> {
        val calendario = Calendar.getInstance()

        calendario.set(Calendar.DAY_OF_MONTH, 1)
        calendario.set(Calendar.HOUR_OF_DAY, 0)
        calendario.set(Calendar.MINUTE, 0)
        calendario.set(Calendar.SECOND, 0)
        calendario.set(Calendar.MILLISECOND, 0)
        val fechaInicio = calendario.time

        calendario.add(Calendar.MONTH, 1)
        val fechaFin = calendario.time

        return Pair(fechaInicio, fechaFin)
    }
}

data class EstadisticasMensuales(
    val ingresos: Double,
    val gastos: Double,
    val balance: Double,
    val mes: Int,
    val anio: Int
)

