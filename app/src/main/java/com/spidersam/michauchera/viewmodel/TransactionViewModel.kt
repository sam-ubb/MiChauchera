package com.spidersam.michauchera.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.spidersam.michauchera.data.AppDatabase
import com.spidersam.michauchera.data.MonthlyStats
import com.spidersam.michauchera.data.TransactionRepository
import com.spidersam.michauchera.model.Transaction
import com.spidersam.michauchera.model.TransactionType
import kotlinx.coroutines.launch

/**
 * ViewModel que gestiona el estado de la UI y las operaciones de transacciones.
 * Sobrevive a cambios de configuración como rotaciones de pantalla.
 */
class TransactionViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TransactionRepository

    // LiveData observables
    val allTransactions: LiveData<List<Transaction>>
    val totalBalance: LiveData<Double>
    val totalIncome: LiveData<Double>
    val totalExpenses: LiveData<Double>

    // Estado de la UI
    private val _monthlyStats = MutableLiveData<MonthlyStats>()
    val monthlyStats: LiveData<MonthlyStats> = _monthlyStats

    private val _operationStatus = MutableLiveData<OperationStatus>()
    val operationStatus: LiveData<OperationStatus> = _operationStatus

    init {
        val database = AppDatabase.getDatabase(application)
        val transactionDao = database.transactionDao()
        repository = TransactionRepository(transactionDao)

        allTransactions = repository.allTransactions
        totalBalance = repository.totalBalance
        totalIncome = repository.totalIncome
        totalExpenses = repository.totalExpenses

        // Cargar estadísticas del mes actual
        loadMonthlyStats()
    }

    /**
     * Inserta una nueva transacción
     */
    fun insertTransaction(transaction: Transaction) {
        viewModelScope.launch {
            val result = repository.insertTransaction(transaction)
            result.fold(
                onSuccess = {
                    _operationStatus.value = OperationStatus.Success("Transacción agregada correctamente")
                    loadMonthlyStats() // Actualizar estadísticas
                },
                onFailure = { exception ->
                    _operationStatus.value = OperationStatus.Error(
                        exception.message ?: "Error al agregar transacción"
                    )
                }
            )
        }
    }

    /**
     * Actualiza una transacción existente
     */
    fun updateTransaction(transaction: Transaction) {
        viewModelScope.launch {
            val result = repository.updateTransaction(transaction)
            result.fold(
                onSuccess = {
                    _operationStatus.value = OperationStatus.Success("Transacción actualizada correctamente")
                    loadMonthlyStats()
                },
                onFailure = { exception ->
                    _operationStatus.value = OperationStatus.Error(
                        exception.message ?: "Error al actualizar transacción"
                    )
                }
            )
        }
    }

    /**
     * Elimina una transacción
     */
    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            val result = repository.deleteTransaction(transaction)
            result.fold(
                onSuccess = {
                    _operationStatus.value = OperationStatus.Success("Transacción eliminada correctamente")
                    loadMonthlyStats()
                },
                onFailure = { exception ->
                    _operationStatus.value = OperationStatus.Error(
                        exception.message ?: "Error al eliminar transacción"
                    )
                }
            )
        }
    }

    /**
     * Elimina una transacción por ID
     */
    fun deleteTransactionById(id: Long) {
        viewModelScope.launch {
            val result = repository.deleteTransactionById(id)
            result.fold(
                onSuccess = {
                    _operationStatus.value = OperationStatus.Success("Transacción eliminada correctamente")
                    loadMonthlyStats()
                },
                onFailure = { exception ->
                    _operationStatus.value = OperationStatus.Error(
                        exception.message ?: "Error al eliminar transacción"
                    )
                }
            )
        }
    }

    /**
     * Obtiene transacciones por tipo
     */
    fun getTransactionsByType(type: TransactionType): LiveData<List<Transaction>> {
        return repository.getTransactionsByType(type)
    }

    /**
     * Obtiene transacciones del mes actual
     */
    fun getCurrentMonthTransactions(): LiveData<List<Transaction>> {
        return repository.getCurrentMonthTransactions()
    }

    /**
     * Carga las estadísticas del mes actual
     */
    private fun loadMonthlyStats() {
        viewModelScope.launch {
            try {
                val stats = repository.getCurrentMonthStats()
                _monthlyStats.value = stats
            } catch (e: Exception) {
                _operationStatus.value = OperationStatus.Error(
                    "Error al cargar estadísticas: ${e.message}"
                )
            }
        }
    }

    /**
     * Recarga las estadísticas manualmente
     */
    fun refreshMonthlyStats() {
        loadMonthlyStats()
    }

    /**
     * Limpia el estado de operación
     */
    fun clearOperationStatus() {
        _operationStatus.value = OperationStatus.Idle
    }
}

/**
 * Estados de operación para retroalimentación al usuario
 */
sealed class OperationStatus {
    object Idle : OperationStatus()
    data class Success(val message: String) : OperationStatus()
    data class Error(val message: String) : OperationStatus()
}

