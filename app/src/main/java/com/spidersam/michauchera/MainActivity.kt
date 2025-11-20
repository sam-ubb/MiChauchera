package com.spidersam.michauchera

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    // Elementos de la UI
    private lateinit var tvTitulo: TextView
    private lateinit var tvSaldoTotal: TextView
    private lateinit var tvIngresos: TextView
    private lateinit var tvGastos: TextView
    private lateinit var tvBalanceMes: TextView
    private lateinit var tvDesglose: TextView
    private lateinit var tvMesada: TextView
    private lateinit var tvEducacion: TextView
    private lateinit var btnAddTransaction: Button
    private lateinit var btnHistorial: Button

    // Variables para gestionar las transacciones (simuladas por ahora)
    private var saldoTotal: Int = 1082500
    private var ingresosMes: Int = 350000
    private var gastosMes: Int = 467500
    private var balanceMes: Int = ingresosMes - gastosMes

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializacion de los elementos de la UI
        tvTitulo = findViewById(R.id.tvTitulo)
        tvSaldoTotal = findViewById(R.id.tvSaldoTotal)
        tvIngresos = findViewById(R.id.tvIngresos)
        tvGastos = findViewById(R.id.tvGastos)
        tvBalanceMes = findViewById(R.id.tvBalanceMes)
        tvDesglose = findViewById(R.id.tvDesglose)
        tvMesada = findViewById(R.id.tvMesada)
        tvEducacion = findViewById(R.id.tvEducacion)
        btnAddTransaction = findViewById(R.id.btnAddTransaction)
        btnHistorial = findViewById(R.id.btnHistorial)

        // Mostrar el saldo y el balance inicial en la UI (la funcion ta mas abajo)
        actualizarUI()

        // Config del boton para añadir una transaccion
        btnAddTransaction.setOnClickListener {
        }

        // Config del boton para ver el historial
        btnHistorial.setOnClickListener {
        }
    }

    // Funcion para actualizar la UI con los valores actuales de saldo, balance, ingresos y gastos
    private fun actualizarUI() {
        tvSaldoTotal.text = "Saldo Total: $${"%.2f".format(saldoTotal)}"
        tvIngresos.text = "Ingresos del Mes: $${"%.2f".format(ingresosMes)}"
        tvGastos.text = "Gastos del Mes: $${"%.2f".format(gastosMes)}"
        tvBalanceMes.text = "Balance del Mes: $${"%.2f".format(balanceMes)}"
    }
}
