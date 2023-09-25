package com.example.salman_ayaz_myruns1

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import java.io.File
import java.io.FileOutputStream


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
    private val genderViewFemale by lazy { findViewById<MaterialButton>(R.id.radio_gender_female) }
    private val genderViewMale by lazy { findViewById<MaterialButton>(R.id.radio_gender_male) }
    private val genderViewOther by lazy { findViewById<MaterialButton>(R.id.radio_gender_other) }

    private val profilePhotoFileName = "profile_photo.jpg"

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
    }

    private fun saveProfile() {
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
                editor.putInt("classYear", classYearView.text.toString().toInt())
            }

            val gender = when {
                genderViewFemale.isChecked -> 0
                genderViewMale.isChecked -> 1
                genderViewOther.isChecked -> 2
                else -> -1 // none selected
            }
            editor.putInt("gender", gender)

            editor.apply()


            // save image to file
            if (profileViewModel.profilePhoto.value != null) { // don't create img file if null
                val profilePhotoFile = File(getExternalFilesDir(null), profilePhotoFileName)
                val profilePhotoOutputStream = FileOutputStream(profilePhotoFile)
                profileViewModel.profilePhoto.value?.compress(
                    Bitmap.CompressFormat.JPEG,
                    100,
                    profilePhotoOutputStream
                )
                profilePhotoOutputStream.close()
            }
        } catch (e: Exception)  {
            e.printStackTrace()
            e.printStackTrace()
            Toast.makeText(
                this,
                "There was an error saving settings",
                Toast.LENGTH_SHORT
            ).show()
        }
        Toast.makeText(
            this,
            "Successfully saved settings",
            Toast.LENGTH_SHORT
        ).show()
    }

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
                2 -> genderViewOther.isChecked = true
            }
            // load image from file
            val profilePhotoFile = File(getExternalFilesDir(null), profilePhotoFileName)
            if (profilePhotoFile.exists()) {
                profileViewModel.profilePhoto.value = BitmapFactory.decodeFile(
                    profilePhotoFile.absolutePath
                )
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