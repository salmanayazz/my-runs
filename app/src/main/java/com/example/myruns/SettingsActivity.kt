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
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import java.io.File


class SettingsActivity : AppCompatActivity() {
    private val cameraRequestCode: Int = 1
    private val storageRequestCode: Int = 2

    private lateinit var settingsViewModel: SettingsViewModel

    private lateinit var profilePhotoView: ImageView
    private lateinit var nameView: EditText
//    private lateinit var emailView: EditText
//    private lateinit var phoneNumberView: EditText
//    private lateinit var classYearView: EditText
//    private lateinit var majorView: EditText

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

        initializeViews()
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

    private fun setProfileImage(image: Bitmap) {
        var profilePhoto: ImageView = findViewById(R.id.profile_photo)
        profilePhoto.setImageBitmap(image)
    }

    private fun initializeViews() {
        profilePhotoView = findViewById(R.id.profile_photo)
        nameView = findViewById(R.id.name_input)
//        emailView = findViewById(R.id.email_input)
//        phoneNumberView = findViewById(R.id.phone_input)
//        classYearView = findViewById(R.id.class_input)
//        majorView = findViewById(R.id.major_input)
    }

    private fun initializeObservers() {
        settingsViewModel.profilePhoto.observe(this) {
            profilePhotoView.setImageBitmap(it)
        }
        settingsViewModel.name.observe(this) {
            nameView.text = Editable.Factory.getInstance().newEditable(it)
        }
        settingsViewModel.email.observe(this) {
            
        }
        settingsViewModel.gender.observe(this) {

        }
        settingsViewModel.phoneNumber.observe(this) {

        }
        settingsViewModel.classYear.observe(this) {

        }
        settingsViewModel.major.observe(this) {

        }
    }

    private fun saveSettings() {
        settingsViewModel.name.value = nameView.text.toString()
//        settingsViewModel.email
//        settingsViewModel.phoneNumber
//        settingsViewModel.classYear
//        settingsViewModel.major

        settingsViewModel.save(this)
    }

}