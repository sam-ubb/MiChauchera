package com.spidersam.michauchera

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

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

        // Inicializacion de los elementos de la UI
        grupoTipoTransaccion = findViewById(R.id.grupoTipoTransaccion)
        campoMonto = findViewById(R.id.etMonto)
        spinnerCategoria = findViewById(R.id.spinnerCategoria)
        campoFecha = findViewById(R.id.etFecha)
        campoDescripcion = findViewById(R.id.etDescripcion)
        botonCancelar = findViewById(R.id.btnCancelar)
        botonGuardar = findViewById(R.id.btnGuardar)

        // Config del boton Cancelar
        botonCancelar.setOnClickListener {
            finish()
        }

        // Config del boton Guardar
        botonGuardar.setOnClickListener {
            val tipoTransaccion = findViewById<RadioButton>(grupoTipoTransaccion.checkedRadioButtonId).text.toString()
            val monto = campoMonto.text.toString().toIntOrNull() ?: 0
            val categoria = spinnerCategoria.selectedItem.toString()
            val fecha = campoFecha.text.toString()
            val descripcion = campoDescripcion.text.toString()

            // Por ahora solo mostramos los datos en la consola o actualizamos la UI

            if (monto == 0 || categoria == "Seleccionar una categoría" || fecha.isEmpty()) {
                // Mostrar un mensaje de error si faltan datos
                Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
            } else {
                // Mostrar los datos de la transaccion
                println("Tipo: $tipoTransaccion, Monto: $monto, Categoría: $categoria, Fecha: $fecha, Descripción: $descripcion")

                // Volver a la actividad principal (o actualizar la UI)
                finish()
            }
        }
    }
}
