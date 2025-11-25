package com.spidersam.michauchera.model

enum class TipoTransaccion {
    INGRESO,
    GASTO
}

data class Transaccion(
    val tipo: TipoTransaccion,
    val monto: Int,
    val categoria: String,
    val fecha: String,
    val descripcion: String?
)
