package com.gnovack.hw03

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*

//HW02
//CreateTask
//Gabriel Novack

class CreateTask : AppCompatActivity() {
    val NEW_TASK = 42
    val NEW_TASK_KEY = "NEW_TASK"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_task)

        val editName = findViewById<EditText>(R.id.task_edit_name)
        val editDate = findViewById<TextView>(R.id.show_set_date)
        val priorityGroup = findViewById<RadioGroup>(R.id.priority_group)
        val cancelButton = findViewById<Button>(R.id.cancel_button)
        val submitButton = findViewById<Button>(R.id.submit_button)
        val dateButton = findViewById<Button>(R.id.set_date)

        dateButton.setOnClickListener {
            val dateDialog:DatePickerDialog = DatePickerDialog(this)
            dateDialog.setOnDateSetListener{ datePicker: DatePicker, y: Int, m: Int, d: Int ->
                editDate.text = "${m+1}/$d/$y"
            }
            dateDialog.show()
        }

        cancelButton.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }

        submitButton.setOnClickListener {
            if(editName.text.isEmpty() || editDate.text.isEmpty()){
                Toast.makeText(this, getString(R.string.empty_fields), Toast.LENGTH_SHORT).show()
            }
            else {
                val checkedButton = findViewById<RadioButton>(priorityGroup.checkedRadioButtonId)
                val checkedPriority = checkedButton.text.toString()
                val newTask = Tasks(
                    editName.text.toString(),
                    MainActivity().simpleDateFormat.parse(editDate.text.toString())!!,
                    checkedPriority
                )
                val returnIntent = Intent()
                returnIntent.putExtra(NEW_TASK_KEY, newTask)
                setResult(NEW_TASK, returnIntent)
                finish()
            }
        }
    }
}