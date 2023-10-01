package com.example.salman_ayaz_myruns

import android.Manifest
import android.R.attr.height
import android.R.attr.width
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import java.io.File
import java.io.FileOutputStream
import java.lang.NumberFormatException


class ProfileActivity : AppCompatActivity() {
    private val cameraRequestCode: Int = 1
    private val storageRequestCode: Int = 2

    private lateinit var profileViewModel: ProfileViewModel

    private lateinit var profileSharedPrefs: SharedPreferences

    private val profilePhotoView by lazy { findViewById<ImageView>(R.id.profile_photo) }
    private val nameView by lazy { findViewById<EditText>(R.id.name_input) }
    private val emailView by lazy { findViewById<EditText>(R.id.email_input) }
    private val phoneNumberView by lazy { findViewById<EditText>(R.id.phone_input) }
    private val classYearView by lazy { findViewById<EditText>(R.id.class_input) }
    private val majorView by lazy { findViewById<EditText>(R.id.major_input) }
    private val genderViewFemale by lazy { findViewById<RadioButton>(R.id.radio_gender_female) }
    private val genderViewMale by lazy { findViewById<RadioButton>(R.id.radio_gender_male) }

    private val profilePhotoFileName = "profile_photo.jpg"
    private val tempProfilePhotoFileName = "profile_photo_temp.jpg"
    private lateinit var tempProfilePhotoUri : Uri

    private val imageCaptureLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            // put temp image in the view model
            profileViewModel.profilePhoto.value = BitmapFactory.decodeStream(
                contentResolver.openInputStream(tempProfilePhotoUri)
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), storageRequestCode)

        profileViewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
        profileSharedPrefs = getSharedPreferences("profile", MODE_PRIVATE)

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
        val imgFile = File(getExternalFilesDir(null), tempProfilePhotoFileName)
        tempProfilePhotoUri = FileProvider.getUriForFile(
            this,
            "com.example.salman_ayaz_myruns",
            imgFile
        )

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, tempProfilePhotoUri) // where to store the temp photo data

        try {
            imageCaptureLauncher.launch(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(
                this,
                "Failed to launch camera: ${e.message}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // setup button listeners
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
            if (saveProfile()) {
                finish()
            }
        }
        findViewById<Button>(R.id.cancel_settings).setOnClickListener {
            finish()
        }
    }

    // observers for ViewModel
    private fun initializeObservers() {
        profileViewModel.profilePhoto.observe(this) {
            profilePhotoView.setImageBitmap(it)
        }
    }

    // saves the profile to file
    private fun saveProfile(): Boolean {
        try {
            val editor = profileSharedPrefs.edit()
            editor.putString("name", nameView.text.toString())
            editor.putString("email", emailView.text.toString())
            editor.putString("phoneNumber", phoneNumberView.text.toString())

            editor.putString("major", majorView.text.toString())

            // remove classYear entry if the input is null
            // allows to empty the input field instead of having a default val
            if (classYearView.text.toString() == "") {
                editor.remove("classYear")
            } else {
                try {
                    editor.putInt("classYear", classYearView.text.toString().toInt())
                } catch(e: NumberFormatException) {
                    e.printStackTrace()
                    Toast.makeText(
                        this,
                        "Invalid class year",
                        Toast.LENGTH_SHORT
                    ).show()
                    return false
                }

            }

            val gender = when {
                genderViewFemale.isChecked -> 0
                genderViewMale.isChecked -> 1
                else -> -1 // none selected
            }
            editor.putInt("gender", gender)


            // save image to file
            val profilePhotoFile = File(getExternalFilesDir(null), profilePhotoFileName)
            if (profileViewModel.profilePhoto.value != null) {
                val imageBitmap = profileViewModel.profilePhoto.value
                val profilePhotoOutputStream = FileOutputStream(profilePhotoFile)
                imageBitmap?.compress(Bitmap.CompressFormat.JPEG, 100, profilePhotoOutputStream)
                profilePhotoOutputStream.close()
            }

            editor.apply()
        } catch (e: Exception)  {
            e.printStackTrace()
            e.printStackTrace()
            Toast.makeText(
                this,
                "There was an error saving settings",
                Toast.LENGTH_SHORT
            ).show()
            return false;
        }
        Toast.makeText(
            this,
            "Successfully saved settings",
            Toast.LENGTH_SHORT
        ).show()
        return true;
    }


    // loads the profile from file
    private fun loadProfile() {
        try {
            // retrieve input values from shared pref
            nameView.setText(profileSharedPrefs.getString("name", ""))
            emailView.setText(profileSharedPrefs.getString("email", ""))
            phoneNumberView.setText(profileSharedPrefs.getString("phoneNumber", ""))
            // check if classYear exists to set the text to null if not present
            if (profileSharedPrefs.contains("classYear")) {
                classYearView.setText(profileSharedPrefs.getInt("classYear", 0).toString())
            } else {
                classYearView.text = null
            }
            majorView.setText(profileSharedPrefs.getString("major", ""))
            when (profileSharedPrefs.getInt("gender", -1)) {
                0 -> genderViewFemale.isChecked = true
                1 -> genderViewMale.isChecked = true
            }
            // load image from file
            val profilePhotoFile = File(getExternalFilesDir(null), profilePhotoFileName)
            if (profilePhotoFile.exists()) {
                val imageBitmap = BitmapFactory.decodeFile(profilePhotoFile.absolutePath)
                profileViewModel.profilePhoto.value = imageBitmap
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(
                this,
                "There was an error loading settings",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}