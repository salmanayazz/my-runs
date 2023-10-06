package com.example.salman_ayaz_myruns

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Spinner
import com.google.android.material.textfield.TextInputLayout

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [StartRunsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class StartRunsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_start_runs, container, false)

        setupACTV(
            rootView,
            R.id.actv_input_type,
            listOf("Manual Entry", "GPS", "Automatic")
        )
        return rootView
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment StartRunsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            StartRunsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun setupACTV(rootView: View, actvID: Int, menuOptions: List<String>) {
        val autoCompleteTextView = rootView.findViewById<AutoCompleteTextView>(actvID)

        // create an ArrayAdapter with the options and a simple layout for dropdown items
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, menuOptions)
        autoCompleteTextView.setAdapter(adapter)

        // set default value to be the first element
        autoCompleteTextView.setText(menuOptions[0], false)
    }
}