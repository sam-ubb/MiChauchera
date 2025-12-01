package com.spidersam.michauchera.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.spidersam.michauchera.R
import com.spidersam.michauchera.TransaccionesAdapter
import com.spidersam.michauchera.utils.FormatosApp
import com.spidersam.michauchera.viewmodel.TransaccionViewModel

class DashboardFragment : Fragment() {

    private val viewModel: TransaccionViewModel by activityViewModels()
    
    private lateinit var tvSaldoTotal: TextView
    private lateinit var tvIngresos: TextView
    private lateinit var tvGastos: TextView
    private lateinit var tvBalanceMes: TextView
    private lateinit var recyclerUltimasTransacciones: RecyclerView
    private lateinit var btnAgregarTransaccion: Button
    private lateinit var btnVerHistorial: Button

    private lateinit var adapter: TransaccionesAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        inicializarVistas(view)
        configurarRecyclerView()
        observarDatos()
        configurarListeners()
    }

    private fun inicializarVistas(view: View) {
        tvSaldoTotal = view.findViewById(R.id.tvSaldoTotal)
        tvIngresos = view.findViewById(R.id.tvIngresos)
        tvGastos = view.findViewById(R.id.tvGastos)
        tvBalanceMes = view.findViewById(R.id.tvBalanceMes)
        recyclerUltimasTransacciones = view.findViewById(R.id.recyclerUltimasTransacciones)
        btnAgregarTransaccion = view.findViewById(R.id.btnAgregarTransaccion)
        btnVerHistorial = view.findViewById(R.id.btnVerHistorial)
    }

    private fun configurarRecyclerView() {
        adapter = TransaccionesAdapter(
            onEditClick = { transaccion ->
                val action = DashboardFragmentDirections
                    .actionDashboardToTransaccionForm(transaccion.id)
                findNavController().navigate(action)
            },
            onDeleteClick = { transaccion ->
                viewModel.eliminarTransaccion(transaccion)
            }
        )
        
        recyclerUltimasTransacciones.layoutManager = LinearLayoutManager(requireContext())
        recyclerUltimasTransacciones.adapter = adapter
    }

    private fun observarDatos() {
        viewModel.balanceTotal.observe(viewLifecycleOwner) { balance ->
            tvSaldoTotal.text = FormatosApp.formatearMonedaConDecimales(balance ?: 0.0)
        }
        
        viewModel.totalIngresos.observe(viewLifecycleOwner) { ingresos ->
            tvIngresos.text = "↗ ${FormatosApp.formatearMonedaConDecimales(ingresos ?: 0.0)}"
        }
        
        viewModel.totalGastos.observe(viewLifecycleOwner) { gastos ->
            tvGastos.text = "↘ ${FormatosApp.formatearMonedaConDecimales(gastos ?: 0.0)}"
        }
        
        viewModel.estadisticasMensuales.observe(viewLifecycleOwner) { estadisticas ->
            val balance = estadisticas.ingresosMes - estadisticas.gastosMes
            tvBalanceMes.text = FormatosApp.formatearMonedaConDecimales(balance)

            val color = if (balance < 0) {
                ContextCompat.getColor(requireContext(), R.color.balance_negative_red)
            } else {
                ContextCompat.getColor(requireContext(), R.color.income_green)
            }
            tvBalanceMes.setTextColor(color)
        }
        
        viewModel.todasLasTransacciones.observe(viewLifecycleOwner) { transacciones ->
            adapter.submitList(transacciones.take(4))
        }
    }

    private fun configurarListeners() {
        btnAgregarTransaccion.setOnClickListener {
            val action = DashboardFragmentDirections.actionDashboardToTransaccionForm(0L)
            findNavController().navigate(action)
        }

        btnVerHistorial.setOnClickListener {
            findNavController().navigate(R.id.transaccionesFragment)
        }
    }
}

