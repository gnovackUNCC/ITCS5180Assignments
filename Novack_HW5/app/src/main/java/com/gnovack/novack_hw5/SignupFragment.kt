package com.gnovack.novack_hw5

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.gnovack.novack_hw5.databinding.FragmentSignupBinding
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.Executors


/**
 * A simple [Fragment] subclass.
 * Use the [SignupFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

//HW5
//SignupFragment
//Gabriel Novack

class SignupFragment : Fragment() {
    private val SUCCESS = 1
    private val FAILURE = 0
    private val CONNECT_FAILURE = -1

    lateinit var binding: FragmentSignupBinding
    lateinit var signUpActions: SignUpActions

    val taskPool = Executors.newFixedThreadPool(2)
    val client = OkHttpClient()

    val handler = Handler(Looper.myLooper()!!, Handler.Callback {

        if(it.what == SUCCESS){
            signUpActions.onSubmit(it.obj as String)
        } else if(it.what == FAILURE){
            val resJson = it.obj as JSONObject
            AlertDialog.Builder(context).setTitle(getString(R.string.error)).setMessage(resJson.getString("message")).show()
        } else {
            Toast.makeText(activity, getString(R.string.connection_error), Toast.LENGTH_SHORT).show()
        }
        true
    })
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignupBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.signupSubmit.setOnClickListener {
            if(binding.signupEmail.text.toString() == "" || binding.signupName.text.toString() == "" || binding.signupPassword.text.toString() == ""){
                AlertDialog.Builder(context).setTitle(getString(R.string.error)).setMessage(getString(R.string.empty_error)).show()
            }else {
                taskPool.execute {
                    val message = Message()
                    val formBody = FormBody.Builder()
                        .add("email", binding.signupEmail.text.toString())
                        .add("password", binding.signupPassword.text.toString())
                        .add("name", binding.signupName.text.toString())
                        .build()
                    val request = Request.Builder()
                        .url("https://www.theappsdr.com/posts/signup")
                        .post(formBody)
                        .build()
                    try {
                        val response = client.newCall(request).execute()
                        val resString = response.body!!.string()
                        val resJson = JSONObject(resString)
                        if (resJson.getString("status") == "ok") {
                            message.what = SUCCESS
                            message.obj = resString
                        } else {
                            message.what = FAILURE
                            message.obj = resJson
                        }
                    } catch (e: IOException) {
                        message.what = CONNECT_FAILURE
                        message.obj = JSONObject("{res: ${e}}")
                    }
                    handler.sendMessage(message)
                }
            }
        }
        binding.signupCancel.setOnClickListener {
            signUpActions.onCancel()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        signUpActions = context as SignUpActions
    }

    interface SignUpActions {
        fun onSubmit(res:String)
        fun onCancel()
    }

}