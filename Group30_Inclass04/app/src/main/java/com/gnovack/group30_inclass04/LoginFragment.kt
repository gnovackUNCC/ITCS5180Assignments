package com.gnovack.group30_inclass04

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gnovack.group30_inclass04.databinding.FragmentLoginBinding
/*
In Class 04
LoginFragment
Gabriel Novack
*/
/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : Fragment() {
    lateinit var login:OnLoginActions

    lateinit var binding:FragmentLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        activity?.title = getString(R.string.login)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.loginButton.setOnClickListener {
            login.onLogin(binding.loginEmail.text.toString(), binding.loginPassword.text.toString())
        }
        binding.createAccount.setOnClickListener {
            login.onCreateAccount()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        login = context as OnLoginActions
    }

    interface OnLoginActions {
        fun onLogin(email:String, pass:String)
        fun onCreateAccount()
    }
}