package br.edu.up.testefinal

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore
    private lateinit var etTask: EditText
    private lateinit var btnAdd: Button
    private lateinit var rvTasks: RecyclerView
    private lateinit var taskAdapter: TaskAdapter
    private val taskList = mutableListOf<Task>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializar Firestore
        db = FirebaseFirestore.getInstance()

        // Inicializar Views
        etTask = findViewById(R.id.etTask)
        btnAdd = findViewById(R.id.btnAdd)
        rvTasks = findViewById(R.id.rvTasks)

        // Configurar RecyclerView
        taskAdapter = TaskAdapter(taskList, ::deleteTask, ::editTask)
        rvTasks.layoutManager = LinearLayoutManager(this)
        rvTasks.adapter = taskAdapter

        // Botão para adicionar tarefas
        btnAdd.setOnClickListener {
            val taskName = etTask.text.toString()
            if (taskName.isNotEmpty()) {
                addTask(taskName)
            } else {
                Toast.makeText(this, "Please enter a task", Toast.LENGTH_SHORT).show()
            }
        }

        // Listener em tempo real
        observeTasks()
    }

    private fun addTask(taskName: String) {
        val taskData = hashMapOf("name" to taskName)
        db.collection("tasks").add(taskData).addOnSuccessListener {
            Toast.makeText(this, "Task added", Toast.LENGTH_SHORT).show()
            etTask.text.clear()
        }.addOnFailureListener {
            Toast.makeText(this, "Error adding task", Toast.LENGTH_SHORT).show()
        }
    }

    private fun observeTasks() {
        db.collection("tasks").addSnapshotListener { snapshots, error ->
            if (error != null) {
                Toast.makeText(this, "Error fetching tasks", Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }

            if (snapshots != null) {
                val newTaskList = mutableListOf<Task>()
                for (document in snapshots) {
                    val task = Task(
                        id = document.id,
                        name = document.getString("name") ?: ""
                    )
                    newTaskList.add(task)
                }

                updateRecyclerView(newTaskList)
            }
        }
    }

    private fun updateRecyclerView(newTaskList: List<Task>) {
        taskList.clear()
        taskList.addAll(newTaskList)
        taskAdapter.notifyDataSetChanged()
    }

    private fun deleteTask(taskId: String) {
        db.collection("tasks").document(taskId).delete().addOnSuccessListener {
            Toast.makeText(this, "Task deleted", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(this, "Error deleting task", Toast.LENGTH_SHORT).show()
        }
    }

    private fun editTask(taskId: String, newTaskName: String) {
        db.collection("tasks").document(taskId).update("name", newTaskName).addOnSuccessListener {
            Toast.makeText(this, "Task updated", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(this, "Error updating task", Toast.LENGTH_SHORT).show()
        }
    }
}