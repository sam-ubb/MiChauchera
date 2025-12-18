package com.spidersam.michauchera.viewmodel
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.spidersam.michauchera.data.BaseDeDatos
import com.spidersam.michauchera.data.RepositorioPresupuestos
import com.spidersam.michauchera.model.Presupuesto
import com.spidersam.michauchera.model.PresupuestoConGasto
import com.spidersam.michauchera.utils.GestionadorPresupuestosWorker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.Calendar
class PresupuestoViewModel(aplicacion: Application) : AndroidViewModel(aplicacion) {
    private val repositorio: RepositorioPresupuestos
    val presupuestosActivos: LiveData<List<Presupuesto>>
    val presupuestosActivosFlow: Flow<List<Presupuesto>>
    private val _presupuestosMesActualConGasto = MutableLiveData<List<PresupuestoConGasto>>()
    val presupuestosMesActualConGasto: LiveData<List<PresupuestoConGasto>> = _presupuestosMesActualConGasto
    private val _presupuestosExcedidos = MutableLiveData<List<PresupuestoConGasto>>()
    val presupuestosExcedidos: LiveData<List<PresupuestoConGasto>> = _presupuestosExcedidos
    private val _presupuestosEnAdvertencia = MutableLiveData<List<PresupuestoConGasto>>()
    val presupuestosEnAdvertencia: LiveData<List<PresupuestoConGasto>> = _presupuestosEnAdvertencia
    private val _estadoOperacion = MutableLiveData<EstadoOperacion>()
    val estadoOperacion: LiveData<EstadoOperacion> = _estadoOperacion
    private val _presupuestoTotalMes = MutableLiveData<Double>()
    val presupuestoTotalMes: LiveData<Double> = _presupuestoTotalMes
    init {
        val database = BaseDeDatos.obtenerBaseDeDatos(aplicacion)
        repositorio = RepositorioPresupuestos(
            presupuestoDao = database.presupuestoDao(),
            transaccionDao = database.transaccionDao()
        )
        presupuestosActivos = repositorio.presupuestosActivos
        presupuestosActivosFlow = repositorio.presupuestosActivosFlow
        cargarPresupuestosMesActualConGasto()
        cargarPresupuestosExcedidos()
        cargarPresupuestoTotalMes()
    }
    fun crearPresupuesto(presupuesto: Presupuesto) = viewModelScope.launch {
        val resultado = repositorio.crearPresupuesto(presupuesto)
        if (resultado.isSuccess) {
            _estadoOperacion.value = EstadoOperacion.Exito("Presupuesto creado exitosamente")
            actualizarDatos()
        } else {
            _estadoOperacion.value = EstadoOperacion.Error(
                resultado.exceptionOrNull()?.message ?: "Error al crear presupuesto"
            )
        }
    }
    fun crearPresupuestoMesActual(categoria: String, montoLimite: Double) = viewModelScope.launch {
        val calendario = Calendar.getInstance()
        val mes = calendario.get(Calendar.MONTH) + 1
        val anio = calendario.get(Calendar.YEAR)
        val presupuesto = Presupuesto(
            categoria = categoria,
            montoLimite = montoLimite,
            mes = mes,
            anio = anio
        )
        crearPresupuesto(presupuesto)
    }
    fun crearPresupuestosIniciales(montoDefecto: Double = 100000.0) = viewModelScope.launch {
        val calendario = Calendar.getInstance()
        val mes = calendario.get(Calendar.MONTH) + 1
        val anio = calendario.get(Calendar.YEAR)
        val categoriasComunes = listOf(
            "Alimentación" to montoDefecto,
            "Transporte" to montoDefecto * 0.5,
            "Vivienda" to montoDefecto * 1.5,
            "Entretenimiento" to montoDefecto * 0.3,
            "Salud" to montoDefecto * 0.4
        )
        val presupuestos = categoriasComunes.map { (categoria, monto) ->
            Presupuesto(
                categoria = categoria,
                montoLimite = monto,
                mes = mes,
                anio = anio
            )
        }
        val resultado = repositorio.crearPresupuestos(presupuestos)
        if (resultado.isSuccess) {
            _estadoOperacion.value = EstadoOperacion.Exito("Presupuestos iniciales creados")
            actualizarDatos()
        } else {
            _estadoOperacion.value = EstadoOperacion.Error(
                resultado.exceptionOrNull()?.message ?: "Error al crear presupuestos iniciales"
            )
        }
    }
    fun obtenerPresupuestoPorId(id: Long, callback: (Presupuesto?) -> Unit) = viewModelScope.launch {
        val presupuesto = repositorio.obtenerPresupuestoPorId(id)
        callback(presupuesto)
    }
    fun cargarPresupuestosMesActualConGasto() = viewModelScope.launch {
        val calendario = Calendar.getInstance()
        val mes = calendario.get(Calendar.MONTH) + 1
        val anio = calendario.get(Calendar.YEAR)
        val presupuestos = repositorio.obtenerPresupuestosConGasto(mes, anio)
        _presupuestosMesActualConGasto.value = presupuestos
    }
    fun cargarPresupuestosPorMes(mes: Int, anio: Int) = viewModelScope.launch {
        val presupuestos = repositorio.obtenerPresupuestosConGasto(mes, anio)
        _presupuestosMesActualConGasto.value = presupuestos
    }
    fun cargarPresupuestosExcedidos() = viewModelScope.launch {
        val excedidos = repositorio.obtenerPresupuestosExcedidos()
        _presupuestosExcedidos.value = excedidos
    }
    fun cargarPresupuestosEnAdvertencia() = viewModelScope.launch {
        val advertencia = repositorio.obtenerPresupuestosEnAdvertencia()
        _presupuestosEnAdvertencia.value = advertencia
    }
    fun cargarPresupuestoTotalMes() = viewModelScope.launch {
        val total = repositorio.obtenerPresupuestoTotalMesActual()
        _presupuestoTotalMes.value = total
    }
    fun obtenerGastoActualCategoria(categoria: String, callback: (Double) -> Unit) = viewModelScope.launch {
        val calendario = Calendar.getInstance()
        val mes = calendario.get(Calendar.MONTH) + 1
        val anio = calendario.get(Calendar.YEAR)
        val gasto = repositorio.obtenerGastoActualCategoria(categoria, mes, anio)
        callback(gasto)
    }
    fun actualizarPresupuesto(presupuesto: Presupuesto) = viewModelScope.launch {
        val resultado = repositorio.actualizarPresupuesto(presupuesto)
        if (resultado.isSuccess) {
            _estadoOperacion.value = EstadoOperacion.Exito("Presupuesto actualizado exitosamente")
            actualizarDatos()
        } else {
            _estadoOperacion.value = EstadoOperacion.Error(
                resultado.exceptionOrNull()?.message ?: "Error al actualizar presupuesto"
            )
        }
    }
    fun actualizarMontoLimite(id: Long, nuevoLimite: Double) = viewModelScope.launch {
        val resultado = repositorio.actualizarMontoLimite(id, nuevoLimite)
        if (resultado.isSuccess) {
            _estadoOperacion.value = EstadoOperacion.Exito("Límite actualizado exitosamente")
            actualizarDatos()
        } else {
            _estadoOperacion.value = EstadoOperacion.Error(
                resultado.exceptionOrNull()?.message ?: "Error al actualizar límite"
            )
        }
    }
    fun archivarPresupuesto(id: Long) = viewModelScope.launch {
        val resultado = repositorio.archivarPresupuesto(id)
        if (resultado.isSuccess) {
            _estadoOperacion.value = EstadoOperacion.Exito("Presupuesto archivado")
            actualizarDatos()
        } else {
            _estadoOperacion.value = EstadoOperacion.Error(
                resultado.exceptionOrNull()?.message ?: "Error al archivar presupuesto"
            )
        }
    }
    fun eliminarPresupuesto(presupuesto: Presupuesto) = viewModelScope.launch {
        val resultado = repositorio.eliminarPresupuesto(presupuesto)
        if (resultado.isSuccess) {
            _estadoOperacion.value = EstadoOperacion.Exito("Presupuesto eliminado exitosamente")
            actualizarDatos()
        } else {
            _estadoOperacion.value = EstadoOperacion.Error(
                resultado.exceptionOrNull()?.message ?: "Error al eliminar presupuesto"
            )
        }
    }
    fun eliminarPresupuestoPorId(id: Long) = viewModelScope.launch {
        val resultado = repositorio.eliminarPresupuestoPorId(id)
        if (resultado.isSuccess) {
            _estadoOperacion.value = EstadoOperacion.Exito("Presupuesto eliminado exitosamente")
            actualizarDatos()
        } else {
            _estadoOperacion.value = EstadoOperacion.Error(
                resultado.exceptionOrNull()?.message ?: "Error al eliminar presupuesto"
            )
        }
    }
    private fun actualizarDatos() {
        cargarPresupuestosMesActualConGasto()
        cargarPresupuestosExcedidos()
        cargarPresupuestosEnAdvertencia()
        cargarPresupuestoTotalMes()
    }
    fun limpiarEstadoOperacion() {
        _estadoOperacion.value = EstadoOperacion.Ninguno
    }
    fun iniciarMonitoreoPeriodico() {
        GestionadorPresupuestosWorker.programarMonitoreoPeriodico(getApplication())
    }
    fun ejecutarMonitoreoInmediato() {
        GestionadorPresupuestosWorker.programarMonitoreoInmediato(getApplication())
    }
    fun detenerMonitoreoPeriodico() {
        GestionadorPresupuestosWorker.cancelarMonitoreoPeriodico(getApplication())
    }
    fun obtenerEstadoMonitoreo() =
        GestionadorPresupuestosWorker.obtenerEstadoMonitoreo(getApplication())
    fun estaMonitoreoActivo(callback: (Boolean) -> Unit) {
        GestionadorPresupuestosWorker.estaMonitoreoActivo(getApplication(), callback)
    }
    sealed class EstadoOperacion {
        object Ninguno : EstadoOperacion()
        data class Exito(val mensaje: String) : EstadoOperacion()
        data class Error(val mensaje: String) : EstadoOperacion()
        object Cargando : EstadoOperacion()
    }
}
