package com.gnovack.novack_inclass9

import android.app.AlertDialog
import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gnovack.novack_inclass9.databinding.FragmentCommentListBinding
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

/**
 * A fragment representing a list of Items.
 */

//HW6
//CommentFragment
//Gabriel Novack
class CommentFragment : Fragment(), CommentRecyclerViewAdapter.CommentClickListener {

    lateinit var binding: FragmentCommentListBinding
    lateinit var db: FirebaseFirestore
    lateinit var commentsRef: CollectionReference
    lateinit var curPost: DocumentSnapshot

    private var curPostId:String? = null
    private var curUser:FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            curPostId = it.getString(CUR_POST)
            curUser = it.getParcelable(CUR_USER)
        }
        db = Firebase.firestore

        commentsRef = db.collection("posts").document("$curPostId").collection("comments")
    }

    private fun updateComments(curComments: QuerySnapshot?) {
        val forumComments = mutableListOf<Comment>()
        for(comment in curComments!!){
            forumComments.add(
                Comment(
                    comment["createdBy"] as String,
                    comment["content"] as String,
                    comment["timeCreated"] as Timestamp,
                    curUser!!.displayName == comment["createdBy"],
                    comment.id
                )
            )
        }
        binding.commentNum.text = when(curComments.size()){
            1 -> binding.root.context.getString(R.string.comment_num, 1)
            else -> binding.root.context.getString(R.string.comment_num_pl, curComments.size())
        }

        with(binding.commentList){
            adapter = CommentRecyclerViewAdapter(forumComments, this@CommentFragment)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCommentListBinding.inflate(inflater, container, false)

        // Set the adapter
        with(binding.commentList) {
            layoutManager = LinearLayoutManager(context)
        }
        commentsRef.addSnapshotListener { value, error ->
            if (error != null) {
                Log.w(ContentValues.TAG, "Listen failed.", error)
                return@addSnapshotListener
            } else {
                updateComments(value)
            }
        }
        activity?.title = getString(R.string.forum)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db.collection("posts").document("$curPostId").get().addOnSuccessListener { result ->
            curPost = result
            binding.commentPostTitle.text = curPost["title"] as String
            binding.commentPostName.text = curPost["createdBy"] as String
            binding.commentPostContent.text = curPost["desc"] as String
        }
        commentsRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                updateComments(task.result)
            } else {
                Log.w("POSTFRAG", "ERROR", task.exception)
            }
        }

        binding.commentPost.setOnClickListener {
            if(binding.commentWrite.text.toString() == ""){
                AlertDialog.Builder(context).setTitle(getString(R.string.error)).setMessage(getString(R.string.empty_error)).show()
            } else {
                commentsRef.add(
                    Comment(
                        curUser!!.displayName!!,
                        binding.commentWrite.text.toString(),
                        Timestamp.now()
                    )
                )
                binding.commentWrite.setText("")
            }
        }
    }

    companion object {

        // TODO: Customize parameter argument names
        const val CUR_POST = "cur-post"
        const val CUR_USER = "cur-user"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(curPostId: String, curUser:FirebaseUser) =
            CommentFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(CUR_POST, curPostId)
                    putParcelable(CUR_USER, curUser)
                }
            }
    }

    override fun onDelete(commentId: String) {
        commentsRef.document("$commentId").delete()
    }
}