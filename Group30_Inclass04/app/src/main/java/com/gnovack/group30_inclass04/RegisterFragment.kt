package com.gnovack.group30_inclass04

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gnovack.group30_inclass04.databinding.FragmentRegisterBinding
/*
In Class 04
RegisterFragment
Gabriel Novack
*/
/**
 * A simple [Fragment] subclass.
 * Use the [RegisterFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RegisterFragment : Fragment() {

    lateinit var binding: FragmentRegisterBinding

    lateinit var registration: OnRegistration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        activity?.title = getString(R.string.register_account)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.registerSubmit.setOnClickListener {
            registration.onRegister(DataServices.register(binding.registerName.text.toString(),
                binding.registerEmail.text.toString(), binding.registerPassword.text.toString()))
        }

        binding.registerCancel.setOnClickListener {
            registration.onCancel()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        registration = context as OnRegistration
    }

    interface OnRegistration {
        fun onRegister(newAccountAttempt:DataServices.AccountRequestTask)
        fun onCancel()
    }
}