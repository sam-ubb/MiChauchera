package com.spidersam.michauchera.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.spidersam.michauchera.model.Usuario

@Dao
interface UsuarioDao {

    @Query("SELECT * FROM usuarios LIMIT 1")
    suspend fun obtenerUsuario(): Usuario?

    @Query("SELECT * FROM usuarios LIMIT 1")
    fun obtenerUsuarioLive(): LiveData<Usuario?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarUsuario(usuario: Usuario): Long

    @Update
    suspend fun actualizarUsuario(usuario: Usuario)

    @Query("SELECT limiteGastoMensual FROM usuarios LIMIT 1")
    suspend fun obtenerLimiteGastoMensual(): Double?

    @Query("SELECT notificacionesActivas FROM usuarios LIMIT 1")
    suspend fun obtenerNotificacionesActivas(): Boolean?
}

