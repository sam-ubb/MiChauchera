package com.spidersam.michauchera.utils

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.spidersam.michauchera.R
import com.spidersam.michauchera.model.TipoTransaccion
import java.util.Date

@BindingAdapter("montoFormateado")
fun TextView.setMontoFormateado(monto: Double?) {
    monto?.let {
        text = FormatosApp.formatearMoneda(it)
    }
}

@BindingAdapter("fechaFormateada")
fun TextView.setFechaFormateada(fecha: Date?) {
    fecha?.let {
        text = FormatosApp.formatoFecha.format(it)
    }
}

@BindingAdapter("montoConColor")
fun TextView.setMontoConColor(tipo: TipoTransaccion?) {
    tipo?.let {
        val colorRes = when (it) {
            TipoTransaccion.INGRESO -> R.color.income_green
            TipoTransaccion.GASTO -> R.color.expense_red
        }
        setTextColor(context.getColor(colorRes))
    }
}

