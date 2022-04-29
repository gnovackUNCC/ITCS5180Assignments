package com.gnovack.group30_inclass04

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gnovack.group30_inclass04.databinding.FragmentUpdateBinding
/*
In Class 04
UpdateFragment
Gabriel Novack
*/

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"

/**
 * A simple [Fragment] subclass.
 * Use the [UpdateFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UpdateFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var curAccount: DataServices.Account? = null

    lateinit var binding: FragmentUpdateBinding

    lateinit var updateActions: UpdateActions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            curAccount = it.getSerializable(ARG_PARAM1) as DataServices.Account
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentUpdateBinding.inflate(inflater, container, false)
        activity?.title = getString(R.string.update_account)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.updateShowEmail.text = curAccount?.getEmail()

        binding.editName.setText(curAccount?.name)
        binding.editPassword.setText(curAccount?.password)

        binding.editCancel.setOnClickListener {
            updateActions.onUpdateCancel()
        }
        binding.editSubmit.setOnClickListener {
            val attempt =DataServices.update(curAccount, binding.editName.text.toString(), binding.editPassword.text.toString())
            updateActions.onUpdate(attempt)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        updateActions = context as UpdateActions
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment UpdateFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: DataServices.Account) =
            UpdateFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, param1)
                }
            }
    }

    interface UpdateActions {
        fun onUpdate(updateAttempt: DataServices.AccountRequestTask)
        fun onUpdateCancel()
    }
}