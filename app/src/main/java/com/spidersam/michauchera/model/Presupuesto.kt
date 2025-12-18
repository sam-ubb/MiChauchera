package com.spidersam.michauchera.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "presupuestos")
data class Presupuesto(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val categoria: String,
    val montoLimite: Double,
    val mes: Int,
    val anio: Int,
    val fechaCreacion: Date = Date(),
    val activo: Boolean = true
) {
    fun calcularPorcentajeUsado(gastoActual: Double): Double {
        return if (montoLimite > 0) {
            (gastoActual / montoLimite) * 100.0
        } else {
            0.0
        }
    }

    fun calcularMontoDisponible(gastoActual: Double): Double {
        return montoLimite - gastoActual
    }

    fun estaExcedido(gastoActual: Double): Boolean {
        return gastoActual > montoLimite
    }
}

data class PresupuestoConGasto(
    val presupuesto: Presupuesto,
    val gastoActual: Double
) {
    val porcentajeUsado: Double
        get() = presupuesto.calcularPorcentajeUsado(gastoActual)

    val montoDisponible: Double
        get() = presupuesto.calcularMontoDisponible(gastoActual)

    val estaExcedido: Boolean
        get() = presupuesto.estaExcedido(gastoActual)

    val nivelAlerta: NivelAlertaPresupuesto
        get() = when {
            porcentajeUsado >= 100 -> NivelAlertaPresupuesto.ROJO
            porcentajeUsado >= 90 -> NivelAlertaPresupuesto.NARANJA
            porcentajeUsado >= 70 -> NivelAlertaPresupuesto.AMARILLO
            else -> NivelAlertaPresupuesto.VERDE
        }
}

enum class NivelAlertaPresupuesto {
    VERDE,
    AMARILLO,
    NARANJA,
    ROJO
}

