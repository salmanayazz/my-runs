package com.example.salman_ayaz_myruns1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main);

        val settings = Intent(this, ProfileActivity::class.java);
        startActivity(settings);
    }
}