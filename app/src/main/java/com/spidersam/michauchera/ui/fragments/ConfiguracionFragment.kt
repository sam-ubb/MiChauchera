package com.spidersam.michauchera.ui.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
import com.spidersam.michauchera.R

class ConfiguracionFragment : Fragment() {

    private lateinit var preferencias: SharedPreferences

    private lateinit var campoNombre: TextInputEditText
    private lateinit var campoCorreo: TextInputEditText
    private lateinit var switchTemaOscuro: SwitchMaterial
    private lateinit var switchNotificaciones: SwitchMaterial
    private lateinit var spinnerMoneda: Spinner

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_configuracion, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        preferencias = requireContext().getSharedPreferences("MiChaucheraPrefs", Context.MODE_PRIVATE)

        inicializarVistas(view)
        configurarSpinnerMoneda()
        cargarPreferencias()
        configurarListeners()
    }

    private fun inicializarVistas(view: View) {
        campoNombre = view.findViewById(R.id.campoNombre)
        campoCorreo = view.findViewById(R.id.campoCorreo)
        switchTemaOscuro = view.findViewById(R.id.switchTemaOscuro)
        switchNotificaciones = view.findViewById(R.id.switchNotificaciones)
        spinnerMoneda = view.findViewById(R.id.spinnerMoneda)
    }

    private fun configurarSpinnerMoneda() {
        val monedas = listOf("CLP - Peso Chileno", "USD - Dólar", "EUR - Euro", "ARS - Peso Argentino")

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            monedas
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerMoneda.adapter = adapter
    }

    private fun cargarPreferencias() {
        campoNombre.setText(preferencias.getString("nombre", ""))
        campoCorreo.setText(preferencias.getString("correo", ""))
        switchTemaOscuro.isChecked = preferencias.getBoolean("temaOscuro", false)
        switchNotificaciones.isChecked = preferencias.getBoolean("notificaciones", true)

        val monedaIndex = preferencias.getInt("monedaIndex", 0)
        spinnerMoneda.setSelection(monedaIndex)
    }

    private fun configurarListeners() {
        campoNombre.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                guardarPreferencia("nombre", campoNombre.text.toString())
            }
        }

        campoCorreo.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                guardarPreferencia("correo", campoCorreo.text.toString())
            }
        }

        switchTemaOscuro.setOnCheckedChangeListener { _, isChecked ->
            guardarPreferencia("temaOscuro", isChecked)
        }

        switchNotificaciones.setOnCheckedChangeListener { _, isChecked ->
            guardarPreferencia("notificaciones", isChecked)
        }
    }

    private fun guardarPreferencia(clave: String, valor: String) {
        preferencias.edit().putString(clave, valor).apply()
    }

    private fun guardarPreferencia(clave: String, valor: Boolean) {
        preferencias.edit().putBoolean(clave, valor).apply()
    }

    override fun onPause() {
        super.onPause()
        val monedaIndex = spinnerMoneda.selectedItemPosition
        preferencias.edit().putInt("monedaIndex", monedaIndex).apply()
    }
}

