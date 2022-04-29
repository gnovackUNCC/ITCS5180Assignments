package com.gnovack.novack_inclass9

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.gnovack.novack_inclass9.databinding.FragmentLoginBinding

//HW5
//LoginFragment
//Gabriel Novack
class LoginFragment : Fragment() {

    lateinit var binding: FragmentLoginBinding
    lateinit var loginActions: LoginActions

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
                loginActions.onLogin(
                    binding.loginEmail.text.toString(),
                    binding.loginPassword.text.toString()
                )
            }
        }
        binding.createAccount.setOnClickListener {
            loginActions.onCreateAccount()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        loginActions = context as LoginActions
    }

    interface LoginActions {
        fun onLogin(email:String, password:String)
        fun onCreateAccount()
    }
}