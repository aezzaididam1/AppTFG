package com.amine.mytfg

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText

class activityPrincipal : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_principal)

        val fabAddTask: FloatingActionButton = findViewById(R.id.fab_add_task)
        fabAddTask.setOnClickListener {
            showAddTaskDialog()
        }
    }
    private fun showAddTaskDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.activity_crear_tarea)
        dialog.window?.let { window ->
            // Hace el fondo del diálogo completamente transparente
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            // Obtén el tamaño de la pantalla para establecer límites
            val displayMetrics = resources.displayMetrics
            val width = displayMetrics.widthPixels


            // Establece el ancho a un porcentaje específico de la pantalla, p. ej., 90% del ancho
            val maxWidth = (width * 0.9).toInt()


            // Ajusta el tamaño según el contenido, pero no permite que sea más ancho que maxWidth
            window.setLayout(maxWidth, ViewGroup.LayoutParams.WRAP_CONTENT)
        }

        dialog.show()
    }

}