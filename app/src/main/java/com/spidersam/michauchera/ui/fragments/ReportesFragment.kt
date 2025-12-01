package com.spidersam.michauchera.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.spidersam.michauchera.R
import com.spidersam.michauchera.utils.FormatosApp
import com.spidersam.michauchera.viewmodel.TransaccionViewModel

class ReportesFragment : Fragment() {

    private val viewModel: TransaccionViewModel by activityViewModels()

    private lateinit var tvTotalIngresos: TextView
    private lateinit var tvTotalGastos: TextView
    private lateinit var tvBalanceNeto: TextView
    private lateinit var tvPromedioGasto: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_reportes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        inicializarVistas(view)
        observarDatos()
    }

    private fun inicializarVistas(view: View) {
        tvTotalIngresos = view.findViewById(R.id.tvTotalIngresos)
        tvTotalGastos = view.findViewById(R.id.tvTotalGastos)
        tvBalanceNeto = view.findViewById(R.id.tvBalanceNeto)
        tvPromedioGasto = view.findViewById(R.id.tvPromedioGasto)
    }

    private fun observarDatos() {
        viewModel.estadisticasMensuales.observe(viewLifecycleOwner) { estadisticas ->
            tvTotalIngresos.text = FormatosApp.formatearMonedaConDecimales(estadisticas.ingresosMes)
            tvTotalGastos.text = FormatosApp.formatearMonedaConDecimales(estadisticas.gastosMes)

            val balance = estadisticas.ingresosMes - estadisticas.gastosMes
            tvBalanceNeto.text = FormatosApp.formatearMonedaConDecimales(balance)

            val promedioDiario = if (estadisticas.gastosMes > 0) {
                estadisticas.gastosMes / 30
            } else {
                0.0
            }
            tvPromedioGasto.text = FormatosApp.formatearMonedaConDecimales(promedioDiario)
        }
    }
}

