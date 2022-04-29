package com.gnovack.hw03

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

//HW02
//DisplayTask
//Gabriel Novack

class DisplayTask : AppCompatActivity() {
    val DELETE_KEY = "DELETE"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_task)

        val showName = findViewById<TextView>(R.id.show_name)
        val showDate = findViewById<TextView>(R.id.show_date)
        val showPriority = findViewById<TextView>(R.id.show_priority)
        val cancelButton = findViewById<Button>(R.id.display_cancel)
        val deleteButton = findViewById<Button>(R.id.display_delete)

        val curTask = intent.getSerializableExtra(MainActivity().TASK_KEY) as Tasks
        val curIndex:Int = intent.getIntExtra(MainActivity().INDEX_KEY, 0)

        showName.text = curTask.name
        showDate.text = MainActivity().simpleDateFormat.format(curTask.date)
        showPriority.text = curTask.priority

        cancelButton.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }
        deleteButton.setOnClickListener {
            val returnIntent = Intent()
            returnIntent.putExtra(DELETE_KEY, curIndex)
            setResult(RESULT_OK, returnIntent)
            finish()
        }
    }
}