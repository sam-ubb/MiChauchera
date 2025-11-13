package com.spidersam.michauchera.model

import java.io.Serializable
import java.util.Date

data class Transaction(
    val id: Long = 0L,
    val amount: Double,
    val type: TransactionType,
    val category: String,
    val date: Date,
    val description: String? = null
) : Serializable

enum class TransactionType {
    INCOME, EXPENSE
}