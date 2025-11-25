package com.spidersam.michauchera

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.spidersam.michauchera.model.RepositorioTransacciones
import com.spidersam.michauchera.model.TipoTransaccion

class MainActivity : AppCompatActivity() {

    // Elementos de la UI
    private lateinit var tvTitulo: TextView
    private lateinit var tvSaldoTotal: TextView
    private lateinit var tvIngresos: TextView
    private lateinit var tvGastos: TextView
    private lateinit var tvBalanceMes: TextView
    private lateinit var tvDesglose: TextView
    private lateinit var tvEducacion: TextView
    private lateinit var btnAddTransaction: Button
    private lateinit var btnHistorial: Button

    // Datos base
    private val saldoInicial: Int = 1082500

    // Variables calculadas
    private var saldoTotal: Int = saldoInicial
    private var ingresosMes: Int = 0
    private var gastosMes: Int = 0
    private var balanceMes: Int = 0

    // Ejemplo de desglose por una categoría concreta
    private var totalEducacion: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // elementos de la UI
        tvTitulo = findViewById(R.id.tvTitulo)
        tvSaldoTotal = findViewById(R.id.tvSaldoTotal)
        tvIngresos = findViewById(R.id.tvIngresos)
        tvGastos = findViewById(R.id.tvGastos)
        tvBalanceMes = findViewById(R.id.tvBalanceMes)
        tvDesglose = findViewById(R.id.tvDesglose)
        tvEducacion = findViewById(R.id.tvEducacion)
        btnAddTransaction = findViewById(R.id.btnAddTransaction)
        btnHistorial = findViewById(R.id.btnHistorial)

        btnAddTransaction.setOnClickListener {
            startActivity(Intent(this, NuevaTransaccionActivity::class.java))
        }

        btnHistorial.setOnClickListener {
            startActivity(Intent(this, HistorialActivity::class.java))
        }

        actualizarDatos()
        actualizarUI()
    }

    override fun onResume() {
        super.onResume()
        actualizarDatos()
        actualizarUI()
    }

    private fun actualizarDatos() {
        val transacciones = RepositorioTransacciones.transacciones

        ingresosMes = transacciones
            .filter { it.tipo == TipoTransaccion.INGRESO }
            .sumOf { it.monto }

        gastosMes = transacciones
            .filter { it.tipo == TipoTransaccion.GASTO }
            .sumOf { it.monto }

        balanceMes = ingresosMes - gastosMes
        saldoTotal = saldoInicial + balanceMes

        // Solo estamos mostrando ejemplo para Educación
        totalEducacion = calcularTotalPorCategoria("Educación")
    }

    // Suma ingresos como positivos y gastos como negativos
    private fun calcularTotalPorCategoria(nombreCategoria: String): Int {
        val transacciones = RepositorioTransacciones.transacciones
        return transacciones
            .filter { it.categoria == nombreCategoria }
            .sumOf { trans ->
                if (trans.tipo == TipoTransaccion.INGRESO) trans.monto else -trans.monto
            }
    }

    private fun actualizarUI() {
        tvSaldoTotal.text = "Saldo Total: $$saldoTotal"
        tvIngresos.text = "Ingresos del Mes: $$ingresosMes"
        tvGastos.text = "Gastos del Mes: $$gastosMes"
        tvBalanceMes.text = "Balance del Mes: $$balanceMes"

        tvEducacion.text = "Educación: $$totalEducacion"
    }
}
