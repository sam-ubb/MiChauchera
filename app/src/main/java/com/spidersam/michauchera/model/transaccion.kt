package com.spidersam.michauchera.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import java.io.Serializable
import java.util.Date

@Entity(tableName = "transactions")
@TypeConverters(Converters::class)
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val amount: Double,
    val type: TransactionType,
    val category: String,
    val date: Date,
    val description: String? = null
) : Serializable {

    init {
        require(amount > 0) { "El monto debe ser positivo" }
        require(category.isNotBlank()) { "La categoría no puede estar vacía" }
    }

    /**
     * Retorna el monto con signo según el tipo de transacción
     * Ingresos: positivo
     * Gastos: negativo
     */
    fun getSignedAmount(): Double {
        return when (type) {
            TransactionType.INCOME -> amount
            TransactionType.EXPENSE -> -amount
        }
    }
}

enum class TransactionType {
    INCOME, EXPENSE
}

/**
 * Convertidores de tipo para Room Database
 */
class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromTransactionType(value: TransactionType): String {
        return value.name
    }

    @TypeConverter
    fun toTransactionType(value: String): TransactionType {
        return TransactionType.valueOf(value)
    }
}
