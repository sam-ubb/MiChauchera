package com.spidersam.michauchera.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import java.util.Date

enum class TipoTransaccion {
    INGRESO,
    GASTO
}

class Convertidores {
    @TypeConverter
    fun deTipoTransaccion(valor: TipoTransaccion): String {
        return valor.name
    }

    @TypeConverter
    fun aTipoTransaccion(valor: String): TipoTransaccion {
        return TipoTransaccion.valueOf(valor)
    }

    @TypeConverter
    fun deMarcaDeTiempo(valor: Long?): Date? {
        return valor?.let { Date(it) }
    }

    @TypeConverter
    fun fechaAMarcaDeTiempo(fecha: Date?): Long? {
        return fecha?.time
    }
}

@Entity(tableName = "transacciones")
data class Transaccion(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val tipo: TipoTransaccion,
    val monto: Double,
    val categoria: String,
    val descripcion: String = "",
    val fecha: Date = Date()
)
