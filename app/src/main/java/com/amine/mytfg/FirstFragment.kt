import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.amine.mytfg.CustomAdapter
import com.amine.mytfg.databases.Tarea
import com.amine.mytfg.databinding.FragmentFirstBinding

class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        cargarHabitos()
    }

    private fun setupRecyclerView() {
        // Inicializa el RecyclerView con una MutableList vacía
        binding.recycledView.layoutManager = LinearLayoutManager(requireContext())
        binding.recycledView.adapter = CustomAdapter(mutableListOf(), onItemChecked = { position, isChecked ->
            handleTaskCompletion(position, isChecked)
        })
    }


    private fun cargarHabitos() {
        val repo = HabitoRepository(requireContext())
        repo.obtenerHabitos(
            onSuccess = { habitos ->
                (binding.recycledView.adapter as CustomAdapter).updateData(habitos)
            },
            onFailure = { exception ->
                Toast.makeText(requireContext(), "Error al cargar los hábitos: ${exception.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        )
    }

    private fun handleTaskCompletion(position: Int, isChecked: Boolean) {
        // Implementa la lógica para actualizar el estado de la tarea como completada
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
