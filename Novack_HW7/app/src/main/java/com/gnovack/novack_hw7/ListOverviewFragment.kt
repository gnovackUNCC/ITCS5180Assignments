package com.gnovack.novack_hw7

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.core.view.marginLeft
import com.gnovack.novack_hw7.databinding.FragmentSummaryListBinding
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

/**
 * A fragment representing a list of Items.
 */
class ListOverviewFragment : Fragment(), ListOverviewRecyclerViewAdapter.ListClickListener {

    lateinit var binding:FragmentSummaryListBinding
    lateinit var db: FirebaseFirestore
    lateinit var curUserRef:DocumentReference
    lateinit var listRef:CollectionReference
    lateinit var overviewActions: OverviewActions

    private var curUser:FirebaseUser? = null

    lateinit var curUserObject:User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            curUser = it.getParcelable(USER_KEY)
        }

        db = Firebase.firestore
        curUserRef = db.collection("users").document(curUser!!.uid)
        listRef = db.collection("lists")

        curUserRef.addSnapshotListener { value, error ->
            if (error != null) {
                Log.w(ContentValues.TAG, "Listen failed.", error)
                return@addSnapshotListener
            }
            updateList(value)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSummaryListBinding.inflate(inflater, container, false)

        curUserRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                updateList(task.result)
            } else {
                Log.w("POSTFRAG", "ERROR", task.exception)
            }
        }
        // Set the adapter
        with(binding.summaryList) {
            layoutManager =  LinearLayoutManager(context)
        }
        activity?.title = getString(R.string.list_overview)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.createList.setOnClickListener {
            val listNameEdit = EditText(activity)
            listNameEdit.hint = getString(R.string.new_list_name)
            listNameEdit.setPadding(listNameEdit.paddingLeft + 32, listNameEdit.paddingBottom, listNameEdit.paddingRight + 32, listNameEdit.paddingBottom)
            val dialog = AlertDialog.Builder(activity).setView(listNameEdit)
                .setTitle(getString(R.string.create_shopping_list))
                .setNegativeButton(getString(R.string.cancel)) { dialogInterface, i ->
                    dialogInterface.cancel()
                }
                .setPositiveButton(getString(R.string.submit), null)
                .create()
            dialog.setOnShowListener {
                val posButton = (it as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)
                posButton.setOnClickListener {
                    if(listNameEdit.text.toString() == ""){
                        Toast.makeText(context, R.string.empty_error, Toast.LENGTH_SHORT).show()
                    } else {
                        listRef.add(ShoppingList(null, listNameEdit.text.toString(), curUserObject.name)).addOnSuccessListener { list ->
                            curUserRef.update("ownedLists", FieldValue.arrayUnion(list.id))
                        }
                        dialog.dismiss()
                    }
                }
            }
            dialog.show()
        }

        binding.logoutButton.setOnClickListener {
            overviewActions.onSignOut()
        }

        binding.friendsListButton.setOnClickListener {
            val arrayAdapter = ArrayAdapter<String>(
                requireContext(),
                android.R.layout.select_dialog_item
            )
            val allFriendsId = mutableListOf<String>()
            listRef.get().addOnSuccessListener { result ->
                for(list in result){
                    if(list.id in curUserObject.ownedLists!!){
                        allFriendsId.addAll(list["sharedWith"] as List<String>)
                    }
                }
                for(id in allFriendsId){
                    db.collection("users").document(id).get().addOnSuccessListener {
                        arrayAdapter.add(it["name"] as String)
                    }
                }
                AlertDialog.Builder(activity).setTitle(getString(R.string.friends_list))
                    .setAdapter(arrayAdapter) { _, _ -> }
                    .show()
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        overviewActions = context as OverviewActions
    }

    fun updateList(curUser: DocumentSnapshot?) {
        if(curUser != null) {
            curUserObject = curUser.toObject<User>()!!
            val listsArr = mutableListOf<ShoppingList>()
            listRef.get().addOnSuccessListener { result ->
                for(list in result){
                    if(curUserObject.ownedLists!!.contains(list.id)){
                        val curList = list.toObject<ShoppingList>()
                        curList.createdByCurrent = true
                        curList.id = list.id
                        listsArr.add(curList)
                    }
                    else if(curUserObject.sharedLists!!.contains(list.id)){
                        val curList = list.toObject<ShoppingList>()
                        curList.id = list.id
                        listsArr.add(curList)
                    }
                }
                with(binding.summaryList) {
                    adapter = ListOverviewRecyclerViewAdapter(listsArr, this@ListOverviewFragment)
                }
            }
            binding.userName.text = curUserObject.name
        }
    }

    companion object {

        // TODO: Customize parameter argument names
        const val USER_KEY = "user-key"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(user: FirebaseUser) =
            ListOverviewFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(USER_KEY, user)
                }
            }
    }

    override fun onDelete(listId: String) {
        listRef.document(listId).get().addOnSuccessListener { result ->
            val curList = result.toObject<ShoppingList>()
            for(user in curList!!.sharedWith){
                db.collection("users").document(user).update("sharedLists", FieldValue.arrayRemove(listId))
            }
        }
        listRef.document(listId).delete()
        curUserRef.update("ownedLists", FieldValue.arrayRemove(listId))
    }

    override fun onListSelected(listId: String) {
        overviewActions.onListSelected(listId)
    }

    interface OverviewActions {
        fun onSignOut()
        fun onListSelected(listId: String)
    }
}