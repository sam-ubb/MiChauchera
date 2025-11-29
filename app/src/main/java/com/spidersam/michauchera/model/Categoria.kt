package com.spidersam.michauchera.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categorias")
data class Categoria(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nombre: String,
    val tipo: TipoTransaccion,
    val icono: String = "",
    val color: String = "#000000"
)

object CategoriasPredefenidas {

    val categoriasIngreso = listOf(
        Categoria(nombre = "Salario", tipo = TipoTransaccion.INGRESO, icono = "💼"),
        Categoria(nombre = "Freelance", tipo = TipoTransaccion.INGRESO, icono = "💻"),
        Categoria(nombre = "Inversiones", tipo = TipoTransaccion.INGRESO, icono = "📈"),
        Categoria(nombre = "Regalo", tipo = TipoTransaccion.INGRESO, icono = "🎁"),
        Categoria(nombre = "Otros Ingresos", tipo = TipoTransaccion.INGRESO, icono = "💰")
    )

    val categoriasGasto = listOf(
        Categoria(nombre = "Alimentación", tipo = TipoTransaccion.GASTO, icono = "🍔"),
        Categoria(nombre = "Transporte", tipo = TipoTransaccion.GASTO, icono = "🚗"),
        Categoria(nombre = "Vivienda", tipo = TipoTransaccion.GASTO, icono = "🏠"),
        Categoria(nombre = "Salud", tipo = TipoTransaccion.GASTO, icono = "⚕️"),
        Categoria(nombre = "Educación", tipo = TipoTransaccion.GASTO, icono = "📚"),
        Categoria(nombre = "Entretenimiento", tipo = TipoTransaccion.GASTO, icono = "🎬"),
        Categoria(nombre = "Ropa", tipo = TipoTransaccion.GASTO, icono = "👕"),
        Categoria(nombre = "Servicios", tipo = TipoTransaccion.GASTO, icono = "💡"),
        Categoria(nombre = "Otros Gastos", tipo = TipoTransaccion.GASTO, icono = "🛒")
    )

    val todasLasCategorias = categoriasIngreso + categoriasGasto
}

