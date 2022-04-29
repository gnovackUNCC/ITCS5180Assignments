package com.gnovack.novack_hw5

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject

//HW5
//MainActivity
//Gabriel Novack
class MainActivity : AppCompatActivity(), LoginFragment.LoginActions, PostFragment.PostActions, SignupFragment.SignUpActions, CreatePostFragment.CreateActions {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val sharedPref = this.getPreferences(Context.MODE_PRIVATE) ?: return
        val userString = sharedPref.getString(getString(R.string.login_key_access), "")

        if(userString == "") {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragmentContainerView, LoginFragment()).commit()
        }else {
            supportFragmentManager.beginTransaction().add(R.id.fragmentContainerView, PostFragment.newInstance(userString!!)).commit()
        }
    }

    override fun onSignupClicked() {
        supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerView, SignupFragment()).commit()
    }

    override fun onSuccess(res: String) {
        val sharedPref = this.getPreferences(Context.MODE_PRIVATE) ?: return
        with (sharedPref.edit()){
            putString(getString(R.string.login_key_access), res)
            apply()
        }
        supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerView, PostFragment.newInstance(res))
            .addToBackStack(null).commit()
    }

    override fun onFailure(res: JSONObject) {
        AlertDialog.Builder(this).setTitle(getString(R.string.error)).setMessage(res.getString("message")).show()
    }

    override fun onLogOut() {
        val sharedPref = this.getPreferences(Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()){
            remove(getString(R.string.login_key_access))
            apply()
        }
        supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerView, LoginFragment()).commit()
    }

    override fun onCreatePost(userJson: String) {
        supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerView, CreatePostFragment.newInstance(userJson)).addToBackStack(null).commit()
    }

    override fun onSubmit(res: String) {
        val sharedPref = this.getPreferences(Context.MODE_PRIVATE) ?: return
        with (sharedPref.edit()){
            putString(getString(R.string.login_key_access), res)
            apply()
        }
        supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerView, PostFragment.newInstance(res)).commit()
    }

    override fun onCancel() {
        supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerView, LoginFragment()).commit()
    }

    override fun onPostCreate() {
        supportFragmentManager.popBackStack()
    }

    override fun onPostCancel() {
        supportFragmentManager.popBackStack()
    }
}