package com.spidersam.michauchera.ui.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.spidersam.michauchera.R
import com.spidersam.michauchera.TransaccionesAdapter
import com.spidersam.michauchera.model.Transaccion
import com.spidersam.michauchera.model.TipoTransaccion
import com.spidersam.michauchera.utils.FormatosApp
import com.spidersam.michauchera.viewmodel.TransaccionViewModel

class TransaccionesFragment : Fragment() {

    private val viewModel: TransaccionViewModel by activityViewModels()

    private lateinit var recyclerTransacciones: RecyclerView
    private lateinit var fabNuevaTransaccion: FloatingActionButton
    private lateinit var etBuscar: TextInputEditText
    private lateinit var grupoFiltros: RadioGroup
    private lateinit var spinnerOrdenarPor: Spinner
    private lateinit var tvResumen: TextView
    private lateinit var tvEstadoVacio: TextView

    private lateinit var adapter: TransaccionesAdapter
    private var todasLasTransacciones: List<Transaccion> = emptyList()
    private var transaccionesFiltradas: List<Transaccion> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_transacciones, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        inicializarVistas(view)
        configurarSpinnerOrdenamiento()
        configurarRecyclerView()
        observarDatos()
        configurarListeners()
    }

    private fun inicializarVistas(view: View) {
        recyclerTransacciones = view.findViewById(R.id.recyclerTransacciones)
        fabNuevaTransaccion = view.findViewById(R.id.fabNuevaTransaccion)
        etBuscar = view.findViewById(R.id.etBuscar)
        grupoFiltros = view.findViewById(R.id.grupoFiltros)
        spinnerOrdenarPor = view.findViewById(R.id.spinnerOrdenarPor)
        tvResumen = view.findViewById(R.id.tvResumen)
        tvEstadoVacio = view.findViewById(R.id.tvEstadoVacio)
    }

    private fun configurarSpinnerOrdenamiento() {
        val opciones = listOf(
            "Fecha (más reciente)",
            "Fecha (más antiguo)",
            "Monto (mayor a menor)",
            "Monto (menor a mayor)"
        )

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            opciones
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerOrdenarPor.adapter = adapter
    }

    private fun configurarRecyclerView() {
        adapter = TransaccionesAdapter(
            onEditClick = { transaccion ->
                val action = TransaccionesFragmentDirections
                    .actionTransaccionesToTransaccionForm(transaccion.id)
                findNavController().navigate(action)
            },
            onDeleteClick = { transaccion ->
                viewModel.eliminarTransaccion(transaccion)
            }
        )

        recyclerTransacciones.layoutManager = LinearLayoutManager(requireContext())
        recyclerTransacciones.adapter = adapter
    }

    private fun observarDatos() {
        viewModel.todasLasTransacciones.observe(viewLifecycleOwner) { transacciones ->
            todasLasTransacciones = transacciones
            aplicarFiltros()
        }

        viewModel.estadoOperacion.observe(viewLifecycleOwner) { estado ->
            when (estado) {
                is TransaccionViewModel.EstadoOperacion.Exito -> {
                    Toast.makeText(requireContext(), estado.mensaje, Toast.LENGTH_SHORT).show()
                    viewModel.limpiarEstadoOperacion()
                }
                is TransaccionViewModel.EstadoOperacion.Error -> {
                    Toast.makeText(requireContext(), estado.mensaje, Toast.LENGTH_LONG).show()
                    viewModel.limpiarEstadoOperacion()
                }
                else -> {}
            }
        }
    }

    private fun configurarListeners() {
        fabNuevaTransaccion.setOnClickListener {
            val action = TransaccionesFragmentDirections
                .actionTransaccionesToTransaccionForm(0L)
            findNavController().navigate(action)
        }

        etBuscar.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                aplicarFiltros()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        grupoFiltros.setOnCheckedChangeListener { _, _ ->
            aplicarFiltros()
        }
    }

    private fun aplicarFiltros() {
        val textoBusqueda = etBuscar.text.toString().lowercase()

        var listaFiltrada = todasLasTransacciones

        // Filtrar por tipo
        when (grupoFiltros.checkedRadioButtonId) {
            R.id.rbIngresos -> {
                listaFiltrada = listaFiltrada.filter { it.tipo == TipoTransaccion.INGRESO }
            }
            R.id.rbGastos -> {
                listaFiltrada = listaFiltrada.filter { it.tipo == TipoTransaccion.GASTO }
            }
        }

        if (textoBusqueda.isNotEmpty()) {
            listaFiltrada = listaFiltrada.filter { transaccion ->
                transaccion.categoria.lowercase().contains(textoBusqueda) ||
                transaccion.descripcion.lowercase().contains(textoBusqueda) ||
                transaccion.monto.toString().contains(textoBusqueda)
            }
        }

        transaccionesFiltradas = listaFiltrada
        actualizarVista()
    }

    private fun actualizarVista() {
        if (transaccionesFiltradas.isEmpty()) {
            recyclerTransacciones.visibility = View.GONE
            tvEstadoVacio.visibility = View.VISIBLE
        } else {
            recyclerTransacciones.visibility = View.VISIBLE
            tvEstadoVacio.visibility = View.GONE
        }

        adapter.submitList(transaccionesFiltradas)
        actualizarResumen()
    }

    private fun actualizarResumen() {
        val totalTransacciones = transaccionesFiltradas.size
        val ingresos = transaccionesFiltradas
            .filter { it.tipo == TipoTransaccion.INGRESO }
            .sumOf { it.monto }
        val gastos = transaccionesFiltradas
            .filter { it.tipo == TipoTransaccion.GASTO }
            .sumOf { it.monto }
        val balance = ingresos - gastos

        tvResumen.text = "Total: $totalTransacciones transacciones | Balance: ${FormatosApp.formatearMoneda(balance)}"
    }
}

