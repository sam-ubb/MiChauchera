package com.spidersam.michauchera.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.spidersam.michauchera.model.Convertidores
import com.spidersam.michauchera.model.Presupuesto
import com.spidersam.michauchera.model.Transaccion
import com.spidersam.michauchera.model.Usuario

@Database(
    entities = [
        Transaccion::class,
        Usuario::class,
        Presupuesto::class
    ],
    version = 3,
    exportSchema = false
)
@TypeConverters(Convertidores::class)
abstract class BaseDeDatos : RoomDatabase() {

    abstract fun transaccionDao(): TransaccionDao
    abstract fun usuarioDao(): UsuarioDao
    abstract fun presupuestoDao(): PresupuestoDao

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

