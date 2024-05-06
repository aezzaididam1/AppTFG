package com.amine.mytfg

import FirstFragment
import HabitoRepository
import android.app.DatePickerDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class activityPrincipal : AppCompatActivity() {
    private lateinit var tvOpenDAta: TextView
    private lateinit var textoFechaFin: TextView
    private lateinit var btnFechaInicio: ImageButton
    private val dateFormatter = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_principal)

        val fabAddTask: FloatingActionButton = findViewById(R.id.fab_add_task)
        fabAddTask.setOnClickListener {
            showAddTaskDialog()
        }
        // Configuración del BottomNavigationView utilizando el nuevo listener
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.inicio_item -> replaceFragment(FirstFragment())
                R.id.desafios_item -> replaceFragment(SecondFragment())
                // Asegúrate de añadir los otros fragmentos si tienes más ítems
            }
            true  // Indica que el evento de selección fue manejado
        }

        // Configura el fragmento inicial (opcional)
        if (savedInstanceState == null) {
            bottomNavigationView.selectedItemId = R.id.inicio_item
        }

    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)  // Asegúrate de que el ID del contenedor está correctamente definido en tu layout
        transaction.commit()
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
            showCreateHabitDialog()
        }

        dialog.findViewById<TextView>(R.id.tvDucharseConAguaFria).setOnClickListener {
            showCreateHabitDialog("Ducharse con agua fría")
        }

        dialog.findViewById<TextView>(R.id.tvComerSano).setOnClickListener {
            showCreateHabitDialog("Comer sano")
        }

        dialog.findViewById<TextView>(R.id.tvHagaSuCama).setOnClickListener {
            showCreateHabitDialog("Hacer la cama")
        }

        dialog.findViewById<TextView>(R.id.tvSalirPasear).setOnClickListener {
            showCreateHabitDialog("Salir a pasear")
        }

    }


    private fun setupConfigHabitButtons(dialog: Dialog) {
        // Configuración de la fecha de inicio
        tvOpenDAta = dialog.findViewById<TextView>(R.id.tvOpenDatePicker)
        btnFechaInicio = dialog.findViewById<ImageButton>(R.id.btnDataPickerInicio)
        btnFechaInicio.setOnClickListener {
            openDatePickerDialog { selectedDate ->
                tvOpenDAta.text = dateFormatter.format(selectedDate.time)
            }
        }

        // Configuración de la fecha final
        textoFechaFin = dialog.findViewById<TextView>(R.id.tvCloseDatePicker)
        val tvCloseDAta = dialog.findViewById<TextView>(R.id.tvCloseDatePicker)
        val btnFechaFinal = dialog.findViewById<ImageButton>(R.id.btnDataPickerFinal)
        btnFechaFinal.setOnClickListener {
            openDatePickerDialog { selectedDate ->
                tvCloseDAta.text = dateFormatter.format(selectedDate.time)
            }
        }

        // Manejo del switch para mostrar u ocultar la fecha final
        val switchFechaFinal = dialog.findViewById<Switch>(R.id.switchFechaFinal)
        val llFechaFinal = dialog.findViewById<LinearLayout>(R.id.llFechaFinal)

        switchFechaFinal.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                llFechaFinal.visibility = View.VISIBLE
            } else {
                llFechaFinal.visibility = View.GONE
            }
        }
    }




    fun showCreateHabitDialog(habitName: String? = null) {
        val createHabitDialog = Dialog(this)
        createHabitDialog.setContentView(R.layout.activity_config_habito1)
        createHabitDialog.window?.let { window ->
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val displayMetrics = resources.displayMetrics
            val width = displayMetrics.widthPixels
            val height = displayMetrics.heightPixels

            val maxWidth = (width * 0.87).toInt()
            val maxHeight = (height * 0.65).toInt()
            window.setLayout(maxWidth, ViewGroup.LayoutParams.WRAP_CONTENT)
        }

        val etNombreHabito = createHabitDialog.findViewById<EditText>(R.id.etNombreHabito)
        etNombreHabito.setText(habitName)

        setupConfigHabitButtons(createHabitDialog)

        createHabitDialog.findViewById<Button>(R.id.btnSaveHabit).setOnClickListener {
            val name = etNombreHabito.text.toString().trim()
            if (name.isEmpty()) {
                Toast.makeText(this, "Por favor, ingrese un nombre para el hábito.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Recolectar información de los CheckBoxes
            val daysOfWeek = listOf(
                createHabitDialog.findViewById<CheckBox>(R.id.cbMonday).isChecked,
                createHabitDialog.findViewById<CheckBox>(R.id.cbTuesday).isChecked,
                createHabitDialog.findViewById<CheckBox>(R.id.cbWednesday).isChecked,
                createHabitDialog.findViewById<CheckBox>(R.id.cbThursday).isChecked,
                createHabitDialog.findViewById<CheckBox>(R.id.cbFriday).isChecked,
                createHabitDialog.findViewById<CheckBox>(R.id.cbSaturday).isChecked,
                createHabitDialog.findViewById<CheckBox>(R.id.cbSunday).isChecked
            )

            val selectedDateInicio = tvOpenDAta.text.toString()
            val selectedDateFin = textoFechaFin.text.toString()

            val habitoRepository = HabitoRepository(this)
            habitoRepository.guardarHabito(name, selectedDateInicio, selectedDateFin, daysOfWeek, {
                Toast.makeText(this@activityPrincipal, "Hábito guardado con éxito", Toast.LENGTH_SHORT).show()
            }, { e ->
                Toast.makeText(this@activityPrincipal, "Error al guardar el hábito: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            })

            createHabitDialog.dismiss()
        }

        createHabitDialog.show()
    }



    fun openDatePickerDialog(onDateSelected: (Calendar) -> Unit) {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, month, dayOfMonth)
                onDateSelected(selectedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }




}