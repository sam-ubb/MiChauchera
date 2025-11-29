package com.spidersam.michauchera.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.spidersam.michauchera.model.Transaccion
import com.spidersam.michauchera.model.TipoTransaccion
import java.util.Date

@Dao
interface TransaccionDao {

    @Query("SELECT * FROM transacciones ORDER BY fecha DESC")
    fun obtenerTodasLasTransacciones(): LiveData<List<Transaccion>>

    @Query("SELECT * FROM transacciones ORDER BY fecha DESC")
    suspend fun obtenerTodasLasTransaccionesSync(): List<Transaccion>

    @Query("SELECT * FROM transacciones WHERE tipo = :tipo ORDER BY fecha DESC")
    fun obtenerTransaccionesPorTipo(tipo: TipoTransaccion): LiveData<List<Transaccion>>

    @Query("SELECT * FROM transacciones WHERE fecha >= :fechaInicio AND fecha < :fechaFin ORDER BY fecha DESC")
    fun obtenerTransaccionesPorMes(fechaInicio: Date, fechaFin: Date): LiveData<List<Transaccion>>

    @Query("SELECT * FROM transacciones WHERE fecha >= :fechaInicio AND fecha < :fechaFin ORDER BY fecha DESC")
    suspend fun obtenerTransaccionesPorMesSync(fechaInicio: Date, fechaFin: Date): List<Transaccion>

    @Query("SELECT * FROM transacciones WHERE id = :id")
    suspend fun obtenerTransaccionPorId(id: Long): Transaccion?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTransaccion(transaccion: Transaccion): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTransacciones(transacciones: List<Transaccion>)

    @Update
    suspend fun actualizarTransaccion(transaccion: Transaccion)

    @Delete
    suspend fun eliminarTransaccion(transaccion: Transaccion)

    @Query("DELETE FROM transacciones WHERE id = :id")
    suspend fun eliminarTransaccionPorId(id: Long)

    @Query("DELETE FROM transacciones")
    suspend fun eliminarTodasLasTransacciones()

    @Query("""
        SELECT 
            COALESCE(SUM(CASE WHEN tipo = 'INGRESO' THEN monto ELSE -monto END), 0)
        FROM transacciones
    """)
    fun obtenerBalanceTotal(): LiveData<Double>

    @Query("""
        SELECT 
            COALESCE(SUM(CASE WHEN tipo = 'INGRESO' THEN monto ELSE -monto END), 0)
        FROM transacciones
        WHERE fecha >= :fechaInicio AND fecha < :fechaFin
    """)
    suspend fun obtenerBalanceMensual(fechaInicio: Date, fechaFin: Date): Double

    @Query("SELECT COALESCE(SUM(monto), 0) FROM transacciones WHERE tipo = 'INGRESO'")
    fun obtenerTotalIngresos(): LiveData<Double>

    @Query("SELECT COALESCE(SUM(monto), 0) FROM transacciones WHERE tipo = 'GASTO'")
    fun obtenerTotalGastos(): LiveData<Double>

    @Query("""
        SELECT COALESCE(SUM(monto), 0) 
        FROM transacciones 
        WHERE tipo = 'INGRESO' AND fecha >= :fechaInicio AND fecha < :fechaFin
    """)
    suspend fun obtenerIngresosMensuales(fechaInicio: Date, fechaFin: Date): Double

    @Query("""
        SELECT COALESCE(SUM(monto), 0) 
        FROM transacciones 
        WHERE tipo = 'GASTO' AND fecha >= :fechaInicio AND fecha < :fechaFin
    """)
    suspend fun obtenerGastosMensuales(fechaInicio: Date, fechaFin: Date): Double
}

