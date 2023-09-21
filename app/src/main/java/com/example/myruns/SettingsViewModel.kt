package com.example.myruns

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter

class SettingsViewModel : ViewModel() {
    val profilePhoto = MutableLiveData<Bitmap>()
    val name = MutableLiveData<String>()
    val email = MutableLiveData<String>()
    val gender = MutableLiveData<Int>()
    val phoneNumber = MutableLiveData<String>()
    val classYear = MutableLiveData<Int>()
    val major = MutableLiveData<String>()

    private val authority = "com.example.myruns"
    private val profilePhotoFileName = "profile_photo.jpg"
    private val settingsFileName = "settings.txt" // holds all other info



    fun save(context: Context) {
        try {
            val settingsFile = File(context.getExternalFilesDir(null), settingsFileName)
            val outputStream = FileOutputStream(settingsFile)
            val writer = BufferedWriter(OutputStreamWriter(outputStream))

            // save settings data
            writer.write("name: ${name.value}\n")
            writer.write("email: ${email.value}\n")
            writer.write("gender: ${gender.value}\n")
            writer.write("phoneNumber: ${phoneNumber.value}\n")
            writer.write("classYear: ${classYear.value}\n")
            writer.write("major: ${major.value}\n")

            writer.close()
            outputStream.close()

            // save the photo to a separate file
            val profilePhotoFile = File(context.getExternalFilesDir(null), profilePhotoFileName)
            val profilePhotoOutputStream = FileOutputStream(profilePhotoFile)
            profilePhoto.value?.compress(Bitmap.CompressFormat.JPEG, 100, profilePhotoOutputStream)
            profilePhotoOutputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(
                context,
                "There was an error saving settings",
                Toast.LENGTH_SHORT
            ).show()
        }
        Toast.makeText(
            context,
            "success",
            Toast.LENGTH_SHORT
        ).show()
    }

    fun load(context: Context) {
        try {
            val settingsFile = File(context.getExternalFilesDir(null), settingsFileName)
            if (settingsFile.exists()) {
                val inputStream = FileInputStream(settingsFile)
                val reader = BufferedReader(InputStreamReader(inputStream))

                name.value = getValueFromLine(reader.readLine(), "name: ")
                println("loaded val is ${name.value}")
                email.value = getValueFromLine(reader.readLine(), "email: ")
                gender.value = getIntValueFromLine(reader.readLine(), "gender: ")
                phoneNumber.value = getValueFromLine(reader.readLine(), "phoneNumber: ")
                classYear.value = getIntValueFromLine(reader.readLine(), "classYear: ")
                major.value = getValueFromLine(reader.readLine(), "major: ")

                reader.close()
                inputStream.close()
            } else {
                println("file no exist")
            }

            val profilePhotoFile = File(context.getExternalFilesDir(null), profilePhotoFileName)
            if (profilePhotoFile.exists()) {
                val profilePhotoBitmap = BitmapFactory.decodeFile(profilePhotoFile.absolutePath)
                profilePhoto.value = profilePhotoBitmap
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(
                context,
                "There was an error loading settings",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun getValueFromLine(line: String?, prefix: String): String? {
        return line?.substringAfter(prefix)
    }

    private fun getIntValueFromLine(line: String?, prefix: String): Int? {
        return try {
            line?.substringAfter(prefix)?.toInt()
        } catch (e: NumberFormatException) {
            null
        }
    }
}