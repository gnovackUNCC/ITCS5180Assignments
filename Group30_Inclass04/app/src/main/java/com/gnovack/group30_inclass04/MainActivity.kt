package com.gnovack.group30_inclass04

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.gnovack.group30_inclass04.databinding.ActivityMainBinding
/*
In Class 04
MainActivity
Gabriel Novack
*/

class MainActivity : AppCompatActivity(), LoginFragment.OnLoginActions, AccountFragment.OnAccountActions, RegisterFragment.OnRegistration, UpdateFragment.UpdateActions {

    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction().add(R.id.fragmentContainerView, LoginFragment())
            .commit()
    }

    override fun onLogin(email: String, pass: String) {
        val logAttempt = DataServices.login(email, pass)
        if(!logAttempt.isSuccessful){
            Toast.makeText(this, logAttempt.errorMessage, Toast.LENGTH_SHORT).show()
        } else{
            supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerView, AccountFragment.newInstance(logAttempt.account!!))
                .commit()
        }
    }

    override fun onCreateAccount() {
        supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerView, RegisterFragment())
            .commit()
    }

    override fun onLogOut() {
        supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerView, LoginFragment())
            .commit()
    }

    override fun onEditAccount(curAccount: DataServices.Account) {
        supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerView, UpdateFragment.newInstance(curAccount))
            .addToBackStack(null).commit()
    }

    override fun onRegister(newAccountAttempt: DataServices.AccountRequestTask) {
        if(newAccountAttempt.isSuccessful){
            supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerView, AccountFragment.newInstance(newAccountAttempt.account!!))
                .commit()
        } else {
            Toast.makeText(this, newAccountAttempt.errorMessage, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onUpdate(updateAttempt: DataServices.AccountRequestTask) {
        if(updateAttempt.isSuccessful){
            supportFragmentManager.popBackStack()
            supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerView, AccountFragment.newInstance(updateAttempt.account!!))
                .commit()
        } else {
            Toast.makeText(this, updateAttempt.errorMessage, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onUpdateCancel() {
        supportFragmentManager.popBackStack()
    }

    override fun onCancel() {
        supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerView, LoginFragment())
            .commit()
    }


}