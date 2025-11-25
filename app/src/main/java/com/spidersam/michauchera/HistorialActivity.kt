package com.spidersam.michauchera

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.spidersam.michauchera.model.RepositorioTransacciones
import com.spidersam.michauchera.model.TipoTransaccion

class HistorialActivity : AppCompatActivity() {

    private lateinit var recyclerViewTransacciones: RecyclerView
    private lateinit var tvResumen: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historial)

        recyclerViewTransacciones = findViewById(R.id.recyclerViewTransacciones)
        tvResumen = findViewById(R.id.tvResumen)

        recyclerViewTransacciones.layoutManager = LinearLayoutManager(this)

        // Cargamos datos iniciales
        configurarRecycler()
        actualizarResumen()
    }

    override fun onResume() {
        super.onResume()
        // Cada vez que volvemos, refrescamos lista y resumen
        configurarRecycler()
        actualizarResumen()
    }

    private fun configurarRecycler() {
        val listaTransacciones = RepositorioTransacciones.transacciones
        val adapter = TransaccionesAdapter(listaTransacciones)
        recyclerViewTransacciones.adapter = adapter
    }

    private fun actualizarResumen() {
        val listaTransacciones = RepositorioTransacciones.transacciones
        val totalTransacciones = listaTransacciones.size

        val balance = listaTransacciones.sumOf { transaccion ->
            if (transaccion.tipo == TipoTransaccion.INGRESO) {
                transaccion.monto
            } else {
                -transaccion.monto
            }
        }

        tvResumen.text = "Total de transacciones: $totalTransacciones, Balance: $balance"
    }
}
