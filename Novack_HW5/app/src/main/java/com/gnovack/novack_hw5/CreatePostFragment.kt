package com.gnovack.novack_hw5

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.gnovack.novack_hw5.databinding.FragmentCreatePostBinding
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.Executors

/**
 * A simple [Fragment] subclass.
 * Use the [CreatePostFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

//HW5
//CreatePostFragment
//Gabriel Novack

class CreatePostFragment : Fragment() {
    private var userString:String? = null
    private var userJson:JSONObject? = null

    lateinit var binding: FragmentCreatePostBinding
    lateinit var createActions: CreateActions

    val taskPool = Executors.newFixedThreadPool(2)
    val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userString = it.getString(PostFragment.USER_OBJECT)
        }
        userJson = JSONObject(userString)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreatePostBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.createSubmit.setOnClickListener {
            if(binding.createPost.text.toString() == ""){
                Toast.makeText(context, getString(R.string.empty_error), Toast.LENGTH_SHORT).show()
            }
            else {
                taskPool.execute {
                    val formBody = FormBody.Builder()
                        .add("post_text", binding.createPost.text.toString())
                        .build()
                    val request = Request.Builder()
                        .url("https://www.theappsdr.com/posts/create")
                        .addHeader("Authorization", "BEARER ${userJson!!.getString("token")}")
                        .post(formBody)
                        .build()
                    try {
                        val response = client.newCall(request).execute()
                        val resString = response.body!!.string()
                        val resJson = JSONObject(resString)
                        if (resJson.getString("status") == "ok") {
                            createActions.onPostCreate()
                        }
                    } catch (e: IOException) {
                        Log.d("POST_CREATE", "onViewCreated: $e")
                    }
                }
            }
        }
        binding.createCancel.setOnClickListener {
            createActions.onPostCancel()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        createActions = context as CreateActions
    }

    companion object {
        const val USER_OBJECT = "user-object"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(userJson: String) =
            CreatePostFragment().apply {
                arguments = Bundle().apply {
                    putString(USER_OBJECT, userJson)
                }
            }
    }

    interface CreateActions {
        fun onPostCreate()
        fun onPostCancel()
    }
}


