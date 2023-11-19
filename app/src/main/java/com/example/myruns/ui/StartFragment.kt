package com.example.myruns.ui

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
import com.example.myruns.R
import com.example.myruns.ui.manualinput.ManualInputActivity
import com.example.myruns.ui.mapdisplay.MapDisplayActivity

/**
 * A simple [Fragment] subclass.
 * Use the [StartFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class StartFragment : Fragment() {
    companion object {
        val inputTypeList = listOf("Manual Entry", "GPS", "Automatic")
        val activityTypeList = listOf(
            "Running", "Walking", "Standing", "Cycling",
            "Hiking", "Downhill Skiing", "Cross-Country Skiing",
            "Snowboarding", "Skating", "Swimming", "Mountain Biking",
            "Wheelchair", "Elliptical", "Other"
        )

        val INPUT_TYPE_MANUAL = inputTypeList[0]
        val INPUT_TYPE_GPS = inputTypeList[1]
        val INPUT_TYPE_AUTOMATIC = inputTypeList[2]
    }


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


        // setup AutoCompleteTextViews
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

        // listeners for AutoCompleteTextViews
        var selectedInput = 0
        inputType.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            selectedInput = position
        }

        var selectedActivity = 0
        activityType.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            selectedActivity = position
        }

        // submit and start ManualInputActivity or MapDisplayActivity
        startButton = rootView.findViewById(R.id.button_start)
        startButton.setOnClickListener() {
            // start activity based on user selection
            val intent = if (inputTypeList[selectedInput] == INPUT_TYPE_MANUAL) {
                Intent(requireContext(), ManualInputActivity::class.java)
                    .putExtra(ManualInputActivity.ACTIVITY_TYPE_KEY, selectedActivity)
            } else {
                Intent(requireContext(), MapDisplayActivity::class.java)
                    .putExtra(MapDisplayActivity.INPUT_TYPE_KEY, inputTypeList[selectedInput])
                    .putExtra(MapDisplayActivity.ACTIVITY_TYPE_KEY, selectedActivity)
            }
            startActivity(intent)
        }

        return rootView
    }

    /**
     * sets up the AutoCompleteTextView with the given actvID
     * @param rootView
     * the root view of the fragment
     * @param actvID
     * the id of the AutoCompleteTextView
     * @param menuOptions
     * the list of options to display in the AutoCompleteTextView
     */
    private fun setupACTV(rootView: View, actvID: Int, menuOptions: List<String>) {
        val autoCompleteTextView = rootView.findViewById<AutoCompleteTextView>(actvID)

        // create an ArrayAdapter with the options and a simple layout for dropdown items
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, menuOptions)
        autoCompleteTextView.setAdapter(adapter)

        // set default value to be the first element
        autoCompleteTextView.setText(menuOptions[0], false)
    }
}