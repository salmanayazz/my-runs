package com.example.myruns.ui.history

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.example.myruns.R
import com.example.myruns.Utils.getParcelableExtraCompat
import com.example.myruns.data.exercise.ExerciseDatabase
import com.example.myruns.data.exercise.ExerciseEntry
import com.example.myruns.data.exercise.ExerciseRepository
import com.example.myruns.data.exercise.ExerciseViewModelFactory
import com.example.myruns.ui.SettingsFragment
import com.google.android.material.textfield.TextInputLayout
import java.io.ByteArrayInputStream
import java.io.ObjectInputStream
import java.text.SimpleDateFormat

/**
 * activity that displays a single ExerciseEntry and allows the user to delete it
 */
class HistoryEntryActivity : AppCompatActivity() {
    private var exerciseEntry: ExerciseEntry? = null
    private lateinit var historyViewModel: HistoryViewModel
    private val distanceTextInputLayout by lazy { findViewById<TextInputLayout>(R.id.distance_text_input) }
    private val sharedPreferences by lazy { PreferenceManager.getDefaultSharedPreferences(this) }
    companion object {
        const val EXERCISE_ENTRY = "exercise_entry"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_entry)

        // setup mvvm objects
        var database = ExerciseDatabase.getInstance(this)
        var databaseDao = database.exerciseDatabaseDao
        var repository = ExerciseRepository(databaseDao)
        var viewModelFactory = ExerciseViewModelFactory(repository)
        historyViewModel = ViewModelProvider(
            this,
            viewModelFactory
        )[HistoryViewModel::class.java]

        exerciseEntry = intent.getParcelableExtraCompat(
            EXERCISE_ENTRY,
            ExerciseEntry::class.java
        )

        if (exerciseEntry == null) {
            Log.e("HistoryEntryActivity", "passed ExerciseEntry is null")
        }

        populateText()

        // delete entry onClick
        findViewById<ImageView>(R.id.delete_entry).setOnClickListener() {
            exerciseEntry?.id?.let {
                    it1 -> historyViewModel.delete(it1)
            }
            this.finish()
        }

        setupObservers()
    }

    /**
     * populates the EditTexts with the ExerciseEntry's data
     */
    private fun populateText() {
        if (exerciseEntry != null && exerciseEntry!!.dateTime != null) {
            try {
                val date = exerciseEntry!!.dateTime?.time
                findViewById<EditText>(R.id.date)
                    .setText(SimpleDateFormat("yyyy-MM-dd").format(date))
                findViewById<EditText>(R.id.time)
                    .setText(SimpleDateFormat("HH:mm").format(date))
            } catch (e: IllegalArgumentException) {
                println(e.printStackTrace())
                Toast.makeText(
                    this,
                    "Date Format error",
                    Toast.LENGTH_SHORT
                ).show()
            }

            // get mins and secs individually
            val mins = (exerciseEntry!!.duration?.toInt()?.toString() ?: "0") + " mins"
            // get decimal part of the duration
            val secs = ((exerciseEntry!!.duration?.rem(1.0) ?: 0.0) * 60).toInt().toString() + " secs"

            findViewById<EditText>(R.id.duration)
                .setText(mins + " " + secs)

            findViewById<EditText>(R.id.distance)
                .setText(exerciseEntry!!.distance?.toString() ?: "")

            findViewById<EditText>(R.id.calories)
                .setText(exerciseEntry!!.calories?.toString() ?: "")

            findViewById<EditText>(R.id.heart_rate)
                .setText(exerciseEntry!!.heartRate?.toString() ?: "")

            findViewById<EditText>(R.id.comments)
                .setText(exerciseEntry!!.comment ?: "")
        }
    }

    /**
     * updates the units every time the fragment is loaded
     */
    override fun onResume() {
        super.onResume()

        // update the units every time fragment is loaded
        val unit = sharedPreferences
            .getString(
                SettingsFragment.UNIT_PREFERENCE,
                SettingsFragment.UNIT_METRIC
            )
        historyViewModel.unitPreference.value = unit
        Log.d("HistoryFragment", "UNIT_PREFERENCE changed to $unit")
    }

    /**
     * sets up the observers for the unit preference
     */
    private fun setupObservers() {
        historyViewModel.unitPreference.observe(this) {
            if (historyViewModel.unitPreference.value == SettingsFragment.UNIT_METRIC) {
                distanceTextInputLayout.suffixText = "kilometers"
            } else {
                distanceTextInputLayout.suffixText = "miles"
            }
        }
    }
}