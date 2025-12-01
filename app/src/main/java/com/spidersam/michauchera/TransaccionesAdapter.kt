package com.spidersam.michauchera

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.spidersam.michauchera.model.Transaccion
import com.spidersam.michauchera.model.TipoTransaccion
import com.spidersam.michauchera.utils.FormatosApp

class TransaccionesAdapter(
    private val onEditClick: (Transaccion) -> Unit,
    private val onDeleteClick: (Transaccion) -> Unit
) : ListAdapter<Transaccion, TransaccionesAdapter.TransaccionViewHolder>(TransaccionDiffCallback()) {

    private val iconosPorCategoria = mapOf(
        "Mesada" to R.drawable.ic_category_mesada,
        "Educación" to R.drawable.ic_category_educacion,
        "Alimentación" to R.drawable.ic_category_alimentacion,
        "Servicios" to R.drawable.ic_category_servicios,
        "Salud" to R.drawable.ic_category_salud,
        "Entretenimiento" to R.drawable.ic_category_entretenimiento,
        "Transporte" to R.drawable.ic_category_transporte,
        "Vivienda" to R.drawable.ic_category_vivienda,
        "Otros" to R.drawable.ic_category_default
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransaccionViewHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaccion, parent, false)
        return TransaccionViewHolder(vista, onEditClick, onDeleteClick)
    }

    override fun onBindViewHolder(holder: TransaccionViewHolder, position: Int) {
        val transaccion = getItem(position)
        holder.bind(transaccion, iconosPorCategoria)
    }

    class TransaccionViewHolder(
        itemView: View,
        private val onEditClick: (Transaccion) -> Unit,
        private val onDeleteClick: (Transaccion) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val iconoCategoria: ImageView = itemView.findViewById(R.id.iconoCategoria)
        private val tvCategoria: TextView = itemView.findViewById(R.id.tvCategoria)
        private val tvMonto: TextView = itemView.findViewById(R.id.tvMonto)
        private val tvFecha: TextView = itemView.findViewById(R.id.tvFecha)
        private val tvDescripcion: TextView? = itemView.findViewById(R.id.tvDescripcion)
        private val badgeTipo: TextView = itemView.findViewById(R.id.badgeTipo)
        private val btnEditar: ImageButton? = itemView.findViewById(R.id.btnEditar)
        private val btnEliminar: ImageButton? = itemView.findViewById(R.id.btnEliminar)

        fun bind(transaccion: Transaccion, iconosPorCategoria: Map<String, Int>) {
            val iconoRes = iconosPorCategoria[transaccion.categoria] ?: R.drawable.ic_category_default
            iconoCategoria.setImageResource(iconoRes)

            tvCategoria.text = transaccion.categoria

            tvMonto.text = FormatosApp.formatearMoneda(transaccion.monto)

            val esIngreso = transaccion.tipo == TipoTransaccion.INGRESO
            val color = if (esIngreso) {
                ContextCompat.getColor(itemView.context, R.color.income_green)
            } else {
                ContextCompat.getColor(itemView.context, R.color.expense_red)
            }
            tvMonto.setTextColor(color)

            badgeTipo.text = if (esIngreso) "Ingreso" else "Gasto"
            badgeTipo.setTextColor(color)
            badgeTipo.setBackgroundColor(
                if (esIngreso) Color.parseColor("#E8F5E9") else Color.parseColor("#FFEBEE")
            )

            tvDescripcion?.text = transaccion.descripcion
            tvDescripcion?.visibility = if (transaccion.descripcion.isNotBlank()) {
                View.VISIBLE
            } else {
                View.GONE
            }

            btnEditar?.setOnClickListener { onEditClick(transaccion) }
            btnEliminar?.setOnClickListener { onDeleteClick(transaccion) }
            itemView.setOnClickListener { onEditClick(transaccion) }
        }
    }

    class TransaccionDiffCallback : DiffUtil.ItemCallback<Transaccion>() {
        override fun areItemsTheSame(oldItem: Transaccion, newItem: Transaccion): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Transaccion, newItem: Transaccion): Boolean {
            return oldItem == newItem
        }
    }
}
