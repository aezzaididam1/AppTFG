import android.content.Context
import android.widget.Toast
import com.amine.mytfg.databases.Tarea
import com.google.firebase.firestore.FirebaseFirestore

class HabitoRepository(val context: Context) {
    private val db = FirebaseFirestore.getInstance()

    fun guardarHabito(nombreHabito: String, fechaInicial: String, fechaFinal: String, diasActivos: List<Boolean>,
                      onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val habito = hashMapOf(
            "nombre" to nombreHabito,
            "fechaInicial" to fechaInicial,
            "fechaFinal" to fechaFinal,
            "diasActivos" to diasActivos
        )

        db.collection("habitos")
            .add(habito)
            .addOnSuccessListener { documentReference ->
                Toast.makeText(context, "Hábito guardado con éxito con ID: ${documentReference.id}", Toast.LENGTH_SHORT).show()
                onSuccess()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error al guardar el hábito: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                onFailure(e)
            }
    }

    fun obtenerHabitos(onSuccess: (List<Tarea>) -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("habitos").get()
            .addOnSuccessListener { result ->
                val habitos = result.map { doc -> doc.toObject(Tarea::class.java) }
                onSuccess(habitos)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
}
