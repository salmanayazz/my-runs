package com.example.myruns

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.core.app.ActivityCompat
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.provider.MediaStore
import android.text.Editable
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import java.io.File
import java.lang.NumberFormatException


class SettingsActivity : AppCompatActivity() {
    private val cameraRequestCode: Int = 1
    private val storageRequestCode: Int = 2

    private lateinit var settingsViewModel: SettingsViewModel

    private val profilePhotoView by lazy { findViewById<ImageView>(R.id.profile_photo) }
    private val nameView by lazy { findViewById<EditText>(R.id.name_input) }
    private val emailView by lazy { findViewById<EditText>(R.id.email_input) }
    private val phoneNumberView by lazy { findViewById<EditText>(R.id.phone_input) }
    private val classYearView by lazy { findViewById<EditText>(R.id.class_input) }
    private val majorView by lazy { findViewById<EditText>(R.id.major_input) }
    private val genderViewFemale by lazy { findViewById<RadioButton>(R.id.radio_gender_female) }
    private val genderViewMale by lazy { findViewById<RadioButton>(R.id.radio_gender_male) }
    private val genderViewOther by lazy { findViewById<RadioButton>(R.id.radio_gender_other) }

    private val imageCaptureLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) { // successfully captured photo
            val data: Intent? = result.data
            if (data != null && data.extras != null) {
                val imageBitmap = data.extras!!.get("data") as Bitmap // TODO: change to something not deprecated
                settingsViewModel.profilePhoto.value = imageBitmap
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        settingsViewModel = ViewModelProvider(this)[SettingsViewModel::class.java]
        settingsViewModel.load(this)

        initializeObservers()


        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), storageRequestCode)

        findViewById<Button>(R.id.profile_photo_button).setOnClickListener {

            // show popup to ask for camera permissions, if successful, launch camera
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                cameraRequestCode
            )
        }

        findViewById<Button>(R.id.confirm_settings).setOnClickListener {
            saveSettings()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == cameraRequestCode) { // check if camera permissions granted
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) { // granted
                launchCamera()
            } else { // denied
                Toast.makeText(
                    this,
                    "Please enable camera permissions",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun launchCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        try {
            imageCaptureLauncher.launch(intent)
        } catch (e: Exception) {
            e.printStackTrace();
        }
    }

    private fun initializeObservers() {
        settingsViewModel.profilePhoto.observe(this) {
            profilePhotoView.setImageBitmap(it)
        }
        settingsViewModel.name.observe(this) {
            nameView.text = Editable.Factory.getInstance().newEditable(it)
        }
        settingsViewModel.email.observe(this) {
            emailView.text = Editable.Factory.getInstance().newEditable(it)
        }
        settingsViewModel.gender.observe(this) {
            when (it){
                0 -> genderViewFemale.isChecked = true;
                1 -> genderViewMale.isChecked = true;
                2 -> genderViewOther.isChecked = true;
            }
        }
        settingsViewModel.phoneNumber.observe(this) {
            phoneNumberView.text = Editable.Factory.getInstance().newEditable(it)
        }
        settingsViewModel.classYear.observe(this) {
            classYearView.text = Editable.Factory.getInstance().newEditable(it.toString())
        }
        settingsViewModel.major.observe(this) {
            majorView.text = Editable.Factory.getInstance().newEditable(it)
        }
    }

    private fun saveSettings() {
        settingsViewModel.name.value = nameView.text.toString()
        settingsViewModel.email.value = emailView.text.toString()
        settingsViewModel.phoneNumber.value = phoneNumberView.text.toString()
        try {
            settingsViewModel.classYear.value = (classYearView.text.toString()).toInt()
        } catch (e: NumberFormatException) {
            e.printStackTrace()
            Toast.makeText(
                this,
                "Class year number is too large", // TODO
                Toast.LENGTH_SHORT
            ).show()
        }

        settingsViewModel.major.value = majorView.text.toString()

        if (genderViewFemale.isChecked) {
            settingsViewModel.gender.value = 0;
        } else if (genderViewMale.isChecked) {
            settingsViewModel.gender.value = 1;
        } else if (genderViewOther.isChecked) {
            settingsViewModel.gender.value = 2;
        }

        settingsViewModel.save(this)
    }
}