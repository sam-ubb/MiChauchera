package com.spidersam.michauchera.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.spidersam.michauchera.model.Transaction
import com.spidersam.michauchera.model.TransactionType
import java.util.Date

@Dao
interface TransactionDao {

    /**
     * Obtiene todas las transacciones ordenadas por fecha descendente
     */
    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAllTransactions(): LiveData<List<Transaction>>

    /**
     * Obtiene todas las transacciones de forma síncrona
     */
    @Query("SELECT * FROM transactions ORDER BY date DESC")
    suspend fun getAllTransactionsSync(): List<Transaction>

    /**
     * Obtiene transacciones por tipo
     */
    @Query("SELECT * FROM transactions WHERE type = :type ORDER BY date DESC")
    fun getTransactionsByType(type: TransactionType): LiveData<List<Transaction>>

    /**
     * Obtiene transacciones de un mes específico
     */
    @Query("SELECT * FROM transactions WHERE date >= :startDate AND date < :endDate ORDER BY date DESC")
    fun getTransactionsByMonth(startDate: Date, endDate: Date): LiveData<List<Transaction>>

    /**
     * Obtiene transacciones de un mes específico de forma síncrona
     */
    @Query("SELECT * FROM transactions WHERE date >= :startDate AND date < :endDate ORDER BY date DESC")
    suspend fun getTransactionsByMonthSync(startDate: Date, endDate: Date): List<Transaction>

    /**
     * Obtiene una transacción por ID
     */
    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getTransactionById(id: Long): Transaction?

    /**
     * Inserta una nueva transacción
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: Transaction): Long

    /**
     * Inserta múltiples transacciones
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransactions(transactions: List<Transaction>)

    /**
     * Actualiza una transacción existente
     */
    @Update
    suspend fun updateTransaction(transaction: Transaction)

    /**
     * Elimina una transacción
     */
    @Delete
    suspend fun deleteTransaction(transaction: Transaction)

    /**
     * Elimina una transacción por ID
     */
    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteTransactionById(id: Long)

    /**
     * Elimina todas las transacciones
     */
    @Query("DELETE FROM transactions")
    suspend fun deleteAllTransactions()

    /**
     * Calcula el saldo total (suma de todos los ingresos menos todos los gastos)
     */
    @Query("""
        SELECT 
            COALESCE(SUM(CASE WHEN type = 'INCOME' THEN amount ELSE -amount END), 0)
        FROM transactions
    """)
    fun getTotalBalance(): LiveData<Double>

    /**
     * Calcula el balance de un mes específico
     */
    @Query("""
        SELECT 
            COALESCE(SUM(CASE WHEN type = 'INCOME' THEN amount ELSE -amount END), 0)
        FROM transactions
        WHERE date >= :startDate AND date < :endDate
    """)
    suspend fun getMonthlyBalance(startDate: Date, endDate: Date): Double

    /**
     * Obtiene el total de ingresos
     */
    @Query("SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE type = 'INCOME'")
    fun getTotalIncome(): LiveData<Double>

    /**
     * Obtiene el total de gastos
     */
    @Query("SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE type = 'EXPENSE'")
    fun getTotalExpenses(): LiveData<Double>

    /**
     * Obtiene ingresos del mes
     */
    @Query("""
        SELECT COALESCE(SUM(amount), 0) 
        FROM transactions 
        WHERE type = 'INCOME' AND date >= :startDate AND date < :endDate
    """)
    suspend fun getMonthlyIncome(startDate: Date, endDate: Date): Double

    /**
     * Obtiene gastos del mes
     */
    @Query("""
        SELECT COALESCE(SUM(amount), 0) 
        FROM transactions 
        WHERE type = 'EXPENSE' AND date >= :startDate AND date < :endDate
    """)
    suspend fun getMonthlyExpenses(startDate: Date, endDate: Date): Double
}

