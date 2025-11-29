package com.spidersam.michauchera.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
data class Usuario(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nombre: String,
    val email: String = "",
    val monedaPreferida: String = "USD",
    val limiteGastoMensual: Double = 0.0,
    val notificacionesActivas: Boolean = true
)

