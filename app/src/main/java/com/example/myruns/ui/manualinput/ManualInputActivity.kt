package com.example.myruns.ui.manualinput

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.myruns.R
import com.example.myruns.data.exercise.ExerciseDatabase
import com.example.myruns.data.exercise.ExerciseRepository
import com.example.myruns.ui.ExerciseViewModelFactory
import com.example.myruns.ui.InputDialogFragment
import com.example.myruns.ui.InputDialogListener
import java.text.SimpleDateFormat
import java.util.Calendar

class ManualInputActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener, InputDialogListener {
    private val dateSelector by lazy { findViewById<EditText>(R.id.date_selector) }
    private val timeSelector by lazy { findViewById<EditText>(R.id.time_selector) }
    private val durationSelector by lazy { findViewById<EditText>(R.id.duration_selector) }
    private val distanceSelector by lazy { findViewById<EditText>(R.id.distance_selector) }
    private val caloriesSelector by lazy { findViewById<EditText>(R.id.calories_selector) }
    private val heartRateSelector by lazy { findViewById<EditText>(R.id.heart_rate_selector) }
    private val commentsSelector by lazy { findViewById<EditText>(R.id.comments_selector) }

    private val calendar = Calendar.getInstance()

    private lateinit var manualInputViewModel: ManualInputViewModel

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

        initializeListeners()
        initializeObservers()
        manualInputViewModel.getAll()
    }

    private fun initializeListeners() {
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

        onClickInputFragment(
            durationSelector,
            ManualInputViewModel.DialogOptions.DURATION,
            "",
            "Duration",
            InputType.TYPE_CLASS_NUMBER
        )
        onClickInputFragment(
            distanceSelector,
            ManualInputViewModel.DialogOptions.DISTANCE,
            "",
            "Distance",
            InputType.TYPE_CLASS_NUMBER
        )
        onClickInputFragment(
            caloriesSelector,
            ManualInputViewModel.DialogOptions.CALORIES,
            "",
            "Calories",
            InputType.TYPE_CLASS_NUMBER
        )
        onClickInputFragment(
            heartRateSelector,
            ManualInputViewModel.DialogOptions.HEART_RATE,
            "",
            "Heart Rate",
            InputType.TYPE_CLASS_NUMBER
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
            finish()
        }
    }

    private fun initializeObservers() {
        manualInputViewModel.dateTime.observe(this) {
            dateSelector.setText(SimpleDateFormat("yyyy-MM-dd").format(it.time))
            timeSelector.setText(SimpleDateFormat("HH:mm").format(it.time))
        }

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
    }

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

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        manualInputViewModel.dateTime.value?.set(Calendar.YEAR, year)
        manualInputViewModel.dateTime.value?.set(Calendar.MONTH, month)
        manualInputViewModel.dateTime.value?.set(Calendar.DAY_OF_MONTH, day)

        // force a notify
        manualInputViewModel.dateTime.value = manualInputViewModel.dateTime.value
    }

    override fun onTimeSet(view: TimePicker, hour: Int, minute: Int) {
        manualInputViewModel.dateTime.value?.set(Calendar.HOUR_OF_DAY, hour)
        manualInputViewModel.dateTime.value?.set(Calendar.MINUTE, minute)

        // force a notify
        manualInputViewModel.dateTime.value = manualInputViewModel.dateTime.value
    }

    override fun onDataPassed(data: String) {
        try {
            when (manualInputViewModel.dialogOpen.value) {
                ManualInputViewModel.DialogOptions.DURATION ->
                    manualInputViewModel.duration.value = data.toInt()
                ManualInputViewModel.DialogOptions.DISTANCE ->
                    manualInputViewModel.distance.value = data.toInt()
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