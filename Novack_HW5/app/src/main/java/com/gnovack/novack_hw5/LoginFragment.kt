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
import com.gnovack.novack_hw5.databinding.FragmentLoginBinding
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.Executors

/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
//HW5
//LoginFragment
//Gabriel Novack

class LoginFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private val SUCCESS = 1
    private val FAILURE = 0
    private val CONNECT_FAILURE = -1

    lateinit var binding: FragmentLoginBinding
    lateinit var loginActions: LoginActions

    val taskPool = Executors.newFixedThreadPool(2)
    val client = OkHttpClient()

    val handler = Handler(Looper.myLooper()!!, Handler.Callback {
        if(it.what == SUCCESS){
            loginActions.onSuccess(it.obj as String)
        } else if (it.what == FAILURE) {
            loginActions.onFailure(it.obj as JSONObject)
        } else {
            Toast.makeText(activity, getString(R.string.connection_error), Toast.LENGTH_SHORT).show()
        }
        true
    })

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.loginSubmit.setOnClickListener {
            if(binding.loginEmail.text.toString() == "" || binding.loginPassword.text.toString() == ""){
                AlertDialog.Builder(context).setTitle(getString(R.string.error)).setMessage(getString(R.string.empty_error)).show()
            }else {
                taskPool.execute {
                    val message = Message()
                    val formBody = FormBody.Builder()
                        .add("email", binding.loginEmail.text.toString())
                        .add("password", binding.loginPassword.text.toString())
                        .build()
                    val request = Request.Builder()
                        .url("https://www.theappsdr.com/posts/login")
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
        binding.createAccount.setOnClickListener {
            loginActions.onSignupClicked()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        loginActions = context as LoginActions
    }

    interface LoginActions {
        fun onSignupClicked()
        fun onSuccess(res:String)
        fun onFailure(res:JSONObject)
    }
}