package com.example.salman_ayaz_myruns

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.TimePicker
import androidx.appcompat.app.AlertDialog
import com.google.android.material.textfield.TextInputLayout
import java.util.Calendar

class ManualInputActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private val dateSelector by lazy { findViewById<EditText>(R.id.date_selector) }
    private val timeSelector by lazy { findViewById<EditText>(R.id.time_selector) }
    private val durationSelector by lazy { findViewById<EditText>(R.id.duration_selector) }
    private val distanceSelector by lazy { findViewById<EditText>(R.id.distance_selector) }
    private val caloriesSelector by lazy { findViewById<EditText>(R.id.calories_selector) }
    private val heatRateSelector by lazy { findViewById<EditText>(R.id.heart_rate_selector) }
    private val commentsSelector by lazy { findViewById<EditText>(R.id.comments_selector) }

    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manual_input)
        initializeListeners()
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
        durationSelector.setOnClickListener() {
            val fragment = InputDialogFragment.newInstance(
                "",
                "Duration",
                InputType.TYPE_CLASS_NUMBER
            )

            fragment.show(supportFragmentManager, "input_dialog")
        }
        distanceSelector.setOnClickListener() {
            val fragment = InputDialogFragment.newInstance(
                "",
                "Distance",
                InputType.TYPE_CLASS_NUMBER
            )

            fragment.show(supportFragmentManager, "input_dialog")
        }
        caloriesSelector.setOnClickListener() {
            val fragment = InputDialogFragment.newInstance(
                "",
                "Calories",
                InputType.TYPE_CLASS_NUMBER
            )

            fragment.show(supportFragmentManager, "input_dialog")
        }
        heatRateSelector.setOnClickListener() {
            val fragment = InputDialogFragment.newInstance(
                "",
                "Heart Rate",
                InputType.TYPE_CLASS_NUMBER
            )

            fragment.show(supportFragmentManager, "input_dialog")
        }
        commentsSelector.setOnClickListener() {
            val fragment = InputDialogFragment.newInstance(
                "",
                "Comments",
                InputType.TYPE_CLASS_TEXT
            )

            fragment.show(supportFragmentManager, "input_dialog")
        }

        // confirm and cancel buttons
        findViewById<Button>(R.id.button_confirm).setOnClickListener {
            finish()
        }
        findViewById<Button>(R.id.button_cancel).setOnClickListener {
            finish()
        }
    }

    private fun showInputDialog(title: String, hint: String, inputType: Int) {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle(title)

        val editText = EditText(this)
        editText.hint = hint
        editText.inputType = inputType

//        val layoutParams = LinearLayout.LayoutParams(
//            LinearLayout.LayoutParams.MATCH_PARENT,
//            LinearLayout.LayoutParams.WRAP_CONTENT
//        )
//        val marginInDp = resources.getDimension(R.dimen.edit_text_margin).toInt()
//        layoutParams.setMargins(marginInDp, marginInDp, marginInDp, marginInDp)
        //editText.layoutParams = layoutParams

        alertDialogBuilder.setView(editText)

        alertDialogBuilder.setPositiveButton("OK") { dialog: DialogInterface, which: Int ->
            val userInput = editText.text.toString()

            dialog.dismiss()
        }

        alertDialogBuilder.setNegativeButton("Cancel") { dialog: DialogInterface, which: Int ->
            dialog.dismiss()
        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {

    }

    override fun onTimeSet(view: TimePicker, hour: Int, minute: Int) {

    }
}