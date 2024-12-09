package br.edu.up.testefinal

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class TaskAdapter(
    private val tasks: MutableList<Task>,
    private val onDelete: (String) -> Unit,
    private val onEdit: (String, String) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.bind(task, onDelete, onEdit)
    }

    override fun getItemCount(): Int = tasks.size

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTaskName: TextView = itemView.findViewById(R.id.tvTaskName)
        private val btnEdit: Button = itemView.findViewById(R.id.btnEdit)
        private val btnDelete: Button = itemView.findViewById(R.id.btnDelete)

        fun bind(task: Task, onDelete: (String) -> Unit, onEdit: (String, String) -> Unit) {
            tvTaskName.text = task.name

            btnEdit.setOnClickListener {
                val context = itemView.context
                val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_task, null)
                val etEditTask = dialogView.findViewById<EditText>(R.id.etEditTask)
                etEditTask.setText(task.name)

                AlertDialog.Builder(context)
                    .setTitle("Edit Task")
                    .setView(dialogView)
                    .setPositiveButton("Update") { _, _ ->
                        val newTaskName = etEditTask.text.toString()
                        if (newTaskName.isNotEmpty()) {
                            onEdit(task.id, newTaskName)
                        } else {
                            Toast.makeText(context, "Task name cannot be empty", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }

            btnDelete.setOnClickListener {
                onDelete(task.id)
            }
        }
    }
}

