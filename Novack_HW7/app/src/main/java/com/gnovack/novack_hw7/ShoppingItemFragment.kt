package com.gnovack.novack_hw7

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.gnovack.novack_hw7.databinding.FragmentShoppingListBinding
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

/**
 * A fragment representing a list of Items.
 */
class ShoppingItemFragment : Fragment(), ShoppingItemRecyclerViewAdapter.ItemClickListener {
    lateinit var binding:FragmentShoppingListBinding
    lateinit var db: FirebaseFirestore
    lateinit var curUserRef: DocumentReference
    lateinit var curListRef: DocumentReference
    lateinit var shoppingListActions: ShoppingListActions

    private var curUser: FirebaseUser? = null

    private var curListId:String? = null

    private var isOwner = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            curListId = it.getString(SHOP_LIST)
            curUser = it.getParcelable(CUR_USER)
        }

        db = Firebase.firestore
        curUserRef = db.collection("users").document(curUser!!.uid)
        curListRef = db.collection("lists").document(curListId!!)

        curListRef.addSnapshotListener { value, error ->
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
        binding = FragmentShoppingListBinding.inflate(inflater, container, false)

        curListRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                updateList(task.result)
            } else {
                Log.w("POSTFRAG", "ERROR", task.exception)
            }
        }
        // Set the adapter
        with(binding.shoppingList) {
            layoutManager =  LinearLayoutManager(context)
        }
        activity?.title = getString(R.string.shopping_list)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.addItem.setOnClickListener {
            val addItemLayout = LinearLayout(activity)
            addItemLayout.orientation = LinearLayout.VERTICAL
            val listNameEdit = EditText(activity)
            listNameEdit.hint = "New Item"
            val listCostEdit = EditText(activity)
            listCostEdit.hint = "Item Cost"
            listCostEdit.inputType = InputType.TYPE_CLASS_NUMBER
            listCostEdit.inputType += InputType.TYPE_NUMBER_FLAG_DECIMAL
            addItemLayout.addView(listNameEdit)
            addItemLayout.addView(listCostEdit)

            val dialog = AlertDialog.Builder(activity).setView(addItemLayout)
                .setTitle(getString(R.string.add_item_list))
                .setNegativeButton(getString(R.string.cancel)) { dialogInterface, i ->
                    dialogInterface.cancel()
                }
                .setPositiveButton(getString(R.string.submit), null)
                .create()

            dialog.setOnShowListener {
                val posButton = (it as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)
                posButton.setOnClickListener {
                    if(listNameEdit.text.toString() == "" || listCostEdit.text.toString() == ""){
                        Toast.makeText(context, R.string.empty_error, Toast.LENGTH_SHORT).show()
                    } else {
                        curListRef.update(
                            "items",
                            FieldValue.arrayUnion(
                                ShoppingItem(
                                    listNameEdit.text.toString(),
                                    getString(
                                        R.string.item_cost,
                                        listCostEdit.text.toString().toFloat()
                                    )
                                )
                            )
                        )
                        dialog.dismiss()
                    }
                }
            }
            dialog.show()
        }

        binding.userInvite.setOnClickListener {
            val arrayAdapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_selectable_list_item)
            val usersId = mutableListOf<String>()
            db.collection("users").get().addOnSuccessListener { result ->
                for(user in result){
                    if(user["name"] as String != curUser!!.displayName) {
                        curListRef.get().addOnSuccessListener { listRes ->
                            if(user.id !in listRes["sharedWith"] as List<String>){
                                usersId.add(user.id)
                                arrayAdapter.add(user["name"] as String)
                            }
                        }
                    }
                }
                AlertDialog.Builder(activity).setNegativeButton(R.string.cancel, DialogInterface.OnClickListener { dialogInterface, i ->
                    dialogInterface.dismiss()
                })
                    .setTitle(getString(R.string.invite_user_list))
                    .setAdapter(arrayAdapter) { dialogInterface, i ->
                        db.collection("users").document(usersId[i])
                            .update("sharedLists", FieldValue.arrayUnion(curListRef.id))
                        curListRef.update("sharedWith", FieldValue.arrayUnion(usersId[i]))
                    }.show()
            }
        }

        binding.removeUser.setOnClickListener {
            if(isOwner) {
                val arrayAdapter = ArrayAdapter<String>(
                    requireContext(),
                    android.R.layout.simple_selectable_list_item
                )
                val sharedWithUsers = mutableListOf<Pair<String, String>>()
                curListRef.get().addOnSuccessListener { result ->
                    val list = result.toObject<ShoppingList>()
                    for (user in list!!.sharedWith) {
                        db.collection("users").document(user).get()
                            .addOnSuccessListener { userRes ->
                                arrayAdapter.add(userRes["name"] as String)
                                sharedWithUsers.add(Pair(userRes["name"] as String, userRes.id))
                            }
                    }
                    AlertDialog.Builder(activity).setNegativeButton(
                        R.string.cancel,
                        DialogInterface.OnClickListener { dialogInterface, i ->
                            dialogInterface.dismiss()
                        })
                        .setTitle(getString(R.string.remove_user_list))
                        .setAdapter(arrayAdapter) { dialogInterface, i ->
                            db.collection("users").document(sharedWithUsers[i].second)
                                .update("sharedLists", FieldValue.arrayRemove(curListRef.id))
                            curListRef.update(
                                "sharedWith",
                                FieldValue.arrayRemove(sharedWithUsers[i].second)
                            )
                        }.show()
                }
            } else {
                curUserRef.update("sharedLists", FieldValue.arrayRemove(curListRef.id))
                curListRef.update("sharedWith", FieldValue.arrayRemove(curUserRef.id))
                shoppingListActions.leaveList(curUser!!)
            }
        }

        curListRef.get().addOnSuccessListener { result ->
            if(result["owner"] as String != curUser!!.displayName){
                isOwner = false
                binding.removeUser.text = "Leave List"
                binding.userInvite.visibility = View.GONE
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        shoppingListActions = context as ShoppingListActions
    }

    fun updateList(curShopListSnap: DocumentSnapshot?){
        val curShopList = curShopListSnap!!.toObject<ShoppingList>()
        var totalSum = 0.0
        if (curShopList != null) {
            for (item in curShopList.items!!) {
                totalSum += item.cost.substring(1).toDouble()
            }

            with(binding.shoppingList) {
                adapter = ShoppingItemRecyclerViewAdapter(
                    curShopList.items, this@ShoppingItemFragment
                )
            }
            if (context != null) {
                binding.listTitle.text = curShopList.name
                binding.totalValue.text = getString(R.string.item_cost, totalSum)
            }
        }
    }

    companion object {

        // TODO: Customize parameter argument names
        const val SHOP_LIST = "shop-list"
        const val CUR_USER = "cur-user"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(listId:String, curUser: FirebaseUser) =
            ShoppingItemFragment().apply {
                arguments = Bundle().apply {
                    putString(SHOP_LIST, listId)
                    putParcelable(CUR_USER, curUser)
                }
            }
    }

    override fun onDelete(item: ShoppingItem) {
        curListRef.update("items", FieldValue.arrayRemove(item))
    }

    override fun onItemChecked(position: Int) {
        db.runTransaction { transaction ->
            val snapshot = transaction.get(curListRef)
            val curItemList = snapshot["items"] as ArrayList<HashMap<String, Any>>
            if(curItemList[position].get("checked") as Boolean)
                curItemList[position].put("checked", false)
            else if(!(curItemList[position].get("checked") as Boolean))
                curItemList[position].put("checked", true)
            transaction.update(curListRef, "items", curItemList)
        }
    }

    interface ShoppingListActions {
        fun leaveList(curUser: FirebaseUser)
    }
}