package com.spidersam.michauchera.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.spidersam.michauchera.model.Presupuesto
import kotlinx.coroutines.flow.Flow

@Dao
interface PresupuestoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarPresupuesto(presupuesto: Presupuesto): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarPresupuestos(presupuestos: List<Presupuesto>)

    @Query("SELECT * FROM presupuestos WHERE activo = 1 ORDER BY anio DESC, mes DESC")
    fun obtenerPresupuestosActivos(): LiveData<List<Presupuesto>>

    @Query("SELECT * FROM presupuestos WHERE activo = 1 ORDER BY anio DESC, mes DESC")
    fun obtenerPresupuestosActivosFlow(): Flow<List<Presupuesto>>

    @Query("SELECT * FROM presupuestos WHERE id = :id")
    suspend fun obtenerPresupuestoPorId(id: Long): Presupuesto?

    @Query("SELECT * FROM presupuestos WHERE mes = :mes AND anio = :anio AND activo = 1 ORDER BY categoria ASC")
    fun obtenerPresupuestosPorMes(mes: Int, anio: Int): LiveData<List<Presupuesto>>

    @Query("SELECT * FROM presupuestos WHERE mes = :mes AND anio = :anio AND activo = 1")
    suspend fun obtenerPresupuestosPorMesSync(mes: Int, anio: Int): List<Presupuesto>

    @Query("""
        SELECT * FROM presupuestos 
        WHERE categoria = :categoria 
        AND mes = :mes 
        AND anio = :anio 
        AND activo = 1
        LIMIT 1
    """)
    suspend fun obtenerPresupuestoPorCategoria(
        categoria: String,
        mes: Int,
        anio: Int
    ): Presupuesto?

    @Query("""
        SELECT DISTINCT categoria 
        FROM presupuestos 
        WHERE mes = :mes AND anio = :anio AND activo = 1
        ORDER BY categoria ASC
    """)
    suspend fun obtenerCategoriasConPresupuesto(mes: Int, anio: Int): List<String>

    @Query("""
        SELECT COALESCE(SUM(montoLimite), 0) 
        FROM presupuestos 
        WHERE mes = :mes AND anio = :anio AND activo = 1
    """)
    suspend fun obtenerPresupuestoTotalMes(mes: Int, anio: Int): Double

    @Update
    suspend fun actualizarPresupuesto(presupuesto: Presupuesto)

    @Query("UPDATE presupuestos SET montoLimite = :nuevoLimite WHERE id = :id")
    suspend fun actualizarMontoLimite(id: Long, nuevoLimite: Double)

    @Query("UPDATE presupuestos SET activo = 0 WHERE id = :id")
    suspend fun archivarPresupuesto(id: Long)

    @Query("UPDATE presupuestos SET activo = 1 WHERE id = :id")
    suspend fun reactivarPresupuesto(id: Long)

    @Delete
    suspend fun eliminarPresupuesto(presupuesto: Presupuesto)

    @Query("DELETE FROM presupuestos WHERE id = :id")
    suspend fun eliminarPresupuestoPorId(id: Long)

    @Query("DELETE FROM presupuestos WHERE mes = :mes AND anio = :anio")
    suspend fun eliminarPresupuestosPorMes(mes: Int, anio: Int)

    @Query("DELETE FROM presupuestos WHERE activo = 0")
    suspend fun eliminarPresupuestosInactivos()

    @Query("DELETE FROM presupuestos")
    suspend fun eliminarTodosLosPresupuestos()

    @Query("""
        SELECT COUNT(*) 
        FROM presupuestos 
        WHERE mes = :mes AND anio = :anio AND activo = 1
    """)
    suspend fun contarPresupuestosActivos(mes: Int, anio: Int): Int

    @Query("""
        SELECT EXISTS(
            SELECT 1 FROM presupuestos 
            WHERE categoria = :categoria 
            AND mes = :mes 
            AND anio = :anio 
            AND activo = 1
        )
    """)
    suspend fun existePresupuestoParaCategoria(
        categoria: String,
        mes: Int,
        anio: Int
    ): Boolean
}

