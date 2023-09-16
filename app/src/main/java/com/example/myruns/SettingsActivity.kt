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
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import java.io.File


class SettingsActivity : AppCompatActivity() {
    private val cameraRequestCode: Int = 1
    private val storageRequestCode: Int = 2

    private val imageCaptureLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) { // successfully captured photo
            val data: Intent? = result.data
            if (data != null && data.extras != null) {
                val imageBitmap = data.extras!!.get("data") as Bitmap // TODO: change to something not deprecated
                setProfileImage(imageBitmap)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), storageRequestCode)

        findViewById<Button>(R.id.profile_photo_button).setOnClickListener {

            // show popup to ask for camera permissions, if successful, launch camera
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                cameraRequestCode
            )
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
        } else if (requestCode == storageRequestCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(
                    this,
                    "Storage Permission Granted",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    this,
                    "Storage Permission Denied",
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

        // save the image
        val imageFileName = "profile_image.jpg"
        val imageFile = File(getExternalFilesDir(null), imageFileName)
//
//        val tempImgFile = File(getExternalFilesDir(null), "profile_image.jpg")
//        val tempImgUri = FileProvider.getUriForFile(this, "com.example.myruns", tempImgFile)
//
//        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, tempImgUri)
//        cameraResult.launch(intent)
    }
}