package com.spidersam.michauchera.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.spidersam.michauchera.model.Convertidores
import com.spidersam.michauchera.model.Transaccion

@Database(
    entities = [Transaccion::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Convertidores::class)
abstract class BaseDeDatos : RoomDatabase() {

    abstract fun transaccionDao(): TransaccionDao

    companion object {
        @Volatile
        private var INSTANCIA: BaseDeDatos? = null

        fun obtenerBaseDeDatos(contexto: Context): BaseDeDatos {
            return INSTANCIA ?: synchronized(this) {
                val instancia = Room.databaseBuilder(
                    contexto.applicationContext,
                    BaseDeDatos::class.java,
                    "michauchera_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCIA = instancia
                instancia
            }
        }
    }
}

