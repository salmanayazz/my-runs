package com.example.myruns.ui

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.myruns.R
import com.google.android.material.textfield.TextInputLayout

/**
 * interface for passing data from the dialog to the activity/fragment
 */
interface InputDialogListener {
    fun onDataPassed(data: String)
}

/**
 * a pop-up dialog that prompts the user for input
 */
class InputDialogFragment : DialogFragment(), DialogInterface.OnClickListener {
    private var inputDialogListener: InputDialogListener? = null
    private lateinit var editText: EditText

    companion object {
        const val DIALOG_KEY = "dialog"
        const val INPUT_DIALOG = 1
        const val ARG_TITLE = "title"
        const val ARG_HINT = "hint"
        const val ARG_INPUT_TYPE = ""

        fun newInstance(title: String, hint: String, inputType: Int): InputDialogFragment {
            val fragment = InputDialogFragment()
            val args = Bundle()
            args.putString(ARG_TITLE, title)
            args.putString(ARG_HINT, hint)
            args.putInt(ARG_INPUT_TYPE, inputType)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            inputDialogListener = context as InputDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement InputDialogListener")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // obtain the arguments that were passed to the fragment
        val inflater = LayoutInflater.from(requireContext())
        val view = inflater.inflate(R.layout.fragment_input_dialog, null)
        val title = arguments?.getString(ARG_TITLE) ?: ""
        val hint = arguments?.getString(ARG_HINT) ?: ""
        val inputType = arguments?.getInt(ARG_INPUT_TYPE) ?: InputType.TYPE_CLASS_TEXT
        
        // set up the dialog
        val textInputLayout: TextInputLayout = view.findViewById(R.id.text_input_layout)
        textInputLayout.hint = hint

        editText = view.findViewById(R.id.edit_text)
        editText.inputType = inputType

        // build the dialog
        return AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setView(view)
            .setPositiveButton("OK", this)
            .setNegativeButton("Cancel", this)
            .create()
    }

    override fun onClick(dialog: DialogInterface?, which: Int) {    
        if (which == DialogInterface.BUTTON_POSITIVE) {
            // send data to the activity/fragment
            val text = editText?.text.toString()
            inputDialogListener?.onDataPassed(text)
        }
    }
}