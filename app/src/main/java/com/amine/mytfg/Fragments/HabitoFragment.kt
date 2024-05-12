package com.amine.mytfg.Fragments

import HabitoRepository
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.amine.mytfg.Adapters.CustomAdapterHabitos
import com.amine.mytfg.databases.Habito
import com.amine.mytfg.databinding.FragmentHabitosBinding
import java.util.Calendar
import java.util.Date

class HabitoFragment : Fragment() {

    private var _binding: FragmentHabitosBinding? = null
    private val binding get() = _binding!!
    private var selectedDate: Date = Calendar.getInstance().time // Variable para mantener la fecha seleccionada

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHabitosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        cargarHabitosPorFecha(selectedDate)  // Carga inicial con la fecha seleccionada

        // Listener para cambios de fecha en el calendario
        binding.calendarView.setOnDateChangedListener { widget, date, selected ->
            val newSelectedDate = Calendar.getInstance()
            newSelectedDate.set(date.year, date.month - 1, date.day, 0, 0, 0)
            newSelectedDate.set(Calendar.MILLISECOND, 0)
            selectedDate = newSelectedDate.time // Actualiza la fecha seleccionada
            Log.d("CalendarView", "Fecha seleccionada: ${selectedDate}")
            cargarHabitosPorFecha(selectedDate)
        }

        // Listener para refrescar el RecyclerView al arrastrar hacia abajo
        binding.swipeRefreshLayout.setOnRefreshListener {
            cargarHabitosPorFecha(selectedDate) // Usa la fecha seleccionada para refrescar
        }
    }

    private fun setupRecyclerView() {
        binding.recycledView.layoutManager = LinearLayoutManager(requireContext())
        binding.recycledView.adapter = CustomAdapterHabitos(mutableListOf(), requireContext(),
            { habito: Habito, isCompleted: Boolean -> handleItemUpdate(habito, isCompleted) })
    }



    private fun handleItemUpdate(habito: Habito, isCompleted: Boolean) {
        // Aquí puedes manejar lo que sucede cuando un hábito se marca como completado o no
        // Por ejemplo, podrías mostrar un Toast o actualizar alguna interfaz
    }
    private fun cargarHabitosPorFecha(date: Date) {
        Log.d("Habitos", "Cargando hábitos para la fecha: $date")
        binding.swipeRefreshLayout.isRefreshing = true
        val repo = HabitoRepository(requireContext())
        repo.obtenerHabitosPorFecha(date,
            onSuccess = { habitos ->
                Log.d("Habitos", "Hábitos cargados: ${habitos.size}")
                (binding.recycledView.adapter as CustomAdapterHabitos).updateData(habitos)
                binding.swipeRefreshLayout.isRefreshing = false
            },
            onFailure = { exception ->
                Log.e("Habitos", "Error al cargar hábitos", exception)
                Toast.makeText(requireContext(), "Error al cargar los hábitos: ${exception.localizedMessage}", Toast.LENGTH_LONG).show()
                binding.swipeRefreshLayout.isRefreshing = false
            }
        )
    }

    private fun handleTaskCompletion(position: Int, isChecked: Boolean) {
        // Implementa lógica para manejar la finalización de tareas
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
