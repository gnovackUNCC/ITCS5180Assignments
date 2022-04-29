package com.gnovack.group30_inclass05

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

//Gabriel Novack
//MainActivity
//In Class 05

class MainActivity : AppCompatActivity(), CategoryFragment.CategoryActions, AppFragment.AppActions {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction().add(R.id.fragmentContainerView, CategoryFragment.newInstance(1)).commit()
    }

    override fun categoryClick(categorySelected: String) {
        supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerView, AppFragment.newInstance(1, categorySelected))
            .addToBackStack(null).commit()
    }

    override fun appClick(appSelected: DataServices.App?) {
        supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerView, GenreFragment.newInstance(1, appSelected!!))
            .addToBackStack(null).commit()
    }
}