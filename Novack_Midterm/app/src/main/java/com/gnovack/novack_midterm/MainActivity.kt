package com.gnovack.novack_midterm

import android.os.Bundle
import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity(), ExpenseFragment.ExpenseActions, AddExpenseFragment.AddActions, CategoryFragment.CategoryActions {
    private val expenses = arrayListOf<Expense>()

    val simpleDateFormat = SimpleDateFormat("MM/dd/yyyy")

    private var selectedCategory:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction().add(R.id.fragmentContainerView, ExpenseFragment.newInstance(expenses)).commit()
    }

    override fun addExpense() {
        selectedCategory = null
        supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerView, AddExpenseFragment())
            .addToBackStack(null).commit()
    }

    override fun expTrashed(fragment: ExpenseFragment, position: Int) {
        expenses.removeAt(position)
        fragment.updateList(expenses)
    }

    override fun updateExpenses(fragment: ExpenseFragment) {
        fragment.updateList(expenses)
    }

    override fun viewSummary() {
        supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerView, MonthFragment.newInstance(expenses)).addToBackStack(null).commit()
    }

    override fun addSubmitted(newExpense: Expense) {
        expenses.add(newExpense)
        expenses.sortBy {
            it.date
        }
        expenses.reverse()
        supportFragmentManager.popBackStack()
    }

    override fun addCanceled() {
        supportFragmentManager.popBackStack()
    }

    override fun selectCat() {
        supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerView, CategoryFragment()).addToBackStack(null).commit()
    }

    override fun catUpdated(fragment: AddExpenseFragment) {
        fragment.updateCategory(selectedCategory ?:resources.getString(R.string.none))
    }

    override fun categorySelected(category: String) {
        selectedCategory = category
        supportFragmentManager.popBackStack()
    }
}

@Parcelize
data class Expense(val name:String, val amount:Double, val date: Date, val category:String): Parcelable