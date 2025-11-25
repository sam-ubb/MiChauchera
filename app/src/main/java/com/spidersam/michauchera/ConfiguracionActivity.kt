package com.spidersam.michauchera

import android.os.Bundle
import android.widget.EditText
import android.widget.Spinner
import android.widget.ArrayAdapter
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ConfiguracionActivity : AppCompatActivity() {

    private lateinit var campoNombre: EditText
    private lateinit var campoCorreo: EditText
    private lateinit var switchTemaOscuro: Switch
    private lateinit var switchNotificaciones: Switch
    private lateinit var spinnerMoneda: Spinner
    private lateinit var textoDetalleTema: TextView
    private lateinit var textoDetalleNotificaciones: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuracion)

        // UI
        campoNombre = findViewById(R.id.campoNombre)
        campoCorreo = findViewById(R.id.campoCorreo)
        switchTemaOscuro = findViewById(R.id.switchTemaOscuro)
        switchNotificaciones = findViewById(R.id.switchNotificaciones)
        spinnerMoneda = findViewById(R.id.spinnerMoneda)
        textoDetalleTema = findViewById(R.id.textoDetalleTema)
        textoDetalleNotificaciones = findViewById(R.id.textoDetalleNotificaciones)

        configurarSpinnerMoneda()
        configurarSwitchTema()
        configurarSwitchNotificaciones()
    }

    private fun configurarSpinnerMoneda() {
        // Ejemplo simple de lista de monedas
        val listaMonedas = listOf("Peso Chileno (CLP)", "Dólar (USD)", "Euro (EUR)")
        val adaptador = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            listaMonedas
        )
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerMoneda.adapter = adaptador
    }

    private fun configurarSwitchTema() {
        switchTemaOscuro.setOnCheckedChangeListener { _, estaMarcado ->
            if (estaMarcado) {
                textoDetalleTema.text = "Modo oscuro"
            } else {
                textoDetalleTema.text = "Modo claro"
            }
        }
    }

    private fun configurarSwitchNotificaciones() {
        switchNotificaciones.setOnCheckedChangeListener { _, estaMarcado ->
            textoDetalleNotificaciones.text =
                if (estaMarcado) "Activadas" else "Desactivadas"
        }
    }
}
