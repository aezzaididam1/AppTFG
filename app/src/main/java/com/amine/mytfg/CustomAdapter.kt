package com.amine.mytfg

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.amine.mytfg.databases.Tarea

class CustomAdapter(
    private var tasks: MutableList<Tarea>,  // Cambiado a MutableList para facilitar las actualizaciones
    private val onItemChecked: (Int, Boolean) -> Unit
) : RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val task = tasks[position]
        holder.itemTitle.text = task.nombre
        holder.itemDetail.text = task.fechaIncial
        holder.itemDetail2.text = task.fechaFinal
        holder.itemImage.setImageResource(R.drawable.ic_lock)
        holder.itemCheck.setOnCheckedChangeListener(null)
        holder.itemCheck.isChecked = false
        holder.itemCheck.setOnCheckedChangeListener { _, isChecked ->
            onItemChecked(position, isChecked)
        }
    }

    override fun getItemCount(): Int = tasks.size

    // Método para actualizar los datos del adaptador y notificar al RecyclerView
    fun updateData(newTasks: List<Tarea>) {
        tasks.clear()
        tasks.addAll(newTasks)
        notifyDataSetChanged()  // Notificamos a RecyclerView de que los datos han cambiado
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var itemImage: ImageView = itemView.findViewById(R.id.item_image)
        var itemTitle: TextView = itemView.findViewById(R.id.item_title)
        var itemDetail: TextView = itemView.findViewById(R.id.item_detail)
        val itemDetail2: TextView = itemView .findViewById(R.id.item_detail2)
        var itemCheck: CheckBox = itemView.findViewById(R.id.item_check)
    }
}
