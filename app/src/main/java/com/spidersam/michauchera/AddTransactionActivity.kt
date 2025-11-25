package com.spidersam.michauchera

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.spidersam.michauchera.databinding.ActivityAddTransactionBinding
import com.spidersam.michauchera.model.Transaction
import com.spidersam.michauchera.model.TransactionType
import java.text.SimpleDateFormat
import java.util.*

class AddTransactionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddTransactionBinding
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private var selectedDate: Calendar = Calendar.getInstance()

    // Categorías predefinidas
    private val categories = listOf(
        "Transporte",
        "Comida",
        "Salario",
        "Entretenimiento",
        "Servicios",
        "Salud",
        "Educación",
        "Compras",
        "Renta/Hipoteca",
        "Inversiones",
        "Otros"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Nueva Transacción"

        setupCategorySpinner()
        setupDatePicker()
        setupSaveButton()

        // Configurar tipo de transacción por defecto (Gasto)
        binding.rbExpense.isChecked = true
    }

    private fun setupCategorySpinner() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, categories)
        (binding.tilCategory.editText as? AutoCompleteTextView)?.apply {
            setAdapter(adapter)
            setText(categories[0], false)
        }
    }

    private fun setupDatePicker() {
        binding.etDate.setText(dateFormat.format(selectedDate.time))
        binding.etDate.setOnClickListener { showDatePicker() }
    }

    private fun showDatePicker() {
        val year = selectedDate.get(Calendar.YEAR)
        val month = selectedDate.get(Calendar.MONTH)
        val day = selectedDate.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            selectedDate.set(selectedYear, selectedMonth, selectedDay)
            binding.etDate.setText(dateFormat.format(selectedDate.time))
        }, year, month, day).apply {
            // No permitir fechas futuras
            datePicker.maxDate = System.currentTimeMillis()
            show()
        }
    }

    private fun setupSaveButton() {
        binding.btnSave.setOnClickListener {
            if (validateAndSave()) {
                finish()
            }
        }
    }

    private fun validateAndSave(): Boolean {
        // Limpiar errores previos
        binding.tilAmount.error = null
        binding.tilCategory.error = null

        // Validar monto
        val amountText = binding.etAmount.text.toString().trim()
        if (amountText.isEmpty()) {
            binding.tilAmount.error = "Ingrese un monto"
            binding.etAmount.requestFocus()
            return false
        }

        val amount = try {
            amountText.replace(",", ".").toDouble()
        } catch (e: NumberFormatException) {
            binding.tilAmount.error = "Monto inválido"
            binding.etAmount.requestFocus()
            return false
        }

        // Validar que el monto sea positivo
        if (amount <= 0) {
            binding.tilAmount.error = "El monto debe ser mayor a 0"
            binding.etAmount.requestFocus()
            return false
        }

        // Validar que el monto no sea excesivamente grande
        if (amount > 999999999.99) {
            binding.tilAmount.error = "El monto es demasiado grande"
            binding.etAmount.requestFocus()
            return false
        }

        // Validar tipo de transacción
        val type = when {
            binding.rbIncome.isChecked -> TransactionType.INCOME
            binding.rbExpense.isChecked -> TransactionType.EXPENSE
            else -> {
                showError("Seleccione un tipo de transacción")
                return false
            }
        }

        // Validar categoría
        val category = (binding.tilCategory.editText as? AutoCompleteTextView)?.text.toString().trim()
        if (category.isEmpty()) {
            binding.tilCategory.error = "Seleccione una categoría"
            return false
        }

        // Descripción (opcional)
        val description = binding.etDescription.text.toString().trim()

        // Crear transacción
        try {
            val transaction = Transaction(
                id = 0L, // Room auto-generará el ID
                amount = amount,
                type = type,
                category = category,
                date = selectedDate.time,
                description = if (description.isEmpty()) null else description
            )

            // Enviar resultado de vuelta
            intent.putExtra("transaction", transaction)
            setResult(RESULT_OK, intent)

            return true
        } catch (e: IllegalArgumentException) {
            showError("Error: ${e.message}")
            return false
        } catch (e: Exception) {
            showError("Error inesperado: ${e.message}")
            return false
        }
    }

    private fun showError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Guardar el estado para rotaciones de pantalla
        outState.putLong("selectedDate", selectedDate.timeInMillis)
        outState.putString("amount", binding.etAmount.text.toString())
        outState.putBoolean("isIncome", binding.rbIncome.isChecked)
        outState.putString("category", (binding.tilCategory.editText as? AutoCompleteTextView)?.text.toString())
        outState.putString("description", binding.etDescription.text.toString())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        // Restaurar el estado después de rotaciones
        savedInstanceState.getLong("selectedDate", -1).takeIf { it != -1L }?.let {
            selectedDate.timeInMillis = it
            binding.etDate.setText(dateFormat.format(selectedDate.time))
        }
        savedInstanceState.getString("amount")?.let {
            binding.etAmount.setText(it)
        }
        if (savedInstanceState.getBoolean("isIncome", false)) {
            binding.rbIncome.isChecked = true
        }
        savedInstanceState.getString("category")?.let {
            (binding.tilCategory.editText as? AutoCompleteTextView)?.setText(it, false)
        }
        savedInstanceState.getString("description")?.let {
            binding.etDescription.setText(it)
        }
    }
}