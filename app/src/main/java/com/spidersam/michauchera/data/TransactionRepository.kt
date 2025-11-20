package com.spidersam.michauchera.data
)
    val year: Int
    val month: Int,
    val balance: Double,
    val expenses: Double,
    val income: Double,
data class MonthlyStats(
 */
 * Data class para estadísticas mensuales
/**

}
    }
        return Pair(startDate, endDate)

        val endDate = calendar.time
        calendar.add(Calendar.MONTH, 1)
        // Primer día del próximo mes a las 00:00:00

        val startDate = calendar.time
        calendar.set(Calendar.MILLISECOND, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        // Primer día del mes a las 00:00:00

        val calendar = Calendar.getInstance()
    private fun getCurrentMonthRange(): Pair<Date, Date> {
     */
     * Obtiene el rango de fechas del mes actual
    /**

    }
        require(transaction.category.isNotBlank()) { "Debe seleccionar una categoría" }
        require(transaction.amount > 0) { "El monto debe ser mayor a 0" }
    private fun validateTransaction(transaction: Transaction) {
     */
     * Valida una transacción antes de insertarla/actualizarla
    /**

    }
        }
            )
                year = Calendar.getInstance().get(Calendar.YEAR)
                month = Calendar.getInstance().get(Calendar.MONTH),
                balance = balance,
                expenses = expenses,
                income = income,
            MonthlyStats(

            val balance = income - expenses
            val expenses = transactionDao.getMonthlyExpenses(startDate, endDate)
            val income = transactionDao.getMonthlyIncome(startDate, endDate)
            val (startDate, endDate) = getCurrentMonthRange()
        return withContext(Dispatchers.IO) {
    suspend fun getCurrentMonthStats(): MonthlyStats {
     */
     * Obtiene estadísticas financieras del mes actual
    /**

    }
        return transactionDao.getTransactionsByMonth(startDate, endDate)
        val (startDate, endDate) = getCurrentMonthRange()
    fun getCurrentMonthTransactions(): LiveData<List<Transaction>> {
     */
     * Obtiene transacciones del mes actual
    /**

    }
        }
            transactionDao.getMonthlyExpenses(startDate, endDate)
            val (startDate, endDate) = getCurrentMonthRange()
        return withContext(Dispatchers.IO) {
    suspend fun getCurrentMonthExpenses(): Double {
     */
     * Obtiene los gastos del mes actual
    /**

    }
        }
            transactionDao.getMonthlyIncome(startDate, endDate)
            val (startDate, endDate) = getCurrentMonthRange()
        return withContext(Dispatchers.IO) {
    suspend fun getCurrentMonthIncome(): Double {
     */
     * Obtiene los ingresos del mes actual
    /**

    }
        }
            transactionDao.getMonthlyBalance(startDate, endDate)
            val (startDate, endDate) = getCurrentMonthRange()
        return withContext(Dispatchers.IO) {
    suspend fun getCurrentMonthBalance(): Double {
     */
     * Obtiene el balance del mes actual
    /**

    }
        return transactionDao.getTransactionsByType(type)
    fun getTransactionsByType(type: TransactionType): LiveData<List<Transaction>> {
     */
     * Obtiene transacciones por tipo
    /**

    }
        }
            transactionDao.getTransactionById(id)
        return withContext(Dispatchers.IO) {
    suspend fun getTransactionById(id: Long): Transaction? {
     */
     * Obtiene una transacción por ID
    /**

    }
        }
            }
                Result.failure(e)
            } catch (e: Exception) {
                Result.success(Unit)
                transactionDao.deleteTransactionById(id)
            try {
        return withContext(Dispatchers.IO) {
    suspend fun deleteTransactionById(id: Long): Result<Unit> {
     */
     * Elimina una transacción por ID
    /**

    }
        }
            }
                Result.failure(e)
            } catch (e: Exception) {
                Result.success(Unit)
                transactionDao.deleteTransaction(transaction)
            try {
        return withContext(Dispatchers.IO) {
    suspend fun deleteTransaction(transaction: Transaction): Result<Unit> {
     */
     * Elimina una transacción
    /**

    }
        }
            }
                Result.failure(e)
            } catch (e: Exception) {
                Result.success(Unit)
                transactionDao.updateTransaction(transaction)
                validateTransaction(transaction)
            try {
        return withContext(Dispatchers.IO) {
    suspend fun updateTransaction(transaction: Transaction): Result<Unit> {
     */
     * Actualiza una transacción existente
    /**

    }
        }
            }
                Result.failure(e)
            } catch (e: Exception) {
                Result.success(id)
                val id = transactionDao.insertTransaction(transaction)
                validateTransaction(transaction)
                // Validación adicional
            try {
        return withContext(Dispatchers.IO) {
    suspend fun insertTransaction(transaction: Transaction): Result<Long> {
     */
     * Inserta una nueva transacción con validación
    /**

    val totalExpenses: LiveData<Double> = transactionDao.getTotalExpenses()
    val totalIncome: LiveData<Double> = transactionDao.getTotalIncome()
    val totalBalance: LiveData<Double> = transactionDao.getTotalBalance()
    val allTransactions: LiveData<List<Transaction>> = transactionDao.getAllTransactions()
    // LiveData observables

class TransactionRepository(private val transactionDao: TransactionDao) {
 */
 * Provee una API limpia para el ViewModel.
 * Repository que maneja las operaciones de datos para transacciones.
/**

import java.util.Date
import java.util.Calendar
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import com.spidersam.michauchera.model.TransactionType
import com.spidersam.michauchera.model.Transaction
import androidx.lifecycle.LiveData


