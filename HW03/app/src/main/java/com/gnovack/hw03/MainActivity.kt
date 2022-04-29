package com.gnovack.hw03

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import java.text.SimpleDateFormat

//HW02
//MainActivity
//Gabriel Novack

class MainActivity : AppCompatActivity() {
    val TASK_KEY = "TASK"
    val INDEX_KEY = "INDEX"
    val tasks:ObservableArrayList<Tasks> = ObservableArrayList()

    val simpleDateFormat = SimpleDateFormat("MM/dd/yyyy")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tasks.addOnListChangedCallback(ListChangedListener())


        val taskNum = findViewById<TextView>(R.id.task_num)

        val upcomingTask = findViewById<View>(R.id.upcoming_task)
        val taskTitle = findViewById<TextView>(R.id.task_title)
        val taskDate = findViewById<TextView>(R.id.task_date)
        val taskPriority = findViewById<TextView>(R.id.task_priority)
        val viewTasks = findViewById<Button>(R.id.view_tasks)
        val createTask = findViewById<Button>(R.id.create_task)

        fun updateTasks() {
            taskNum.text = getString(R.string.num_tasks, tasks.size)
            if (tasks.size == 0) {
                taskTitle.text = getString(R.string.none)
                taskDate.text = ""
                taskPriority.text = ""
            } else {
                val newestTask = tasks[0]
                taskTitle.text = newestTask.name
                taskDate.text = simpleDateFormat.format(newestTask.date)
                taskPriority.text = newestTask.priority
            }
        }

        val startForResult:ActivityResultLauncher<Intent> = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), ActivityResultCallback {result ->
            if (result != null && result.resultCode == RESULT_OK) {
                val index = result.data?.getIntExtra(DisplayTask().DELETE_KEY, 0) ?: 0
                tasks.removeAt(index)
                updateTasks()
            } else if (result != null && result.resultCode == CreateTask().NEW_TASK){
                val newTask:Tasks = result.data!!.getSerializableExtra(CreateTask().NEW_TASK_KEY) as Tasks
                tasks.add(newTask)
                updateTasks()
            }
        })

        updateTasks()

        upcomingTask.setOnClickListener {
            val upcomingIntent = Intent(this, DisplayTask::class.java)
            try {
                upcomingIntent.putExtra(TASK_KEY, tasks[0])
                startForResult.launch(upcomingIntent)
            } catch (e:IndexOutOfBoundsException){
                Toast.makeText(this, getString(R.string.none_available), Toast.LENGTH_SHORT).show()
            }
        }
        viewTasks.setOnClickListener {
            if (tasks.size == 0){
                Toast.makeText(this, getString(R.string.none_available), Toast.LENGTH_SHORT).show()
            }else{
                val alertDialog: AlertDialog? = let {
                    val builder = AlertDialog.Builder(it)
                    builder.setTitle("Select Task")
                        .setItems(tasks.map { it.name }.toTypedArray(), DialogInterface.OnClickListener {dialog, which ->
                            val taskIntent = Intent(this, DisplayTask::class.java)
                            taskIntent.putExtra(TASK_KEY, tasks[which])
                            taskIntent.putExtra(INDEX_KEY, which)
                            startForResult.launch(taskIntent)
                        })
                        .setNegativeButton(R.string.cancel, DialogInterface.OnClickListener {_, _ ->})
                    builder.create()
                }
                alertDialog?.show()
            }
        }
        createTask.setOnClickListener {
            val createIntent = Intent(this, CreateTask::class.java)
            startForResult.launch(createIntent)
        }
    }
}


class ListChangedListener : ObservableList.OnListChangedCallback<ObservableArrayList<Tasks>>() {

    override fun onItemRangeInserted(sender: ObservableArrayList<Tasks>, positionStart: Int, itemCount: Int) {
        sender.sortBy { it.date }
    }

    override fun onChanged(sender: ObservableArrayList<Tasks>?) {}

    override fun onItemRangeChanged(
        sender: ObservableArrayList<Tasks>?,
        positionStart: Int,
        itemCount: Int
    ) {}

    override fun onItemRangeMoved(
        sender: ObservableArrayList<Tasks>?,
        fromPosition: Int,
        toPosition: Int,
        itemCount: Int
    ) {}

    override fun onItemRangeRemoved(
        sender: ObservableArrayList<Tasks>?,
        positionStart: Int,
        itemCount: Int
    ) { }

}
