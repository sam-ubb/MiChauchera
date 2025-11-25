package com.spidersam.michauchera.model

object RepositorioTransacciones {

    private val listaInterna = mutableListOf<Transaccion>()

    val transacciones: List<Transaccion>
        get() = listaInterna

    fun agregarTransaccion(transaccion: Transaccion) {
        listaInterna.add(transaccion)
    }

    fun limpiar() {
        listaInterna.clear()
    }
}
