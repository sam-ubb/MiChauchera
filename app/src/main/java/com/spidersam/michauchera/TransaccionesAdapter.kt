package com.spidersam.michauchera

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.spidersam.michauchera.model.TipoTransaccion
import com.spidersam.michauchera.model.Transaccion

class TransaccionesAdapter(
    private val transacciones: List<Transaccion>
) : RecyclerView.Adapter<TransaccionesAdapter.TransaccionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransaccionViewHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaccion, parent, false)
        return TransaccionViewHolder(vista)
    }

    override fun onBindViewHolder(holder: TransaccionViewHolder, position: Int) {
        val transaccion = transacciones[position]
        holder.bind(transaccion)
    }

    override fun getItemCount(): Int = transacciones.size

    class TransaccionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val textoCategoria: TextView = itemView.findViewById(R.id.textoCategoria)
        private val textoMonto: TextView = itemView.findViewById(R.id.textoMonto)
        private val textoFecha: TextView = itemView.findViewById(R.id.textoFecha)
        private val textoTipo: TextView = itemView.findViewById(R.id.textoTipo)

        fun bind(transaccion: Transaccion) {
            textoCategoria.text = transaccion.categoria
            textoFecha.text = transaccion.fecha

            val signo = if (transaccion.tipo == TipoTransaccion.INGRESO) "+" else "-"
            textoMonto.text = "$signo${transaccion.monto}"

            textoTipo.text = if (transaccion.tipo == TipoTransaccion.INGRESO) {
                "Ingreso"
            } else {
                "Gasto"
            }
        }
    }
}
