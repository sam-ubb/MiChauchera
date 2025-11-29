package com.spidersam.michauchera.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.spidersam.michauchera.data.BaseDeDatos
import com.spidersam.michauchera.data.EstadisticasMensuales
import com.spidersam.michauchera.data.RepositorioTransacciones
import com.spidersam.michauchera.model.Transaccion
import com.spidersam.michauchera.model.TipoTransaccion
import kotlinx.coroutines.launch

class TransaccionViewModel(aplicacion: Application) : AndroidViewModel(aplicacion) {

    private val repositorio: RepositorioTransacciones

    val todasLasTransacciones: LiveData<List<Transaccion>>
    val balanceTotal: LiveData<Double>
    val totalIngresos: LiveData<Double>
    val totalGastos: LiveData<Double>

    private val _estadoOperacion = MutableLiveData<EstadoOperacion>()
    val estadoOperacion: LiveData<EstadoOperacion> = _estadoOperacion

    private val _estadisticasMensuales = MutableLiveData<EstadisticasMensuales>()
    val estadisticasMensuales: LiveData<EstadisticasMensuales> = _estadisticasMensuales

    init {
        val transaccionDao = BaseDeDatos.obtenerBaseDeDatos(aplicacion).transaccionDao()
        repositorio = RepositorioTransacciones(transaccionDao)
        todasLasTransacciones = repositorio.todasLasTransacciones
        balanceTotal = repositorio.balanceTotal
        totalIngresos = repositorio.totalIngresos
        totalGastos = repositorio.totalGastos

        cargarEstadisticasMensuales()
    }

    fun insertarTransaccion(transaccion: Transaccion) = viewModelScope.launch {
        val resultado = repositorio.insertarTransaccion(transaccion)
        if (resultado.isSuccess) {
            _estadoOperacion.value = EstadoOperacion.Exito("Transacción agregada exitosamente")
            cargarEstadisticasMensuales()
        } else {
            _estadoOperacion.value = EstadoOperacion.Error(
                resultado.exceptionOrNull()?.message ?: "Error al agregar transacción"
            )
        }
    }

    fun actualizarTransaccion(transaccion: Transaccion) = viewModelScope.launch {
        val resultado = repositorio.actualizarTransaccion(transaccion)
        if (resultado.isSuccess) {
            _estadoOperacion.value = EstadoOperacion.Exito("Transacción actualizada exitosamente")
            cargarEstadisticasMensuales()
        } else {
            _estadoOperacion.value = EstadoOperacion.Error(
                resultado.exceptionOrNull()?.message ?: "Error al actualizar transacción"
            )
        }
    }

    fun eliminarTransaccion(transaccion: Transaccion) = viewModelScope.launch {
        val resultado = repositorio.eliminarTransaccion(transaccion)
        if (resultado.isSuccess) {
            _estadoOperacion.value = EstadoOperacion.Exito("Transacción eliminada exitosamente")
            cargarEstadisticasMensuales()
        } else {
            _estadoOperacion.value = EstadoOperacion.Error(
                resultado.exceptionOrNull()?.message ?: "Error al eliminar transacción"
            )
        }
    }

    fun eliminarTransaccionPorId(id: Long) = viewModelScope.launch {
        val resultado = repositorio.eliminarTransaccionPorId(id)
        if (resultado.isSuccess) {
            _estadoOperacion.value = EstadoOperacion.Exito("Transacción eliminada exitosamente")
            cargarEstadisticasMensuales()
        } else {
            _estadoOperacion.value = EstadoOperacion.Error(
                resultado.exceptionOrNull()?.message ?: "Error al eliminar transacción"
            )
        }
    }

    fun obtenerTransaccionPorId(id: Long, callback: (Transaccion?) -> Unit) = viewModelScope.launch {
        val transaccion = repositorio.obtenerTransaccionPorId(id)
        callback(transaccion)
    }

    fun obtenerTransaccionesPorTipo(tipo: TipoTransaccion): LiveData<List<Transaccion>> {
        return repositorio.obtenerTransaccionesPorTipo(tipo)
    }

    fun obtenerTransaccionesMesActual(): LiveData<List<Transaccion>> {
        return repositorio.obtenerTransaccionesMesActual()
    }

    private fun cargarEstadisticasMensuales() = viewModelScope.launch {
        val estadisticas = repositorio.obtenerEstadisticasMensuales()
        _estadisticasMensuales.value = estadisticas
    }

    fun limpiarEstadoOperacion() {
        _estadoOperacion.value = EstadoOperacion.Ninguno
    }

    sealed class EstadoOperacion {
        object Ninguno : EstadoOperacion()
        data class Exito(val mensaje: String) : EstadoOperacion()
        data class Error(val mensaje: String) : EstadoOperacion()
    }
}

