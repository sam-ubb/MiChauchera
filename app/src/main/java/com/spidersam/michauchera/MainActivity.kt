package com.spidersam.michauchera

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.spidersam.michauchera.adapter.TransactionAdapter
import com.spidersam.michauchera.databinding.ActivityMainBinding
import com.spidersam.michauchera.model.Transaction
import com.spidersam.michauchera.viewmodel.OperationStatus
import com.spidersam.michauchera.viewmodel.TransactionViewModel
import java.text.NumberFormat
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: TransactionViewModel
    private lateinit var adapter: TransactionAdapter
    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale("es", "MX"))

    // Activity Result Launcher para AddTransactionActivity
    private val addTransactionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            result.data?.getSerializableExtra("transaction")?.let { transaction ->
                viewModel.insertTransaction(transaction as Transaction)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar Toolbar
        setSupportActionBar(binding.toolbar)

        // Inicializar ViewModel
        viewModel = ViewModelProvider(this)[TransactionViewModel::class.java]

        // Configurar RecyclerView
        setupRecyclerView()

        // Configurar observers
        setupObservers()

        // Configurar FAB
        binding.fabAddTransaction.setOnClickListener {
            val intent = Intent(this, AddTransactionActivity::class.java)
            addTransactionLauncher.launch(intent)
        }
    }

    private fun setupRecyclerView() {
        adapter = TransactionAdapter(
            onItemClick = { transaction ->
                showTransactionDetails(transaction)
            },
            onItemLongClick = { transaction ->
                showDeleteConfirmation(transaction)
                true
            }
        )

        binding.rvTransactions.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
            setHasFixedSize(true)
        }
    }

    private fun setupObservers() {
        // Observar lista de transacciones
        viewModel.allTransactions.observe(this) { transactions ->
            adapter.submitList(transactions)

            // Mostrar/ocultar mensaje de estado vacío
            if (transactions.isEmpty()) {
                binding.tvEmptyState.visibility = View.VISIBLE
                binding.rvTransactions.visibility = View.GONE
            } else {
                binding.tvEmptyState.visibility = View.GONE
                binding.rvTransactions.visibility = View.VISIBLE
            }
        }

        // Observar saldo total
        viewModel.totalBalance.observe(this) { balance ->
            binding.tvSaldoTotal.text = currencyFormat.format(balance)
        }

        // Observar ingresos totales
        viewModel.totalIncome.observe(this) { income ->
            binding.tvIngresos.text = currencyFormat.format(income)
        }

        // Observar gastos totales
        viewModel.totalExpenses.observe(this) { expenses ->
            binding.tvGastos.text = currencyFormat.format(expenses)
        }

        // Observar estadísticas mensuales
        viewModel.monthlyStats.observe(this) { stats ->
            val balance = stats.balance
            binding.tvBalanceMes.text = if (balance >= 0) {
                "+${currencyFormat.format(balance)}"
            } else {
                currencyFormat.format(balance)
            }

            // Cambiar color según sea positivo o negativo
            val colorRes = if (balance >= 0) R.color.income_green else R.color.expense_red
            binding.tvBalanceMes.setTextColor(getColor(colorRes))
        }

        // Observar estado de operaciones
        viewModel.operationStatus.observe(this) { status ->
            when (status) {
                is OperationStatus.Success -> {
                    showMessage(status.message)
                    viewModel.clearOperationStatus()
                }
                is OperationStatus.Error -> {
                    showError(status.message)
                    viewModel.clearOperationStatus()
                }
                is OperationStatus.Idle -> {
                    // No hacer nada
                }
            }
        }
    }

    private fun showTransactionDetails(transaction: Transaction) {
        val message = buildString {
            append("Categoría: ${transaction.category}\n")
            append("Monto: ${currencyFormat.format(transaction.amount)}\n")
            append("Tipo: ${if (transaction.type.name == "INCOME") "Ingreso" else "Gasto"}\n")
            transaction.description?.let {
                append("Descripción: $it\n")
            }
        }

        AlertDialog.Builder(this)
            .setTitle("Detalles de Transacción")
            .setMessage(message)
            .setPositiveButton("Cerrar", null)
            .setNegativeButton("Eliminar") { _, _ ->
                showDeleteConfirmation(transaction)
            }
            .show()
    }

    private fun showDeleteConfirmation(transaction: Transaction) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar Transacción")
            .setMessage("¿Estás seguro de que deseas eliminar esta transacción?")
            .setPositiveButton("Eliminar") { _, _ ->
                viewModel.deleteTransaction(transaction)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun showMessage(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun showError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
            .setBackgroundTint(getColor(R.color.expense_red))
            .show()
    }
}