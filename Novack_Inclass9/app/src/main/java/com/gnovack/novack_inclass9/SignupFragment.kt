package com.gnovack.novack_inclass9

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.gnovack.novack_inclass9.databinding.FragmentSignupBinding

//HW5
//SignupFragment
//Gabriel Novack
class SignupFragment : Fragment() {
    lateinit var binding: FragmentSignupBinding
    lateinit var signupActions: SignupActions

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
                signupActions.onSignup(
                    binding.signupName.text.toString(),
                    binding.signupEmail.text.toString(),
                    binding.signupPassword.text.toString()
                )
            }
        }
        binding.signupCancel.setOnClickListener {
            signupActions.onSignupCancel()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        signupActions = context as SignupActions
    }

    interface SignupActions {
        fun onSignup(name:String, email:String, password:String)
        fun onSignupCancel()
    }
}