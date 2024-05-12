package com.amine.mytfg.databases

import java.util.Date

data class Habito(
    var id: String = "",
    val nombre: String = "",
    val fechaInicial: Date? = null,
    val fechaFinal: Date? = null,    // Puede ser nulo si no se define una fecha de finalización
    val diasActivos: List<Boolean> = listOf(false, false, false, false, false, false, false),
    var isCompleted: Boolean = false
)
