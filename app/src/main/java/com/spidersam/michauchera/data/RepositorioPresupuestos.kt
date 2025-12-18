package com.spidersam.michauchera.data
import androidx.lifecycle.LiveData
import com.spidersam.michauchera.model.Presupuesto
import com.spidersam.michauchera.model.PresupuestoConGasto
import com.spidersam.michauchera.model.TipoTransaccion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Date
class RepositorioPresupuestos(
    private val presupuestoDao: PresupuestoDao,
    private val transaccionDao: TransaccionDao
) {
    val presupuestosActivos: LiveData<List<Presupuesto>> =
        presupuestoDao.obtenerPresupuestosActivos()
    val presupuestosActivosFlow: Flow<List<Presupuesto>> =
        presupuestoDao.obtenerPresupuestosActivosFlow()
    suspend fun crearPresupuesto(presupuesto: Presupuesto): Result<Long> {
        return withContext(Dispatchers.IO) {
            try {
                validarPresupuesto(presupuesto)
                val existe = presupuestoDao.existePresupuestoParaCategoria(
                    presupuesto.categoria,
                    presupuesto.mes,
                    presupuesto.anio
                )
                if (existe) {
                    return@withContext Result.failure(
                        Exception("Ya existe un presupuesto para ${presupuesto.categoria} en este mes")
                    )
                }
                val id = presupuestoDao.insertarPresupuesto(presupuesto)
                Result.success(id)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    suspend fun crearPresupuestos(presupuestos: List<Presupuesto>): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                presupuestos.forEach { validarPresupuesto(it) }
                presupuestoDao.insertarPresupuestos(presupuestos)
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    suspend fun obtenerPresupuestoPorId(id: Long): Presupuesto? {
        return withContext(Dispatchers.IO) {
            presupuestoDao.obtenerPresupuestoPorId(id)
        }
    }
    fun obtenerPresupuestosMesActual(): LiveData<List<Presupuesto>> {
        val (mes, anio) = obtenerMesAnioActual()
        return presupuestoDao.obtenerPresupuestosPorMes(mes, anio)
    }
    fun obtenerPresupuestosPorMes(mes: Int, anio: Int): LiveData<List<Presupuesto>> {
        return presupuestoDao.obtenerPresupuestosPorMes(mes, anio)
    }
    suspend fun obtenerPresupuestosConGasto(
        mes: Int,
        anio: Int
    ): List<PresupuestoConGasto> {
        return withContext(Dispatchers.IO) {
            val presupuestos = presupuestoDao.obtenerPresupuestosPorMesSync(mes, anio)
            val (fechaInicio, fechaFin) = obtenerRangoMes(mes, anio)
            val transacciones = transaccionDao.obtenerTransaccionesPorMesSync(fechaInicio, fechaFin)
            val gastosPorCategoria = transacciones
                .filter { it.tipo == TipoTransaccion.GASTO }
                .groupBy { it.categoria }
                .mapValues { entry -> entry.value.sumOf { it.monto } }
            presupuestos.map { presupuesto ->
                val gastoActual = gastosPorCategoria[presupuesto.categoria] ?: 0.0
                PresupuestoConGasto(presupuesto, gastoActual)
            }
        }
    }
    suspend fun obtenerGastoActualCategoria(
        categoria: String,
        mes: Int,
        anio: Int
    ): Double {
        return withContext(Dispatchers.IO) {
            val (fechaInicio, fechaFin) = obtenerRangoMes(mes, anio)
            val transacciones = transaccionDao.obtenerTransaccionesPorMesSync(fechaInicio, fechaFin)
            transacciones
                .filter { it.tipo == TipoTransaccion.GASTO && it.categoria == categoria }
                .sumOf { it.monto }
        }
    }
    suspend fun obtenerPresupuestoTotalMesActual(): Double {
        return withContext(Dispatchers.IO) {
            val (mes, anio) = obtenerMesAnioActual()
            presupuestoDao.obtenerPresupuestoTotalMes(mes, anio)
        }
    }
    suspend fun obtenerPresupuestosExcedidos(): List<PresupuestoConGasto> {
        return withContext(Dispatchers.IO) {
            val (mes, anio) = obtenerMesAnioActual()
            val presupuestosConGasto = obtenerPresupuestosConGasto(mes, anio)
            presupuestosConGasto.filter { it.estaExcedido }
        }
    }
    suspend fun obtenerPresupuestosEnAdvertencia(): List<PresupuestoConGasto> {
        return withContext(Dispatchers.IO) {
            val (mes, anio) = obtenerMesAnioActual()
            val presupuestosConGasto = obtenerPresupuestosConGasto(mes, anio)
            presupuestosConGasto.filter { it.porcentajeUsado >= 80.0 && !it.estaExcedido }
        }
    }
    suspend fun actualizarPresupuesto(presupuesto: Presupuesto): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                validarPresupuesto(presupuesto)
                presupuestoDao.actualizarPresupuesto(presupuesto)
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    suspend fun actualizarMontoLimite(id: Long, nuevoLimite: Double): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                require(nuevoLimite > 0) { "El límite debe ser mayor a 0" }
                presupuestoDao.actualizarMontoLimite(id, nuevoLimite)
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    suspend fun archivarPresupuesto(id: Long): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                presupuestoDao.archivarPresupuesto(id)
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    suspend fun eliminarPresupuesto(presupuesto: Presupuesto): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                presupuestoDao.eliminarPresupuesto(presupuesto)
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    suspend fun eliminarPresupuestoPorId(id: Long): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                presupuestoDao.eliminarPresupuestoPorId(id)
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    private fun validarPresupuesto(presupuesto: Presupuesto) {
        require(presupuesto.montoLimite > 0) {
            "El límite del presupuesto debe ser mayor a 0"
        }
        require(presupuesto.mes in 1..12) {
            "El mes debe estar entre 1 y 12"
        }
        require(presupuesto.anio >= 2000) {
            "El año debe ser válido"
        }
        require(presupuesto.categoria.isNotBlank()) {
            "La categoría no puede estar vacía"
        }
    }
    private fun obtenerMesAnioActual(): Pair<Int, Int> {
        val calendario = Calendar.getInstance()
        val mes = calendario.get(Calendar.MONTH) + 1
        val anio = calendario.get(Calendar.YEAR)
        return Pair(mes, anio)
    }
    private fun obtenerRangoMes(mes: Int, anio: Int): Pair<Date, Date> {
        val calendario = Calendar.getInstance()
        calendario.set(Calendar.YEAR, anio)
        calendario.set(Calendar.MONTH, mes - 1)
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
