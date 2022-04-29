package com.gnovack.group30_hw3

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import java.text.SimpleDateFormat

//HW03
//MainActivity
//Gabriel Novack

class MainActivity : AppCompatActivity(), TodoListFragment.ListActions, DisplayTaskFragment.DisplayActions, CreateTaskFragment.CreateActions {

    val tasks:ObservableArrayList<Task> = ObservableArrayList()

    val simpleDateFormat = SimpleDateFormat("MM/dd/yyyy")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tasks.addOnListChangedCallback(ListChangedListener())

        supportFragmentManager.beginTransaction().add(R.id.fragmentContainerView, TodoListFragment.newInstance(tasks))
            .commit()
    }

    override fun onCreateTask() {
        supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerView, CreateTaskFragment())
            .addToBackStack("todo").commit()
    }

    override fun onViewTask(task: Task) {
        supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerView, DisplayTaskFragment.newInstance(task))
            .addToBackStack("todo").commit()
    }

    override fun onUpdate(todoListFragment: TodoListFragment) {
        todoListFragment.updateList(tasks)
    }

    override fun onDelete(task: Task) {
        tasks.remove(task)
        supportFragmentManager.popBackStack()
    }

    override fun onCancel() {
        supportFragmentManager.popBackStack()
    }

    override fun onTaskSubmitted(task: Task) {
        tasks.add(task)
        supportFragmentManager.popBackStack()
    }

    override fun onCreateCancel() {
        supportFragmentManager.popBackStack()
    }
}

class ListChangedListener : ObservableList.OnListChangedCallback<ObservableArrayList<Task>>() {

    override fun onItemRangeInserted(sender: ObservableArrayList<Task>, positionStart: Int, itemCount: Int) {
        sender.sortBy { it.date }
    }

    override fun onChanged(sender: ObservableArrayList<Task>?) {}

    override fun onItemRangeChanged(
        sender: ObservableArrayList<Task>?,
        positionStart: Int,
        itemCount: Int
    ) {}

    override fun onItemRangeMoved(
        sender: ObservableArrayList<Task>?,
        fromPosition: Int,
        toPosition: Int,
        itemCount: Int
    ) {}

    override fun onItemRangeRemoved(
        sender: ObservableArrayList<Task>?,
        positionStart: Int,
        itemCount: Int
    ) { }

}
