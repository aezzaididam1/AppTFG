
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.amine.mytfg.databases.Habito
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import java.util.Calendar
import java.util.Date

class HabitoRepository(val context: Context) {
    private val db = FirebaseFirestore.getInstance()

    fun guardarHabito(nombreHabito: String, fechaInicial: Date, fechaFinal: Date?, diasActivos: List<Boolean>,
                      onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
        val habito = hashMapOf(
            "nombre" to nombreHabito,
            "fechaInicial" to fechaInicial,
            "fechaFinal" to fechaFinal,
            "diasActivos" to diasActivos,
            "isCompleted" to false
        )

        db.collection("habitos")
            .add(habito)
            .addOnSuccessListener { documentReference ->
                Toast.makeText(context, "Hábito guardado con éxito con ID: ${documentReference.id}", Toast.LENGTH_SHORT).show()
                onSuccess(documentReference.id)  // Devuelve el ID del nuevo documento creado
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error al guardar el hábito: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                onFailure(e)
            }
    }


    fun obtenerHabitosPorFecha(date: Date, onSuccess: (List<Habito>) -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("habitos").get()
            .addOnSuccessListener { result ->
                val habitosFiltrados = filtrarHabitos(result, date)
                onSuccess(habitosFiltrados)
            }
            .addOnFailureListener { exception ->
                Log.e("FirestoreError", "fallo al cargar los habitos: ", exception)
                onFailure(exception)
            }
    }

    fun filtrarHabitos(querySnapshot: QuerySnapshot, date: Date): List<Habito> {
        val calendar = Calendar.getInstance()
        calendar.time = date
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1  // Asumiendo que 0 es Domingo

        return querySnapshot.documents.mapNotNull { doc ->
            doc.toObject(Habito::class.java)?.apply {
                this.id = doc.id  // Almacena el ID del documento en el objeto Tarea
            }?.takeIf { tarea ->
                val isActiveToday = tarea.diasActivos.size > dayOfWeek && tarea.diasActivos[dayOfWeek]
                val tareaFechaInicial = tarea.fechaInicial ?: Date(Long.MIN_VALUE)
                val tareaFechaFinal = tarea.fechaFinal ?: Date(Long.MAX_VALUE)
                isActiveToday && !tareaFechaInicial.after(date) && !tareaFechaFinal.before(date)
            }
        }
    }


    fun updateHabitoCompletion(habitoId: String, isCompleted: Boolean, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("habitos")
            .document(habitoId)
            .update("isCompleted", isCompleted)
            .addOnSuccessListener {
                Log.d("HabitoRepository", "Hábito actualizado con éxito.")
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.e("HabitoRepository", "Error al actualizar el hábito.", e)
                onFailure(e)
            }
    }
    fun contarHabitosCompletados(onResult: (completados: Int, noCompletados: Int) -> Unit) {
        db.collection("habitos")
            .get()
            .addOnSuccessListener { result ->
                val total = result.size()
                val completados = result.documents.count { doc ->
                    doc.getBoolean("isCompleted") == true
                }
                onResult(completados, total - completados)
            }
            .addOnFailureListener { exception ->
                Log.e("FirestoreError", "Error al cargar datos de hábitos", exception)
            }
    }



}
