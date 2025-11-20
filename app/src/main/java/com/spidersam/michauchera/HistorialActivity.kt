package com.spidersam.michauchera

import android.os.Bundle
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HistorialActivity : AppCompatActivity() {

    private lateinit var etBuscar: EditText
    private lateinit var grupoFiltros: RadioGroup
    private lateinit var etFechaDesde: EditText
    private lateinit var etFechaHasta: EditText
    private lateinit var spinnerOrdenarPor: Spinner
    private lateinit var recyclerViewTransacciones: RecyclerView
    private lateinit var tvResumen: TextView

    private val listaTransacciones = mutableListOf<Transaccion>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historial)

        etBuscar = findViewById<EditText>(R.id.etBuscar)
        grupoFiltros = findViewById<RadioGroup>(R.id.grupoFiltros)
        etFechaDesde = findViewById<EditText>(R.id.etFechaDesde)
        etFechaHasta = findViewById<EditText>(R.id.etFechaHasta)
        spinnerOrdenarPor = findViewById<Spinner>(R.id.spinnerOrdenarPor)
        recyclerViewTransacciones = findViewById<RecyclerView>(R.id.recyclerViewTransacciones)
        tvResumen = findViewById<TextView>(R.id.tvResumen)

        recyclerViewTransacciones.layoutManager = LinearLayoutManager(this)
        val adapter = TransaccionesAdapter(listaTransacciones)
        recyclerViewTransacciones.adapter = adapter

        cargarTransacciones()
        actualizarResumen()
        adapter.notifyDataSetChanged()
    }

    private fun cargarTransacciones() {
        listaTransacciones.add(Transaccion("Entretenimiento", -7500, "Cine", "11/11/2025", "Gasto"))
        listaTransacciones.add(Transaccion("Mesada", 350000, "Proyecto freelance", "09/11/2025", "Ingreso"))
        listaTransacciones.add(Transaccion("Salud", -65000, "Farmacia", "08/11/2025", "Gasto"))
    }

    private fun actualizarResumen() {
        val totalTransacciones = listaTransacciones.size
        val balance = listaTransacciones.sumOf { it.monto }
        tvResumen.text = "Total de transacciones: $totalTransacciones, Balance: $balance"
    }
}
