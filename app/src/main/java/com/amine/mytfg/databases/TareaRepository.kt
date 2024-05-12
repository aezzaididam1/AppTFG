
import android.util.Log
import com.amine.mytfg.databases.Task
import com.google.firebase.firestore.FirebaseFirestore

class TaskRepository {

    private val db = FirebaseFirestore.getInstance()

    fun saveTask(task: Task, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        if (task.id.isNullOrEmpty()) {
            db.collection("tasks").add(task)
                .addOnSuccessListener { documentReference ->
                    Log.d("Firebase", "DocumentSnapshot written with ID: ${documentReference.id}")
                    onSuccess()
                }
                .addOnFailureListener { e ->
                    Log.w("Firebase", "Error adding document", e)
                    onFailure(e)
                }
        } else {
            db.collection("tasks").document(task.id!!).set(task)
                .addOnSuccessListener {
                    Log.d("Firebase", "Task updated successfully!")
                    onSuccess()
                }
                .addOnFailureListener { e ->
                    Log.w("Firebase", "Error updating task", e)
                    onFailure(e)
                }
        }
    }

    fun loadTasksForDate(date: String, onResult: (List<Task>) -> Unit) {
        db.collection("tasks")
            .whereEqualTo("date", date)
            .get()
            .addOnSuccessListener { documents ->
                val tasks = documents.map { doc ->
                    Task(
                        id = doc.id,
                        title = doc.getString("title") ?: "",
                        date = doc.getString("date") ?: "",
                        startTime = doc.getString("startTime") ?: "",
                        endTime = doc.getString("endTime") ?: "",
                        notify = doc.getBoolean("notify") ?: false
                    )
                }
                onResult(tasks)
            }
            .addOnFailureListener { exception ->
                Log.e("TaskRepository", "Error loading tasks for date $date", exception)
            }
    }

    fun deleteTask(task: Task, onComplete: (Boolean) -> Unit) {
        task.id?.let { id ->
            // Si el ID no es nulo, procede con la eliminación
            db.collection("tasks").document(id).delete()
                .addOnSuccessListener {
                    Log.d("Firebase", "Task borrada")
                    onComplete(true)
                }
                .addOnFailureListener { e ->
                    Log.e("Firebase", "Error borrando la  task", e)
                    onComplete(false)
                }
        } ?: run {
            // Si el ID es nulo, loguea un error e
            Log.e("Firebase", "Error: el ID es null, no se puede borrar")
            onComplete(false)
        }
    }

}
