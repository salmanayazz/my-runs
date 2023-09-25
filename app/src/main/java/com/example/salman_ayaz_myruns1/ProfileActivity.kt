package com.example.salman_ayaz_myruns1

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.button.MaterialButton


class ProfileActivity : AppCompatActivity() {
    private val cameraRequestCode: Int = 1
    private val storageRequestCode: Int = 2

    private lateinit var profileViewModel: ProfileViewModel

    private val profilePhotoView by lazy { findViewById<ImageView>(R.id.profile_photo) }
    private val nameView by lazy { findViewById<EditText>(R.id.name_input) }
    private val emailView by lazy { findViewById<EditText>(R.id.email_input) }
    private val phoneNumberView by lazy { findViewById<EditText>(R.id.phone_input) }
    private val classYearView by lazy { findViewById<EditText>(R.id.class_input) }
    private val majorView by lazy { findViewById<EditText>(R.id.major_input) }
    private val genderViewFemale by lazy { findViewById<MaterialButton>(R.id.radio_gender_female) }
    private val genderViewMale by lazy { findViewById<MaterialButton>(R.id.radio_gender_male) }
    private val genderViewOther by lazy { findViewById<MaterialButton>(R.id.radio_gender_other) }

    private val imageCaptureLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) { // successfully captured photo
            val data: Intent? = result.data
            if (data != null && data.extras != null) {
                val imageBitmap = data.extras!!.get("data") as Bitmap // TODO: change to something not deprecated
                profileViewModel.profilePhoto.value = imageBitmap
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), storageRequestCode)

        profileViewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
        loadProfile()

        initializeObservers()
        initializeListeners()
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

    private fun initializeListeners() {
        // button listeners
        findViewById<Button>(R.id.profile_photo_button).setOnClickListener {
            // show popup to ask for camera permissions, if successful, launch camera
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                cameraRequestCode
            )
        }
        findViewById<Button>(R.id.confirm_settings).setOnClickListener {
            saveProfile()
            finish()
        }
        findViewById<Button>(R.id.cancel_settings).setOnClickListener {
            finish()
        }
    }

    private fun initializeObservers() {
        profileViewModel.profilePhoto.observe(this) {
            profilePhotoView.setImageBitmap(it)
        }
        profileViewModel.name.observe(this) {
            nameView.text = Editable.Factory.getInstance().newEditable(it)
        }
        profileViewModel.email.observe(this) {
            emailView.text = Editable.Factory.getInstance().newEditable(it)
        }
        profileViewModel.gender.observe(this) {
            when (it){
                0 -> genderViewFemale.isChecked = true;
                1 -> genderViewMale.isChecked = true;
                2 -> genderViewOther.isChecked = true;
            }
        }
        profileViewModel.phoneNumber.observe(this) {
            phoneNumberView.text = Editable.Factory.getInstance().newEditable(it)
        }
        profileViewModel.classYear.observe(this) {
            classYearView.text = Editable.Factory.getInstance().newEditable(it?.toString() ?: "")
        }
        profileViewModel.major.observe(this) {
            majorView.text = Editable.Factory.getInstance().newEditable(it)
        }
    }

    private fun saveProfile() {
        profileViewModel.name.value = nameView.text.toString()
        profileViewModel.email.value = emailView.text.toString()
        profileViewModel.phoneNumber.value = phoneNumberView.text.toString()
        try {
            profileViewModel.classYear.value = (classYearView.text.toString()).toInt()
        } catch (e: NumberFormatException) {
            profileViewModel.classYear.value = null;
            e.printStackTrace()
        }

        profileViewModel.major.value = majorView.text.toString()

        if (genderViewFemale.isChecked) {
            profileViewModel.gender.value = 0
        } else if (genderViewMale.isChecked) {
            profileViewModel.gender.value = 1
        } else if (genderViewOther.isChecked) {
            profileViewModel.gender.value = 2
        } else {
            profileViewModel.gender.value = null
        }

        profileViewModel.save(this)
    }

    private fun loadProfile() {
        profileViewModel.load(this)
    }
}