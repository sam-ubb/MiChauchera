package com.spidersam.michauchera.data

import androidx.lifecycle.LiveData
import com.spidersam.michauchera.model.Transaction
import com.spidersam.michauchera.model.TransactionType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Date

class TransactionRepository(private val transactionDao: TransactionDao) {

    val allTransactions: LiveData<List<Transaction>> = transactionDao.getAllTransactions()
    val totalBalance: LiveData<Double> = transactionDao.getTotalBalance()
    val totalIncome: LiveData<Double> = transactionDao.getTotalIncome()
    val totalExpenses: LiveData<Double> = transactionDao.getTotalExpenses()

    suspend fun insertTransaction(transaction: Transaction): Result<Long> {
        return withContext(Dispatchers.IO) {
            try {
                validateTransaction(transaction)
                val id = transactionDao.insertTransaction(transaction)
                Result.success(id)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun updateTransaction(transaction: Transaction): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                validateTransaction(transaction)
                transactionDao.updateTransaction(transaction)
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun deleteTransaction(transaction: Transaction): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                transactionDao.deleteTransaction(transaction)
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun deleteTransactionById(id: Long): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                transactionDao.deleteTransactionById(id)
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun getTransactionById(id: Long): Transaction? {
        return withContext(Dispatchers.IO) {
            transactionDao.getTransactionById(id)
        }
    }

    fun getTransactionsByType(type: TransactionType): LiveData<List<Transaction>> {
        return transactionDao.getTransactionsByType(type)
    }

    suspend fun getCurrentMonthBalance(): Double {
        return withContext(Dispatchers.IO) {
            val (startDate, endDate) = getCurrentMonthRange()
            transactionDao.getMonthlyBalance(startDate, endDate)
        }
    }

    suspend fun getCurrentMonthIncome(): Double {
        return withContext(Dispatchers.IO) {
            val (startDate, endDate) = getCurrentMonthRange()
            transactionDao.getMonthlyIncome(startDate, endDate)
        }
    }

    suspend fun getCurrentMonthExpenses(): Double {
        return withContext(Dispatchers.IO) {
            val (startDate, endDate) = getCurrentMonthRange()
            transactionDao.getMonthlyExpenses(startDate, endDate)
        }
    }

    fun getCurrentMonthTransactions(): LiveData<List<Transaction>> {
        val (startDate, endDate) = getCurrentMonthRange()
        return transactionDao.getTransactionsByMonth(startDate, endDate)
    }

    suspend fun getCurrentMonthStats(): MonthlyStats {
        return withContext(Dispatchers.IO) {
            val (startDate, endDate) = getCurrentMonthRange()
            val income = transactionDao.getMonthlyIncome(startDate, endDate)
            val expenses = transactionDao.getMonthlyExpenses(startDate, endDate)
            val balance = income - expenses

            MonthlyStats(
                income = income,
                expenses = expenses,
                balance = balance,
                month = Calendar.getInstance().get(Calendar.MONTH),
                year = Calendar.getInstance().get(Calendar.YEAR)
            )
        }
    }

    private fun validateTransaction(transaction: Transaction) {
        require(transaction.amount > 0) { "El monto debe ser mayor a 0" }
        require(transaction.category.isNotBlank()) { "Debe seleccionar una categoría" }
    }

    private fun getCurrentMonthRange(): Pair<Date, Date> {
        val calendar = Calendar.getInstance()

        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startDate = calendar.time

        calendar.add(Calendar.MONTH, 1)
        val endDate = calendar.time

        return Pair(startDate, endDate)
    }
}

data class MonthlyStats(
    val income: Double,
    val expenses: Double,
    val balance: Double,
    val month: Int,
    val year: Int
)


