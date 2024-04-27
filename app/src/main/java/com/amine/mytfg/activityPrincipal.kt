package com.amine.mytfg

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class activityPrincipal : AppCompatActivity() {
    private lateinit var tvOpenDAta: TextView
    private lateinit var btnFechaInicio: ImageButton
    private val dateFormatter = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())


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
            val height = displayMetrics.heightPixels


            // Establece el ancho a un porcentaje específico de la pantalla, p. ej., 90% del ancho
            val maxWidth = (width * 0.87).toInt()
            val maxHeight = (height * 0.65).toInt() // Ajusta este porcentaje para cambiar la altura



            // Ajusta el tamaño según el contenido, pero no permite que sea más ancho que maxWidth
            window.setLayout(maxWidth, maxHeight)
        }

        setupDialogButtons(dialog)

        dialog.show()

    }

    private fun setupDialogButtons(dialog: Dialog) {
        dialog.findViewById<TextView>(R.id.tvCrearHabitoPersonalizado)?.setOnClickListener {
            // Aquí manejas lo que ocurre cuando el usuario hace clic en crear habito personalizado
            Toast.makeText(this, "Hábito creado con éxito", Toast.LENGTH_SHORT).show()
            dialog.dismiss() // Cierra el diálogo
        }

        dialog.findViewById<TextView>(R.id.tvDucharseConAguaFria).setOnClickListener {
            showCreateHabitDialog()
            //dialog.dismiss()
        }


    }

    private fun setupConfigHabitButtons(dialog: Dialog) {
        tvOpenDAta = dialog.findViewById<TextView>(R.id.tvOpenDatePicker)

        btnFechaInicio = dialog.findViewById<ImageButton>(R.id.btnDataPickerInicio)
        btnFechaInicio.setOnClickListener {
            openDatePickerDialog()

        }

    }


    fun showCreateHabitDialog() {
        val createHabitDialog = Dialog(this)
        createHabitDialog.setContentView(R.layout.activity_config_habito1)
        createHabitDialog.window?.let { window ->
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) // Fondo transparente

            val displayMetrics = resources.displayMetrics
            val width = displayMetrics.widthPixels
            val height = displayMetrics.heightPixels

            val maxWidth = (width * 0.87).toInt() // Establece el ancho a un porcentaje específico
            val maxHeight = (height * 0.65).toInt() // Ajusta este porcentaje para cambiar la altura

            // Ajusta el tamaño según el contenido, pero no permite que sea más ancho que maxWidth
            window.setLayout(maxWidth, ViewGroup.LayoutParams.WRAP_CONTENT)
        }

        createHabitDialog.show()
        setupConfigHabitButtons(createHabitDialog)


    }

    private fun openDatePickerDialog() {


        // Obtener la fecha actual para preseleccionarla en el DatePicker
        val calendar = Calendar.getInstance()

        val datePickerDialog = DatePickerDialog(
            this,

            DatePickerDialog.OnDateSetListener { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, month, dayOfMonth)
                Log.i("pruebas", "openDatePickerDialog: funciona ")
                // Formatear la fecha seleccionada al formato deseado
                val formattedDate = this.dateFormatter.format(selectedDate.time)
                Log.i("prb", "formattedDate: " + formattedDate)
                // Establecer la fecha formateada en el EditText
                tvOpenDAta.setText(formattedDate)

                //Modificamos selectedDate en la activity (variable global)
                //MainActivity().selectedDate = selectedDate.time
                //Log.i("PATATA", "openDatePickerDialog- SELECTED DATE: " +  MainActivity().selectedDate) //Sun Apr 21 11:21:30 GMT+02:00 2024
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH),
        )

        // Mostrar el DatePickerDialog
        datePickerDialog.show()
    }



}