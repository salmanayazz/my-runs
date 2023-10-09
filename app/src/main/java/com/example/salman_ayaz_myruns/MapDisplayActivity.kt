package com.example.salman_ayaz_myruns

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MapDisplayActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_display)

        findViewById<Button>(R.id.confirm).setOnClickListener() {
            finish()
        }
        findViewById<Button>(R.id.cancel).setOnClickListener() {
            finish()
        }
    }
}