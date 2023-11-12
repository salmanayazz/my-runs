package com.example.myruns.ui.manualinput

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TimePicker
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.example.myruns.R
import com.example.myruns.data.exercise.ExerciseDatabase
import com.example.myruns.data.exercise.ExerciseRepository
import com.example.myruns.ui.ExerciseViewModelFactory
import com.example.myruns.ui.InputDialogFragment
import com.example.myruns.ui.InputDialogListener
import com.example.myruns.ui.SettingsFragment
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.Calendar

/**
 * activity that allows the user to manually create an ExerciseEntry
 */
class ManualInputActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener, InputDialogListener {
    companion object {
        const val ACTIVITY_TYPE_KEY = "activity_type_key"
    }

    private val dateSelector by lazy { findViewById<EditText>(R.id.date_selector) }
    private val timeSelector by lazy { findViewById<EditText>(R.id.time_selector) }
    private val durationSelector by lazy { findViewById<EditText>(R.id.duration_selector) }
    private val distanceTextInputLayout by lazy { findViewById<TextInputLayout>(R.id.distance_text_input)}
    private val distanceSelector by lazy { findViewById<EditText>(R.id.distance_selector) }
    private val caloriesSelector by lazy { findViewById<EditText>(R.id.calories_selector) }
    private val heartRateSelector by lazy { findViewById<EditText>(R.id.heart_rate_selector) }
    private val commentsSelector by lazy { findViewById<EditText>(R.id.comments_selector) }

    private val calendar = Calendar.getInstance()

    private lateinit var manualInputViewModel: ManualInputViewModel
    private val sharedPreferences by lazy { PreferenceManager.getDefaultSharedPreferences(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manual_input)

        // setup mvvm objects
        var database = ExerciseDatabase.getInstance(this)
        var databaseDao = database.exerciseDatabaseDao
        var repository = ExerciseRepository(databaseDao)
        var viewModelFactory = ExerciseViewModelFactory(repository)
        manualInputViewModel = ViewModelProvider(
            this,
            viewModelFactory
        )[ManualInputViewModel::class.java]


        manualInputViewModel.activityType =
            intent.getIntExtra(ACTIVITY_TYPE_KEY, 0)

        initializeListeners()
        initializeObservers()
    }

    /**
     * updates the units every time the fragment is resumed
     */
    override fun onResume() {
        super.onResume()

        // update the units every time fragment is loaded
        val unit = sharedPreferences
            .getString(
                SettingsFragment.UNIT_PREFERENCE,
                SettingsFragment.UNIT_METRIC
            )
        manualInputViewModel.unitPreference.value = unit
        Log.d("ManualInputActivity", "UNIT_PREFERENCE changed to $unit")
    }

    /**
     * initializes all the listeners for the activity
     * including the date and time selectors and the input fragments
     */
    private fun initializeListeners() {
        // date & time
        dateSelector.setOnClickListener() {
            DatePickerDialog(
                this,
                this,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
        timeSelector.setOnClickListener() {
            TimePickerDialog(
                this,
                this,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                false
            ).show()
        }

        // alert input fragments
        onClickInputFragment(
            durationSelector,
            ManualInputViewModel.DialogOptions.DURATION,
            "",
            "Duration",
            InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        )
        onClickInputFragment(
            distanceSelector,
            ManualInputViewModel.DialogOptions.DISTANCE,
            "",
            "Distance",
            InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        )
        onClickInputFragment(
            caloriesSelector,
            ManualInputViewModel.DialogOptions.CALORIES,
            "",
            "Calories",
            InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        )
        onClickInputFragment(
            heartRateSelector,
            ManualInputViewModel.DialogOptions.HEART_RATE,
            "",
            "Heart Rate",
            InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        )
        onClickInputFragment(
            commentsSelector,
            ManualInputViewModel.DialogOptions.COMMENTS,
            "",
            "Comments",
            InputType.TYPE_CLASS_TEXT
        )

        // confirm and cancel buttons
        findViewById<Button>(R.id.button_confirm).setOnClickListener {
            manualInputViewModel.insert()
            finish()
        }
        findViewById<Button>(R.id.button_cancel).setOnClickListener {
            Toast.makeText(
                this,
                "Entry Discarded",
                Toast.LENGTH_SHORT
            ).show()
            finish()
        }
    }

    /**
     * initializes all the observers for the activity
     * including the date and time selectors and the input fragments
     */
    private fun initializeObservers() {
        // date & time
        manualInputViewModel.dateTime.observe(this) {
            dateSelector.setText(SimpleDateFormat("yyyy-MM-dd").format(it.time))
            timeSelector.setText(SimpleDateFormat("HH:mm").format(it.time))
        }
        
        // input fragments
        manualInputViewModel.duration.observe(this) {
            durationSelector.setText(it.toString())
        }
        manualInputViewModel.duration.observe(this) {
            durationSelector.setText(it.toString())
        }
        manualInputViewModel.distance.observe(this) {
            distanceSelector.setText(it.toString())
        }
        manualInputViewModel.calories.observe(this) {
            caloriesSelector.setText(it.toString())
        }
        manualInputViewModel.heartRate.observe(this) {
            heartRateSelector.setText(it.toString())
        }
        manualInputViewModel.comments.observe(this) {
            commentsSelector.setText(it.toString())
        }

        // change the suffix text for the distance selector
        manualInputViewModel.unitPreference.observe(this) {
            if (it == SettingsFragment.UNIT_METRIC) {
                distanceTextInputLayout.suffixText = "kilometers"
            } else {
                distanceTextInputLayout.suffixText = "miles"
            }
        }
    }

    /**
     * sets up the onClickListener for the input fragments
     * @param selector
     * the view that opens the input fragment
     * @param dialogOption
     * the dialog option to open
     * @param title
     * the title of the input fragment
     * @param hint
     * the hint of the input fragment
     * @param inputType
     * the input type of the input fragment (e.g. InputType.TYPE_CLASS_NUMBER)
     */
    private fun onClickInputFragment(selector: View, dialogOption: ManualInputViewModel.DialogOptions,title: String, hint: String, inputType: Int) {
        selector.setOnClickListener() {
            manualInputViewModel.dialogOpen.value = dialogOption

            val fragment = InputDialogFragment.newInstance(
                title,
                hint,
                inputType
            )

            fragment.show(supportFragmentManager, "input_dialog")
        }
    }

    /**
     * sets the date of the ExerciseEntry
     */
    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        manualInputViewModel.dateTime.value?.set(Calendar.YEAR, year)
        manualInputViewModel.dateTime.value?.set(Calendar.MONTH, month)
        manualInputViewModel.dateTime.value?.set(Calendar.DAY_OF_MONTH, day)

        // force a notify
        manualInputViewModel.dateTime.value = manualInputViewModel.dateTime.value
    }

    /**
     * sets the time of the ExerciseEntry
     */
    override fun onTimeSet(view: TimePicker, hour: Int, minute: Int) {
        manualInputViewModel.dateTime.value?.set(Calendar.HOUR_OF_DAY, hour)
        manualInputViewModel.dateTime.value?.set(Calendar.MINUTE, minute)

        // force a notify
        manualInputViewModel.dateTime.value = manualInputViewModel.dateTime.value
    }

    /**
     * sets the data of the ExerciseEntry based on the data passed from the 
     * InputDialogFragment through the InputDialogListener interface
     */
    override fun onDataPassed(data: String) {
        try {
            when (manualInputViewModel.dialogOpen.value) {
                ManualInputViewModel.DialogOptions.DURATION ->
                    manualInputViewModel.duration.value = data.toDouble()
                ManualInputViewModel.DialogOptions.DISTANCE ->
                    manualInputViewModel.distance.value = data.toDouble()
                ManualInputViewModel.DialogOptions.CALORIES ->
                    manualInputViewModel.calories.value = data.toDouble()
                ManualInputViewModel.DialogOptions.HEART_RATE ->
                    manualInputViewModel.heartRate.value = data.toDouble()
                ManualInputViewModel.DialogOptions.COMMENTS ->
                    manualInputViewModel.comments.value = data
                else -> {}
            }
        } catch (e: NumberFormatException) {
            Toast.makeText(
                this,
                "Invalid Input",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}