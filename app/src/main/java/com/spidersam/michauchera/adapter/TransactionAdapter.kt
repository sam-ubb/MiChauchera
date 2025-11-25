package com.spidersam.michauchera.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.spidersam.michauchera.R
import com.spidersam.michauchera.databinding.ItemTransactionBinding
import com.spidersam.michauchera.model.Transaction
import com.spidersam.michauchera.model.TransactionType
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

class TransactionAdapter(
    private val onItemClick: (Transaction) -> Unit,
    private val onItemLongClick: (Transaction) -> Boolean
) : ListAdapter<Transaction, TransactionAdapter.TransactionViewHolder>(TransactionDiffCallback()) {

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale("es", "MX"))

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val binding = ItemTransactionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TransactionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TransactionViewHolder(
        private val binding: ItemTransactionBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(transaction: Transaction) {
            binding.apply {
                // Categoría y descripción
                tvCategory.text = transaction.category
                tvDescription.text = transaction.description ?: "Sin descripción"

                // Fecha
                tvDate.text = dateFormat.format(transaction.date)

                // Monto con formato y color según tipo
                val amountColor = when (transaction.type) {
                    TransactionType.INCOME -> ContextCompat.getColor(root.context, R.color.income_green)
                    TransactionType.EXPENSE -> ContextCompat.getColor(root.context, R.color.expense_red)
                }

                val amountText = if (transaction.type == TransactionType.INCOME) {
                    "+${currencyFormat.format(transaction.amount)}"
                } else {
                    "-${currencyFormat.format(transaction.amount)}"
                }

                tvAmount.text = amountText
                tvAmount.setTextColor(amountColor)

                // Click listeners
                root.setOnClickListener { onItemClick(transaction) }
                root.setOnLongClickListener { onItemLongClick(transaction) }
            }
        }
    }

    class TransactionDiffCallback : DiffUtil.ItemCallback<Transaction>() {
        override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem == newItem
        }
    }
}

