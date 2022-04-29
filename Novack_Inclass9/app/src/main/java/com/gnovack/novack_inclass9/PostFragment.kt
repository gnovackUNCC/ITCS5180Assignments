package com.gnovack.novack_inclass9

import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.gnovack.novack_inclass9.databinding.FragmentPostListBinding
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

//HW5
//PostFragment
//Gabriel Novack
class PostFragment : Fragment(), PostRecyclerViewAdapter.DeleteClickListener {

    lateinit var binding:FragmentPostListBinding
    lateinit var db:FirebaseFirestore
    lateinit var colRef:CollectionReference
    lateinit var postActions: PostActions


    private var curUser:FirebaseUser? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            curUser = it.getParcelable(USER_KEY)
        }

        db = Firebase.firestore
        colRef = db.collection("posts")
        colRef.addSnapshotListener { value, error ->
            if (error != null) {
                Log.w(TAG, "Listen failed.", error)
                return@addSnapshotListener
            }
            updateList(value)

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPostListBinding.inflate(inflater, container, false)

        colRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                updateList(task.result)
            } else {
                Log.w("POSTFRAG", "ERROR", task.exception)
            }
        }

        // Set the adapter
        with(binding.postList) {
            layoutManager =  LinearLayoutManager(context)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.logoutButton.setOnClickListener {
            postActions.onLogOut()
        }
        binding.createPostButton.setOnClickListener {
            postActions.onCreatePost()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        postActions = context as PostActions
    }

    fun updateList(curCol:QuerySnapshot?) {
        if(curCol != null) {
            val forumList = mutableListOf<Forum>()
            for (forum in curCol) {
                forumList.add(
                    Forum(
                        forum["createdBy"] as String,
                        forum["title"] as String,
                        forum["desc"] as String,
                        (forum["timeCreated"] as Timestamp),
                        forum.id,
                        curUser!!.displayName == forum["createdBy"]
                    )
                )
            }
            with(binding.postList) {
                adapter = PostRecyclerViewAdapter(forumList, this@PostFragment)
            }
        }
    }

    companion object {

        // TODO: Customize parameter argument names
        const val USER_KEY = "user-key"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(user: FirebaseUser) =
            PostFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(USER_KEY, user)
                }
            }
    }

    override fun onDelete(postId: String) {
        colRef.document("$postId")
            .delete()
    }

    interface PostActions {
        fun onLogOut()
        fun onCreatePost()
    }
}