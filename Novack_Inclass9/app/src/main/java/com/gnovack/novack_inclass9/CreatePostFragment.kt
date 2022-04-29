package com.gnovack.novack_inclass9

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.gnovack.novack_inclass9.databinding.FragmentCreatePostBinding
import com.google.firebase.Timestamp

//HW5
//CreatePostFragment
//Gabriel Novack
class CreatePostFragment : Fragment() {

    lateinit var binding:FragmentCreatePostBinding
    lateinit var createActions: CreateActions

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
            if(binding.createTitle.text.toString() == "" || binding.createContent.text.toString() == ""){
                AlertDialog.Builder(context).setTitle(getString(R.string.error)).setMessage(getString(R.string.empty_error)).show()
            }
            else {
                createActions.onPostCreated(
                    Forum(
                        "",
                        binding.createTitle.text.toString(), binding.createContent.text.toString(),
                        Timestamp.now()
                    )
                )
            }
        }
        binding.createCancel.setOnClickListener {
            createActions.onCreateCancel()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        createActions = context as CreateActions
    }

    interface CreateActions {
        fun onPostCreated(newPost:Forum)
        fun onCreateCancel()
    }
}