package com.example.myruns

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.google.android.material.textfield.TextInputLayout

/**
 * A simple [Fragment] subclass.
 * Use the [InputDialogFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class InputDialogFragment : DialogFragment(), DialogInterface.OnClickListener {
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

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = LayoutInflater.from(requireContext())
        val view = inflater.inflate(R.layout.fragment_input_dialog, null)
        val title = arguments?.getString(ARG_TITLE) ?: ""
        val hint = arguments?.getString(ARG_HINT) ?: ""
        val inputType = arguments?.getInt(ARG_INPUT_TYPE) ?: InputType.TYPE_CLASS_TEXT

        val textInputLayout: TextInputLayout = view.findViewById(R.id.text_input_layout)
        textInputLayout.hint = hint

        val editText: EditText = view.findViewById(R.id.edit_text)
        editText.inputType = inputType

        return AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setView(view)
            .setPositiveButton("OK", this)
            .setNegativeButton("Cancel", this)
            .create()
    }

    override fun onClick(dialog: DialogInterface?, which: Int) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
            val editText: EditText? = view?.findViewById(R.id.edit_text)
            val text = editText?.text.toString()
            //Toast.makeText(activity, "Ok clicked. Text: $text", Toast.LENGTH_LONG).show()
        } else if (which == DialogInterface.BUTTON_NEGATIVE) {
            //Toast.makeText(activity, "Cancel clicked", Toast.LENGTH_LONG).show()
        }
    }
}