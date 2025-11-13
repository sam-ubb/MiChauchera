package com.spidersam.michauchera

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.spidersam.michauchera.model.Transaction
import com.spidersam.michauchera.model.TransactionType
import java.text.SimpleDateFormat
import java.util.*

class AddTransactionActivity : AppCompatActivity() {

    private lateinit var etAmount: EditText
    private lateinit var rgType: RadioGroup
    private lateinit var spinnerCategory: Spinner
    private lateinit var etDate: EditText
    private lateinit var etDescription: EditText
    private lateinit var btnSave: Button

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private var selectedDate: Calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_transaction)

        etAmount = findViewById(R.id.etAmount)
        rgType = findViewById(R.id.rgType)
        spinnerCategory = findViewById(R.id.spinnerCategory)
        etDate = findViewById(R.id.etDate)
        etDescription = findViewById(R.id.etDescription)
        btnSave = findViewById(R.id.btnSave)

        //categorías de ejemplo
        val categories = listOf("Transporte", "Comida", "Salario", "Entretenimiento", "Otros")
        spinnerCategory.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categories)

        etDate.setText(dateFormat.format(selectedDate.time))
        etDate.setOnClickListener { showDatePicker() }

        btnSave.setOnClickListener { onSave() }
    }

    private fun showDatePicker() {
        val y = selectedDate.get(Calendar.YEAR)
        val m = selectedDate.get(Calendar.MONTH)
        val d = selectedDate.get(Calendar.DAY_OF_MONTH)
        DatePickerDialog(this, { _, year, month, dayOfMonth ->
            selectedDate.set(year, month, dayOfMonth)
            etDate.setText(dateFormat.format(selectedDate.time))
        }, y, m, d).show()
    }

    private fun onSave() {
        val amountText = etAmount.text.toString().trim()
        if (amountText.isEmpty()) {
            etAmount.error = "Ingrese monto"
            return
        }
        val amount = amountText.replace(",", ".").toDoubleOrNull()
        if (amount == null) {
            etAmount.error = "Monto inválido"
            return
        }

        val type = when (rgType.checkedRadioButtonId) {
            R.id.rbIncome -> TransactionType.INCOME
            else -> TransactionType.EXPENSE
        }

        val category = spinnerCategory.selectedItem?.toString() ?: "Otros"
        val description = etDescription.text.toString().trim()

        val transaction = Transaction(
            id = System.currentTimeMillis(),
            amount = amount,
            type = type,
            category = category,
            date = selectedDate.time,
            description = if (description.isEmpty()) null else description
        )

        // Devuelve el objeto al llamador (puede guardarse en DB en la Activity principal)
        val result = Intent()
        result.putExtra("transaction", transaction)
        setResult(RESULT_OK, result)
        finish()
    }
}