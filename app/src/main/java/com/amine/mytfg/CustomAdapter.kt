package com.amine.mytfg

import HabitoRepository
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.amine.mytfg.databases.Tarea
import java.text.SimpleDateFormat
import java.util.Locale

class CustomAdapter(
    private var tasks: MutableList<Tarea>,
    private val context: Context,  // Usamos el contexto proporcionado
    private val onItemUpdated: (Tarea, Boolean) -> Unit
) : RecyclerView.Adapter<CustomAdapter.ViewHolder>() {
    private val habitoRepository = HabitoRepository(context)  // Usar el contexto del adaptador
    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val task = tasks[position]
        holder.itemTitle.text = task.nombre
        holder.itemDetail.text = dateFormat.format(task.fechaInicial)
        holder.itemDetail2.text = task.fechaFinal?.let { dateFormat.format(it) } ?: "sin finalización"
        holder.itemImage.setImageResource(R.drawable.ic_lock)

        // Configurar el estado del CheckBox antes de establecer el listener
        holder.itemCheck.isChecked = task.isCompleted

        // Establecer el listener para el CheckBox después de configurar su estado inicial
        holder.itemCheck.setOnCheckedChangeListener(null)  // Remover cualquier listener previo
        holder.itemCheck.setOnCheckedChangeListener { _, isChecked ->
            task.isCompleted = isChecked
            onItemUpdated(task, isChecked)
            habitoRepository.updateHabitoCompletion(task.id, isChecked, {
                Log.d("CustomAdapter", "Hábito actualizado correctamente.")
            }, { error ->
                Log.e("CustomAdapter", "Error al actualizar el hábito.", error)
            })
        }
    }



    override fun getItemCount() = tasks.size

    fun updateData(newTasks: List<Tarea>) {
        tasks.clear()
        tasks.addAll(newTasks)
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var itemImage: ImageView = itemView.findViewById(R.id.item_image)
        var itemTitle: TextView = itemView.findViewById(R.id.item_title)
        var itemDetail: TextView = itemView.findViewById(R.id.item_detail)
        val itemDetail2: TextView = itemView.findViewById(R.id.item_detail2)
        var itemCheck: CheckBox = itemView.findViewById(R.id.item_check)
    }
}
