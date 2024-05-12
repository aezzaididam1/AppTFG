package com.amine.mytfg.Fragments

import TaskRepository
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.amine.mytfg.Adapters.TaskAdapter
import com.amine.mytfg.databases.Task
import com.amine.mytfg.databinding.FragmentTareasBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class TareaFragment : Fragment() {

    private var _binding: FragmentTareasBinding? = null
    private val binding get() = _binding!!
    private lateinit var taskAdapter: TaskAdapter
    private val taskRepository = TaskRepository()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) // Formato de fecha usado en Firestore
    private lateinit var today : String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTareasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupCalendar()

        // Carga inicial de tareas para el día actual
         today = dateFormat.format(Calendar.getInstance().time)
        loadTasksForSelectedDate(today)

        // Setup SwipeRefreshLayout
        binding.swipeRefreshLayout.setOnRefreshListener {
            val formattedDate = dateFormat.format(Calendar.getInstance().time)  // Puedes ajustar según el último día seleccionado si es necesario
            today = formattedDate
            loadTasksForSelectedDate(formattedDate)
        }
    }

    private fun setupRecyclerView() {
        taskAdapter = TaskAdapter(mutableListOf(), requireContext(), { task ->
            Toast.makeText(context, "Task clicked: ${task.title}", Toast.LENGTH_SHORT).show()
        }, { task ->
            showDeleteDialog(task)
        })
        binding.tasksRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = taskAdapter
            setNestedScrollingEnabled(false) // Mejorar el rendimiento dentro de NestedScrollView
        }
    }

    private fun showDeleteDialog(task: Task) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Task")
            .setMessage("Are you sure you want to delete this task?")
            .setPositiveButton("Delete") { dialog, which ->
                deleteTask(task)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteTask(task: Task) {
        taskRepository.deleteTask(task) { isSuccess ->
            if (isSuccess) {
                val formattedDate = dateFormat.format(Calendar.getInstance().time)
                loadTasksForSelectedDate(formattedDate)  // Reload tasks
                Toast.makeText(context, "Task deleted successfully", Toast.LENGTH_SHORT).show()
                loadTasksForSelectedDate(formattedDate  )
            } else {
                Toast.makeText(context, "Failed to delete task", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupCalendar() {
        binding.calendarView.setOnDateChangedListener { widget, date, selected ->
            val calendar = Calendar.getInstance()
            calendar.set(date.year, date.month - 1, date.day)
            val selectedDate = calendar.time
            val formattedDate = dateFormat.format(selectedDate)
            loadTasksForSelectedDate(formattedDate)
            Log.d("CalendarView", "Fecha seleccionada: $formattedDate")
        }
    }

    private fun loadTasksForSelectedDate(date: String) {
        taskRepository.loadTasksForDate(date) { tasks ->
            activity?.runOnUiThread {
                if (tasks.isNotEmpty()) {
                    taskAdapter.updateTasks(tasks)
                    Log.d("TasksLoading", "Tareas actualizadas en el adaptador: ${tasks.size}")
                } else {
                    Log.d("TasksLoading", "No se encontraron tareas para la fecha: $date")
                }
                binding.swipeRefreshLayout.isRefreshing = false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
