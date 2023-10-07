package com.example.salman_ayaz_myruns

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.google.android.material.textfield.TextInputLayout

class ManualInputActivity : AppCompatActivity() {
    private val dateSelector by lazy { findViewById<EditText>(R.id.date_selector) }
    private val timeSelector by lazy { findViewById<EditText>(R.id.time_selector) }
    private val durationSelector by lazy { findViewById<EditText>(R.id.duration_selector) }
    private val distanceSelector by lazy { findViewById<EditText>(R.id.distance_selector) }
    private val caloriesSelector by lazy { findViewById<EditText>(R.id.calories_selector) }
    private val heatRateSelector by lazy { findViewById<EditText>(R.id.heart_rate_selector) }
    private val commentsSelector by lazy { findViewById<EditText>(R.id.comments_selector) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manual_input)
        initializeListeners()
    }

    private fun initializeListeners() {
        dateSelector.setOnClickListener() {

        }
        timeSelector.setOnClickListener() {

        }
        durationSelector.setOnClickListener() {

        }
        distanceSelector.setOnClickListener() {

        }
        caloriesSelector.setOnClickListener() {

        }
        heatRateSelector.setOnClickListener() {

        }
        commentsSelector.setOnClickListener() {

        }
    }

}