package com.spidersam.michauchera.utils

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

object FormatosApp {

    private val localeChileno = Locale.forLanguageTag("es-CL")

    val formatoMoneda: NumberFormat = NumberFormat.getInstance(localeChileno)
    val formatoFecha: SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    fun formatearMoneda(valor: Double): String {
        return "$${formatoMoneda.format(valor.toLong())}"
    }

    fun formatearMonedaConDecimales(valor: Double): String {
        return "$${formatoMoneda.format(valor.toLong())}.00"
    }
}

