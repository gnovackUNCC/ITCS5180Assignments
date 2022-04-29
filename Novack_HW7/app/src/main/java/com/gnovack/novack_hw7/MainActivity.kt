package com.gnovack.novack_hw7

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity(), LoginFragment.LoginActions, SignupFragment.SignupActions, ListOverviewFragment.OverviewActions, ShoppingItemFragment.ShoppingListActions {

    lateinit var auth: FirebaseAuth

    lateinit var mainDb: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = Firebase.auth
        mainDb = Firebase.firestore
        if(auth.currentUser == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragmentContainerView, LoginFragment()).commit()
        } else {
            mainDb.collection("users").document(auth.currentUser!!.uid).get().addOnSuccessListener { result ->
                if(result.data == null)
                    mainDb.collection("users").document(auth.currentUser!!.uid).set(User(auth.currentUser!!.uid, auth.currentUser!!.displayName!!))
            }
            supportFragmentManager.beginTransaction()
                .add(R.id.fragmentContainerView, ListOverviewFragment.newInstance(auth.currentUser!!)).commit()
        }
    }

    override fun onLogin(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(ContentValues.TAG, "signInWithEmail:success")
                    val user = auth.currentUser
                    mainDb.collection("users").document(user!!.uid).get().addOnSuccessListener { result ->
                        if(result.data == null)
                            mainDb.collection("users").document(user.uid).set(User(user.uid, user.displayName!!))
                    }
                    supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerView, ListOverviewFragment.newInstance(user!!))
                        .commit()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(ContentValues.TAG, "signInWithEmail:failure", task.exception)
                    AlertDialog.Builder(this).setTitle(R.string.error)
                        .setMessage(getString(R.string.incorrect)).show()
                }
            }
    }

    override fun onCreateAccount() {
        supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerView, SignupFragment()).commit()
    }

    override fun onSignup(name: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(ContentValues.TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    user!!.updateProfile(UserProfileChangeRequest.Builder().setDisplayName(name).build())
                    mainDb.collection("users").document(user.uid).set(User(user.uid, name))
                    supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerView, ListOverviewFragment.newInstance(user))
                        .commit()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(ContentValues.TAG, "createUserWithEmail:failure", task.exception)
                    AlertDialog.Builder(this).setTitle(R.string.error)
                        .setMessage(getString(R.string.email_used)).show()
                }
            }
    }

    override fun onSignupCancel() {
        supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerView, LoginFragment()).commit()
    }

    override fun onSignOut() {
        auth.signOut()
        supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerView, LoginFragment()).commit()
    }

    override fun onListSelected(listId: String) {
        supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerView, ShoppingItemFragment.newInstance(listId, auth.currentUser!!)).addToBackStack(null).commit()
    }

    override fun leaveList(curUser: FirebaseUser) {
        supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerView, ListOverviewFragment.newInstance(curUser)).commit()
    }
}