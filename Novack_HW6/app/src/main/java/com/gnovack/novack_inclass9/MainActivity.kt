package com.gnovack.novack_inclass9

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.concurrent.Executors

//HW6
//MainActivity
//Gabriel Novack

class MainActivity : AppCompatActivity(), LoginFragment.LoginActions, SignupFragment.SignupActions, PostFragment.PostActions, CreatePostFragment.CreateActions {

    lateinit var auth: FirebaseAuth

    lateinit var mainDb:FirebaseFirestore

    val taskPool = Executors.newFixedThreadPool(2)

    public val dateFormat = SimpleDateFormat("MM-dd-yyyy hh:mm:ss");

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = Firebase.auth
        mainDb = Firebase.firestore

        if(auth.currentUser == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragmentContainerView, LoginFragment()).commit()
        } else {

            supportFragmentManager.beginTransaction()
                .add(R.id.fragmentContainerView, PostFragment.newInstance(auth.currentUser!!)).commit()
        }
    }

    override fun onLogin(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    val user = auth.currentUser
                    supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerView, PostFragment.newInstance(user!!))
                        .commit()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
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
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    user!!.updateProfile(UserProfileChangeRequest.Builder().setDisplayName(name).build())
                    supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerView, PostFragment.newInstance(user))
                        .commit()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    AlertDialog.Builder(this).setTitle(R.string.error)
                        .setMessage(getString(R.string.email_used)).show()
                }
            }
    }

    override fun onSignupCancel() {
        supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerView, LoginFragment()).commit()
    }

    override fun onLogOut() {
        auth.signOut()
        supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerView, LoginFragment()).commit()
    }

    override fun onCreatePost() {
        supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerView, CreatePostFragment())
            .addToBackStack(null).commit()
    }

    override fun onPostLiked(postId: String, curLiked: Boolean) {
        if(curLiked) {
            mainDb.collection("posts").document("$postId")
                .update("likes", FieldValue.arrayUnion(auth.currentUser!!.displayName!!))
        } else {
            mainDb.collection("posts").document("$postId").update("likes", FieldValue.arrayRemove(auth.currentUser!!.displayName!!))
        }
    }

    override fun onPostClicked(postId: String) {
        supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerView, CommentFragment.newInstance(postId, auth.currentUser!!)).addToBackStack(null).commit()
    }

    override fun onPostCreated(newPost: Forum) {
        newPost.createdBy = auth.currentUser!!.displayName!!
        mainDb.collection("posts").add(newPost)
        supportFragmentManager.popBackStack()
    }

    override fun onCreateCancel() {
        supportFragmentManager.popBackStack()
    }
}