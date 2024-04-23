package com.amine.mytfg

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText

class CrearTareaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_tarea)

        /**val btnAddTask: Button = findViewById(R.id.bAddQuantity)
        val etTaskName: TextInputEditText = findViewById(R.id.etAmount)
        btnAddTask.setOnClickListener {
            val taskName = etTaskName.text.toString().trim()
            if (taskName.isNotEmpty()) {
                // Aquí psrs añadir la tarea a la base de datos o lista
                Toast.makeText(this, "Tarea '$taskName' creada", Toast.LENGTH_SHORT).show()
                finish() // Cierra la actividad y regresa a la anterior
            } else {
                Toast.makeText(this, "Por favor, ingresa un nombre para la tarea", Toast.LENGTH_SHORT).show()
            }
        }*/

    }

}