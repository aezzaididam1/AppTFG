package com.amine.mytfg.databases

data class Task(
    var id: String? = null,
    var title: String = "",
    var date: String = "",  // uso String para facilitar la manipulación con Firestore
    var startTime: String = "",
    var endTime: String = "",
    val notify: Boolean = false
)
