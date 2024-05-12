package com.amine.mytfg.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.amine.mytfg.R
import com.amine.mytfg.databases.Task

class TaskAdapter(
    private var tasks: MutableList<Task>,
    private val context: Context,
    private val onItemClicked: (Task) -> Unit,
    private val onDeleteClicked: (Task) -> Unit  // Añade esta función de callback para manejar la eliminación
) : RecyclerView.Adapter<TaskAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_tarea, parent, false)
        return ViewHolder(view, onDeleteClicked)  // Pasa el callback al ViewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val task = tasks[position]
        holder.bind(task)
        holder.itemView.setOnClickListener { onItemClicked(task) }
    }

    override fun getItemCount() = tasks.size

    fun updateTasks(newTasks: List<Task>) {
        tasks.clear()
        tasks.addAll(newTasks)
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View, private val onDeleteClicked: (Task) -> Unit) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        private val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        private val startTimeTextView: TextView = itemView.findViewById(R.id.startTimeTextView)
        private val endTimeTextView: TextView = itemView.findViewById(R.id.endTimeTextView)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.deleteButton) // Referencia al botón de eliminar

        fun bind(task: Task) {
            titleTextView.text = task.title
            dateTextView.text = "Date: ${task.date}"
            startTimeTextView.text = "Start: ${task.startTime}"
            endTimeTextView.text = "End: ${task.endTime}"

            deleteButton.setOnClickListener {
                onDeleteClicked(task)  // Llama al callback cuando se presiona el botón de eliminar
            }
        }
    }
}
