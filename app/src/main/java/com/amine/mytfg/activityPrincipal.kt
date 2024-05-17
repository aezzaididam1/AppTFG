package com.amine.mytfg

import HabitoRepository
import TaskRepository
import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.amine.mytfg.Fragments.EstadisticaFragment
import com.amine.mytfg.Fragments.HabitoFragment
import com.amine.mytfg.Fragments.SettingsFragment
import com.amine.mytfg.Fragments.TareaFragment
import com.amine.mytfg.Services.ReminderBroadcastReceiver
import com.amine.mytfg.databases.Task
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class activityPrincipal : AppCompatActivity() {
    private lateinit var taskRepository: TaskRepository

    private lateinit var tvOpenDAta: TextView
    private lateinit var textoFechaFin: TextView
    private lateinit var btnFechaInicio: ImageButton
    private lateinit var alarmManager: AlarmManager

    private val dateFormatter = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())


    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_principal)
        initializeAlarmManager()

        taskRepository = TaskRepository()


        if (!alarmManager.canScheduleExactAlarms()) {
            // Mostrar un diálogo explicativo antes de lanzar la configuración
            AlertDialog.Builder(this)
                .setTitle("Permiso necesario")
                .setMessage("Esta aplicación necesita permiso para alarmas exactas para recordatorios precisos. Por favor, permita el uso de alarmas exactas en la siguiente pantalla.")
                .setPositiveButton("Aceptar") { dialog, which ->
                    val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                    startActivity(intent)
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    455432 // Un código de solicitud que definimos nosotros
                )
            }
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }


        setupBottomNavigationView()
        setupFloatingActionButton()
    }

    private fun initializeAlarmManager() {
        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {

        val name = getString(R.string.channel_name) // Definimos un nombre para el canal
        val descriptionText = getString(R.string.channel_description)
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel =
            NotificationChannel(ReminderBroadcastReceiver.CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }

        // Obtiene el NotificationManager usando getSystemService
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }


    private fun setupBottomNavigationView() {
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.inicio_item -> replaceFragment(HabitoFragment())
                R.id.desafios_item -> replaceFragment(TareaFragment())
                R.id.productividad_item -> replaceFragment(EstadisticaFragment())
                R.id.ajustes_item -> replaceFragment(SettingsFragment())
            }
            true
        }
        bottomNavigationView.selectedItemId = R.id.inicio_item
    }

    private fun setupFloatingActionButton() {
        val fabAddTask: FloatingActionButton = findViewById(R.id.fab_add_task)
        fabAddTask.setOnClickListener {
            dialogoAñadirTareaHabito()
        }
    }

    companion object {
        private const val REQUEST_CODE_OVERLAY_PERMISSION = 101
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(
            R.id.fragment_container,
            fragment
        )
        transaction.commit()
    }

    private fun dialogoAñadirTareaHabito() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.activity_crear_tarea)
        dialog.window?.let { window ->
            // Hace el fondo del diálogo completamente transparente
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            // Obtenemos el tamaño de la pantalla para establecer límites
            val displayMetrics = resources.displayMetrics
            val width = displayMetrics.widthPixels
            val height = displayMetrics.heightPixels


            // Establece el ancho a un porcentaje específico de la pantalla
            val maxWidth = (width * 0.87).toInt()
            val maxHeight = (height * 0.65).toInt() // Ajustamos este porcentaje para cambiar la altura

            // Ajusta el tamaño según el contenido, pero no permite que sea más ancho que maxWidth
            window.setLayout(maxWidth, maxHeight)
        }

        setupDialogButtons(dialog)

        dialog.show()

    }

    private fun setupDialogButtons(dialog: Dialog) {

        dialog.findViewById<TextView>(R.id.tvCrearTarea)?.setOnClickListener {
            showCreateTaskDialog()
        }

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
            window.setLayout(
                maxWidth,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ) // Ajuste del tamaño del diálogo
        }

        val etNombreHabito = createHabitDialog.findViewById<EditText>(R.id.etNombreHabito)
        etNombreHabito.setText(habitName)
        setupConfigHabitButtons(createHabitDialog)

        createHabitDialog.findViewById<Button>(R.id.btnSaveHabit).setOnClickListener {
            val name = etNombreHabito.text.toString().trim()
            if (name.isEmpty()) {
                Toast.makeText(
                    this,
                    "Por favor, ingrese un nombre para el hábito.",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val daysOfWeek = listOf(
                createHabitDialog.findViewById<CheckBox>(R.id.cbSunday).isChecked,
                createHabitDialog.findViewById<CheckBox>(R.id.cbMonday).isChecked,
                createHabitDialog.findViewById<CheckBox>(R.id.cbTuesday).isChecked,
                createHabitDialog.findViewById<CheckBox>(R.id.cbWednesday).isChecked,
                createHabitDialog.findViewById<CheckBox>(R.id.cbThursday).isChecked,
                createHabitDialog.findViewById<CheckBox>(R.id.cbFriday).isChecked,
                createHabitDialog.findViewById<CheckBox>(R.id.cbSaturday).isChecked

            )

            val formatter = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
            val selectedDateInicio = formatter.parseOrNull(tvOpenDAta.text.toString())
            val selectedDateFin = formatter.parseOrNull(textoFechaFin.text.toString())

            if (selectedDateInicio != null && (selectedDateFin == null || !selectedDateInicio.after(
                    selectedDateFin
                ))
            ) {
                HabitoRepository(this).guardarHabito(
                    name,
                    selectedDateInicio,
                    selectedDateFin,
                    daysOfWeek,
                    {
                        Toast.makeText(
                            this@activityPrincipal,
                            "Hábito guardado con éxito",
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    { e ->
                        Toast.makeText(
                            this@activityPrincipal,
                            "Error al guardar el hábito: ${e.localizedMessage}",
                            Toast.LENGTH_LONG
                        ).show()
                    })
            } else {
                Toast.makeText(
                    this,
                    "La fecha de inicio no puede ser posterior a la fecha final.",
                    Toast.LENGTH_LONG
                ).show()
            }
            createHabitDialog.dismiss()
        }
        createHabitDialog.show()
    }

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private fun showCreateTaskDialog() {
        val taskDialog = Dialog(this)
        taskDialog.setContentView(R.layout.activity_config_tarea)
        taskDialog.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val displayMetrics = resources.displayMetrics
            val width = displayMetrics.widthPixels
            setLayout((width * 0.87).toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
        }

        val tvDate = taskDialog.findViewById<TextView>(R.id.tvTaskDate)
        val btnSetDate = taskDialog.findViewById<Button>(R.id.btnSetTaskDate)
        val timePickerStart = taskDialog.findViewById<TimePicker>(R.id.timePickerStart)
        val timePickerEnd = taskDialog.findViewById<TimePicker>(R.id.timePickerEnd)
        val btnSaveTask = taskDialog.findViewById<Button>(R.id.btnSaveTask)
        val etTaskName = taskDialog.findViewById<EditText>(R.id.etTaskName)
        val switchNotifications = taskDialog.findViewById<Switch>(R.id.switchNotifications)

        var selectedDate: String? = null

        btnSetDate.setOnClickListener {
            openDatePickerDialog { calendar ->
                selectedDate =
                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
                tvDate.text = selectedDate
            }
        }

        btnSaveTask.setOnClickListener {
            val name = etTaskName.text.toString().trim()
            val date = tvDate.text.toString()
            val startTime = "${timePickerStart.hour}:${timePickerStart.minute}"
            val endTime = "${timePickerEnd.hour}:${timePickerEnd.minute}"
            val notificationsEnabled = switchNotifications.isChecked

            if (name.isBlank()) {
                Toast.makeText(
                    this,
                    "Por favor, ingrese un nombre para la tarea.",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val task = Task(
                title = name,
                date = date,
                startTime = startTime,
                endTime = endTime,
                notify = notificationsEnabled
            )
            saveTaskToFirebase(task)
            taskDialog.dismiss()
        }

        taskDialog.show()
    }


    private fun saveTaskToFirebase(task: Task) {
        taskRepository.saveTask(task,
            onSuccess = {
                Toast.makeText(this, "Tarea guardada con éxito", Toast.LENGTH_SHORT).show()
                if (task.notify) {
                    programarNotificacion(this, task)
                }
            },
            onFailure = { e ->
                Toast.makeText(
                    this,
                    "Error al guardar la tarea: ${e.localizedMessage}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
    }

    fun programarNotificacion(context: Context, task: Task) {
        Log.d("programarNotificacion", " notification para tarea: ${task.title}")

        val taskDate = parseDate(task) ?: return
        val notificationTime = Calendar.getInstance().apply {
            time = taskDate
            add(Calendar.HOUR, -1)  // Notificación programada para una hora antes
        }

        // Aquí se muestra el log con la hora exacta de la alarma programada
        Log.d("programarNotificacion", "Alarm puesta para: ${notificationTime.time}")

        val intent = Intent(context, ReminderBroadcastReceiver::class.java).apply {
            putExtra("titulo tarea", task.title)
            putExtra("hora tarea", task.startTime)
        }
        val requestCode = task.title.hashCode()
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    notificationTime.timeInMillis,
                    pendingIntent
                )
                Log.d("programarNotificacion", "alarma programada con exito")
            } else {
                Log.e("AlarmManager", "permisos no concedidos")
                // Considerar alternativas como notificaciones inexactas
            }
        } else {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                notificationTime.timeInMillis,
                pendingIntent
            )
            Log.d("programarNotificacion", "otra version.")
        }
    }


    fun parseDate(task: Task): Date? {
        val dateTimeString = "${task.date} ${task.startTime}"
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        return try {
            format.parse(dateTimeString)
        } catch (e: ParseException) {
            Log.e("ParseDate", "Failed to parse date: $dateTimeString", e)
            null
        }
    }


    fun openDatePickerDialog(onDateSelected: (Calendar) -> Unit) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            this,
            { _, year, monthOfYear, dayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, monthOfYear, dayOfMonth, 0, 0, 0)
                selectedDate.set(Calendar.MILLISECOND, 0)
                onDateSelected(selectedDate)
                Log.d("DatePicker", "Fecha seleccionada: ${selectedDate.time}")
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }



    // Helper function to safely parse dates
    fun SimpleDateFormat.parseOrNull(text: String): Date? = try {
        parse(text)
    } catch (e: ParseException) {
        null
    }


}