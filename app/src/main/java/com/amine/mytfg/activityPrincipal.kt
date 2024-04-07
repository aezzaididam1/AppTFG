package com.amine.mytfg

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton

class activityPrincipal : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_principal)

        val fabAddTask: FloatingActionButton = findViewById(R.id.fab_add_task)
        fabAddTask.setOnClickListener {
            // Aquí puedes mostrar un diálogo o una nueva actividad para añadir la tarea
            showAddTaskDialog()
        }
    }
    private fun showAddTaskDialog() {
        val intent = Intent(this, InicioSesionActivity::class.java)
        startActivity(intent)    }
}