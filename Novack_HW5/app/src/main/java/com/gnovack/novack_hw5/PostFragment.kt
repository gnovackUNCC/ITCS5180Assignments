package com.gnovack.novack_hw5

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.gnovack.novack_hw5.databinding.FragmentPostListBinding
import com.gnovack.novack_hw5.databinding.PageButtonBinding
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.Executors

/**
 * A fragment representing a list of Items.
 */

//HW5
//PostFragment
//Gabriel Novack

class PostFragment : Fragment(), PostRecyclerViewAdapter.DeleteClickListener {

    private val SUCCESS = 1
    private val FAILURE = 0
    private val CONNECT_FAILURE = -1

    private var userString:String? = null
    private var userJson:JSONObject? = null
    private var postList:JSONArray? = null

    lateinit var binding: FragmentPostListBinding
    lateinit var postActions: PostActions

    private var curPage = 1

    private var firstTime = true

    val taskPool = Executors.newFixedThreadPool(2)
    val client = OkHttpClient()

    val handler = Handler(Looper.myLooper()!!, Handler.Callback {
        if(it.what == SUCCESS){
            postList = (it.obj as JSONObject).getJSONArray("posts")
            val posts = mutableListOf<Post>()
            val createdByUserIndices = mutableListOf<Int>()
            for(p in 1..postList!!.length()){
                val curPost = postList!!.getJSONObject(p-1)
                posts.add(Post(curPost.getString("created_by_name"), curPost.getString("post_id"),
                    curPost.getString("created_by_uid"), curPost.getString("post_text"), curPost.getString("created_at"), curPost.getString("created_by_uid") == userJson!!.getString("user_id").toString()))
            }

            for(p in 0..postList!!.length()-1){
                val curPost = postList!!.getJSONObject(p)
                if(curPost.getString("created_by_uid") == userJson!!.getString("user_id").toString()){
                    createdByUserIndices.add(p)
                }
            }
            val numPages = when((it.obj as JSONObject).getInt("totalCount") % 10){
                0 -> (it.obj as JSONObject).getInt("totalCount")/10
                else -> (it.obj as JSONObject).getInt("totalCount")/10 + 1
            }
            if(firstTime) {
                for (i in 1..numPages) {
                    val newButton =
                        PageButtonBinding.inflate(layoutInflater, binding.buttonLayout, true)
                    newButton.pageButton.text = i.toString()
                    newButton.pageButton.setOnClickListener {
                        updatePage(newButton.pageButton.text.toString().toInt())
                    }
                }
            }
            binding.showingPage.text = resources.getString(R.string.showing_page, curPage, numPages)
            with(binding.postList) {
                adapter = PostRecyclerViewAdapter(posts!!, this@PostFragment)
            }
        } else {
            Toast.makeText(activity, getString(R.string.connection_error), Toast.LENGTH_SHORT).show()
        }
        firstTime = false
        true
    })

    private fun updatePage(page: Int) {
        curPage = page
        taskPool.execute {
            val message = Message()
            val request = Request.Builder()
                .url("https://www.theappsdr.com/posts?page=$page")
                .addHeader("Authorization", "BEARER ${userJson!!.getString("token")}")
                .build()
            try{
                val response = client.newCall(request).execute()
                val resString = response.body!!.string()
                val resJson = JSONObject(resString)
                if(resJson.getString("status") == "ok"){
                    message.what = SUCCESS
                    message.obj = resJson
                } else {
                    message.what = FAILURE
                    message.obj = resJson
                }
            } catch(e: IOException) {
                message.what = CONNECT_FAILURE
                message.obj = JSONObject("{res: ${e}}")
            }
            handler.sendMessage(message)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            userString = it.getString(USER_OBJECT)
        }
        userJson = JSONObject(userString)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPostListBinding.inflate(inflater, container, false)

        binding.postWelcome.text = resources.getString(R.string.welcome, userJson!!.getString("user_fullname"))
        // Set the adapter
        with(binding.postList) {
            layoutManager = LinearLayoutManager(context)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.logoutButton.setOnClickListener {
            postActions.onLogOut()
        }
        binding.createPostButton.setOnClickListener {
            postActions.onCreatePost(userString!!)
        }
    }

    override fun onStart() {
        super.onStart()
        updatePage(1)
    }

    override fun onPause() {
        super.onPause()
        firstTime = true
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        postActions = context as PostActions
    }

    companion object {

        // TODO: Customize parameter argument names
        const val USER_OBJECT = "user-object"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(userJson: String) =
            PostFragment().apply {
                arguments = Bundle().apply {
                    putString(USER_OBJECT, userJson)
                }
            }
    }

    interface PostActions {
        fun onLogOut()
        fun onCreatePost(userJson: String)
    }

    override fun onDelete(postId: String) {
        AlertDialog.Builder(context).setTitle(getString(R.string.caution)).setMessage(getString(R.string.delete)).setPositiveButton(getString(
                    R.string.ok), DialogInterface.OnClickListener { dialogInterface, i ->
            taskPool.execute {
                val formBody = FormBody.Builder()
                    .add("post_id", postId)
                    .build()
                val request = Request.Builder()
                    .url("https://www.theappsdr.com/posts/delete")
                    .addHeader("Authorization", "BEARER ${userJson!!.getString("token")}")
                    .post(formBody)
                    .build()
                try {
                    val response = client.newCall(request).execute()
                    val resString = response.body!!.string()
                    val resJson = JSONObject(resString)
                    if (resJson.getString("status") == "ok") {
                        updatePage(curPage)
                    }else {
                        Toast.makeText(context, resJson.getString("message"), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: IOException) {
                    Log.d("POST_DELETE", "onViewCreated: $e")
                }
            }
        }).setNegativeButton(getString(R.string.cancel), null).show()

    }
}