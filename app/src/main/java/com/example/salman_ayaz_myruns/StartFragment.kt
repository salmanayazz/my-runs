package com.example.salman_ayaz_myruns

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button

/**
 * A simple [Fragment] subclass.
 * Use the [StartFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class StartFragment : Fragment() {
    private val inputTypeList = listOf("Manual Entry", "GPS", "Automatic")
    private val activityTypeList = listOf(
        "Running", "Walking", "Standing", "Cycling",
        "Hiking", "Downhill Skiing", "Cross-Country Skiing",
        "Snowboarding", "Skating", "Swimming", "Mountain Biking",
        "Wheelchair", "Elliptical", "Other"
    )

    private lateinit var inputType: AutoCompleteTextView
    private lateinit var activityType: AutoCompleteTextView
    private lateinit var startButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_start, container, false)

        inputType = rootView.findViewById(R.id.actv_input_type)
        activityType = rootView.findViewById(R.id.actv_activity_type)

        setupACTV(
            rootView,
            inputType.id,
            inputTypeList
        )

        setupACTV(
            rootView,
            activityType.id,
            activityTypeList
        )

        var selectedInput = inputTypeList[0]
        inputType.onItemClickListener = AdapterView.OnItemClickListener { parent, _, position, _ ->
            selectedInput = parent.getItemAtPosition(position).toString()
        }

        startButton = rootView.findViewById(R.id.button_start)
        startButton.setOnClickListener() {
            // start activity based on user selection
            val settings = Intent(
                requireActivity(),
                if (selectedInput == "Manual Entry") {
                    ManualInputActivity::class.java
                } else {
                    MapDisplayActivity::class.java
                }
            )
            startActivity(settings)
        }

        return rootView
    }

    // sets up the AutoCompleteTextView with the given actvID
    private fun setupACTV(rootView: View, actvID: Int, menuOptions: List<String>) {
        val autoCompleteTextView = rootView.findViewById<AutoCompleteTextView>(actvID)

        // create an ArrayAdapter with the options and a simple layout for dropdown items
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, menuOptions)
        autoCompleteTextView.setAdapter(adapter)

        // set default value to be the first element
        autoCompleteTextView.setText(menuOptions[0], false)
    }
}