package com.spidersam.michauchera

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.spidersam.michauchera.model.RepositorioTransacciones
import com.spidersam.michauchera.model.TipoTransaccion
import com.spidersam.michauchera.model.Transaccion

class NuevaTransaccionActivity : AppCompatActivity() {

    // Elementos de la UI
    private lateinit var grupoTipoTransaccion: RadioGroup
    private lateinit var campoMonto: EditText
    private lateinit var spinnerCategoria: Spinner
    private lateinit var campoFecha: EditText
    private lateinit var campoDescripcion: EditText
    private lateinit var botonCancelar: Button
    private lateinit var botonGuardar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nueva_transaccion)

        // Inicializamos los elementos de la UI
        grupoTipoTransaccion = findViewById(R.id.grupoTipoTransaccion)
        campoMonto = findViewById(R.id.campoMonto)
        spinnerCategoria = findViewById(R.id.spinnerCategoria)
        campoFecha = findViewById(R.id.campoFecha)
        campoDescripcion = findViewById(R.id.campoDescripcion)
        botonCancelar = findViewById(R.id.botonCancelar)
        botonGuardar = findViewById(R.id.botonGuardar)

        configurarSpinnerCategorias()
        botonCancelar.setOnClickListener {
            finish()
        }
        botonGuardar.setOnClickListener {
            guardarTransaccion()
        }
    }

    private fun configurarSpinnerCategorias() {
        val categorias = listOf(
            "Educación",
            "Alimentación",
            "Servicios",
            "Salud",
            "Entretenimiento",
            "Otros"
        )

        val adaptador = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            categorias
        )
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategoria.adapter = adaptador
    }

    private fun guardarTransaccion() {
        val idSeleccionado = grupoTipoTransaccion.checkedRadioButtonId
        if (idSeleccionado == -1) {
            Toast.makeText(this, "Seleccione si es ingreso o gasto", Toast.LENGTH_SHORT).show()
            return
        }

        val textoTipo = findViewById<RadioButton>(idSeleccionado).text.toString()
        val textoMonto = campoMonto.text.toString()
        val categoria = spinnerCategoria.selectedItem?.toString() ?: ""
        val fecha = campoFecha.text.toString()
        val descripcion = campoDescripcion.text.toString()

        if (textoMonto.isBlank() || categoria.isBlank() || fecha.isBlank()) {
            Toast.makeText(this, "Por favor, complete monto, categoría y fecha", Toast.LENGTH_SHORT).show()
            return
        }

        val monto = textoMonto.toIntOrNull()
        if (monto == null || monto <= 0) {
            Toast.makeText(this, "El monto debe ser un entero mayor que 0", Toast.LENGTH_SHORT).show()
            return
        }

        // Convertimos texto a enum
        val tipo = if (textoTipo == "Ingreso") {
            TipoTransaccion.INGRESO
        } else {
            TipoTransaccion.GASTO
        }

        // Creamos el objeto Transaccion
        val transaccion = Transaccion(
            tipo = tipo,
            monto = monto,
            categoria = categoria,
            fecha = fecha,
            descripcion = if (descripcion.isBlank()) null else descripcion
        )

        // Guardamos en memoria
        RepositorioTransacciones.agregarTransaccion(transaccion)

        Toast.makeText(this, "Transacción guardada", Toast.LENGTH_SHORT).show()
        finish()
    }
}
