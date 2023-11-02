package com.example.myruns.ui.history

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import com.example.myruns.R
import com.example.myruns.data.exercise.ExerciseEntry
import java.io.ByteArrayInputStream
import java.io.ObjectInputStream
import java.text.SimpleDateFormat

class HistoryEntryActivity : AppCompatActivity() {
    private var exerciseEntry: ExerciseEntry? = null
    companion object {
        val EXERCISE_ENTRY = "exercise_entry"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_entry)

        // get the ExerciseEntry as a byte array
        val exerciseByteArr = intent.getByteArrayExtra(EXERCISE_ENTRY)

        try {
            // parse into ExerciseEntry if not null
            exerciseEntry = ObjectInputStream(
                ByteArrayInputStream(exerciseByteArr)
            ).readObject() as ExerciseEntry
            println("git the entry: $exerciseEntry")
            populateText()
        } catch (e: Exception) {
            println(e.printStackTrace())
        }

    }

    private fun populateText() {
        if (exerciseEntry != null) {
//            findViewById<EditText>(R.id.date)
//                .setText(SimpleDateFormat("yyyy-MM-dd")
//                    .format(exerciseEntry!!.dateTime))
//
//            findViewById<EditText>(R.id.time)
//                .setText(SimpleDateFormat("HH:mm")
//                    .format(exerciseEntry!!.dateTime))

            findViewById<EditText>(R.id.duration)
                .setText(exerciseEntry!!.duration?.toString() ?: "")

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
}