package com.amine.mytfg.databases

data class Tarea(
    val nombre: String = "",
    val fechaIncial: String = "",
    val fechaFinal: String = "",
    val diasActivos: List<Boolean> = listOf(false, false, false, false, false, false, false)

)
