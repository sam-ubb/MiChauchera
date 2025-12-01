package com.spidersam.michauchera.ui.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.spidersam.michauchera.R
import com.spidersam.michauchera.model.TipoTransaccion
import com.spidersam.michauchera.model.Transaccion
import com.spidersam.michauchera.viewmodel.TransaccionViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class TransaccionFormFragment : Fragment() {

    private val viewModel: TransaccionViewModel by activityViewModels()
    private val args: TransaccionFormFragmentArgs by navArgs()

    private lateinit var etMonto: EditText
    private lateinit var etDescripcion: EditText
    private lateinit var etFecha: EditText
    private lateinit var spinnerCategoria: Spinner
    private lateinit var rgTipo: RadioGroup
    private lateinit var btnGuardar: Button
    private lateinit var btnCancelar: Button

    private var transaccionId: Long = 0L
    private var fechaSeleccionada: Date = Date()

    private val categorias = listOf(
        "Alimentación", "Transporte", "Vivienda", "Salud",
        "Educación", "Entretenimiento", "Servicios", "Otros"
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_transaccion_form, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        transaccionId = args.transaccionId

        inicializarVistas(view)
        configurarSpinnerCategorias()
        configurarListeners()

        if (transaccionId > 0) {
            cargarTransaccion()
        } else {
            actualizarFecha(Date())
        }
    }

    private fun inicializarVistas(view: View) {
        etMonto = view.findViewById(R.id.etMonto)
        etDescripcion = view.findViewById(R.id.etDescripcion)
        etFecha = view.findViewById(R.id.etFecha)
        spinnerCategoria = view.findViewById(R.id.spinnerCategoria)
        rgTipo = view.findViewById(R.id.rgTipo)
        btnGuardar = view.findViewById(R.id.btnGuardar)
        btnCancelar = view.findViewById(R.id.btnCancelar)
    }

    private fun configurarSpinnerCategorias() {
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            categorias
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategoria.adapter = adapter
    }

    private fun configurarListeners() {
        etFecha.setOnClickListener {
            mostrarDatePicker()
        }

        btnGuardar.setOnClickListener {
            guardarTransaccion()
        }

        btnCancelar.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun cargarTransaccion() {
        viewModel.obtenerTransaccionPorId(transaccionId) { transaccion ->
            transaccion?.let {
                etMonto.setText(it.monto.toString())
                etDescripcion.setText(it.descripcion)
                actualizarFecha(it.fecha)

                val posicion = categorias.indexOf(it.categoria)
                if (posicion >= 0) {
                    spinnerCategoria.setSelection(posicion)
                }

                when (it.tipo) {
                    TipoTransaccion.INGRESO -> rgTipo.check(R.id.rbIngreso)
                    TipoTransaccion.GASTO -> rgTipo.check(R.id.rbGasto)
                }
            }
        }
    }

    private fun mostrarDatePicker() {
        val calendario = Calendar.getInstance()
        calendario.time = fechaSeleccionada

        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                val nuevaFecha = Calendar.getInstance()
                nuevaFecha.set(year, month, day)
                actualizarFecha(nuevaFecha.time)
            },
            calendario.get(Calendar.YEAR),
            calendario.get(Calendar.MONTH),
            calendario.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun actualizarFecha(fecha: Date) {
        fechaSeleccionada = fecha
        val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        etFecha.setText(formato.format(fecha))
    }

    private fun guardarTransaccion() {
        val montoTexto = etMonto.text.toString()
        if (montoTexto.isEmpty()) {
            Toast.makeText(requireContext(), "Ingrese un monto", Toast.LENGTH_SHORT).show()
            return
        }

        val monto = montoTexto.toDoubleOrNull()
        if (monto == null || monto <= 0) {
            Toast.makeText(requireContext(), "Ingrese un monto válido", Toast.LENGTH_SHORT).show()
            return
        }

        val descripcion = etDescripcion.text.toString()
        val categoriaSeleccionada = spinnerCategoria.selectedItem.toString()

        val tipo = when (rgTipo.checkedRadioButtonId) {
            R.id.rbIngreso -> TipoTransaccion.INGRESO
            R.id.rbGasto -> TipoTransaccion.GASTO
            else -> {
                Toast.makeText(requireContext(), "Seleccione un tipo", Toast.LENGTH_SHORT).show()
                return
            }
        }

        val transaccion = Transaccion(
            id = transaccionId,
            monto = monto,
            descripcion = descripcion,
            categoria = categoriaSeleccionada,
            tipo = tipo,
            fecha = fechaSeleccionada
        )

        if (transaccionId > 0) {
            viewModel.actualizarTransaccion(transaccion)
        } else {
            viewModel.insertarTransaccion(transaccion)
        }

        findNavController().navigateUp()
    }
}

