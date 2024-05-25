package com.example.file

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {
    private lateinit var editText: EditText
    private lateinit var btnSave: Button
    private lateinit var btnRead: Button
    private lateinit var btnDelete: Button
    private val fileName = "example.txt"
    private val permissionRequestCode = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editText = findViewById(R.id.editText)
        btnSave = findViewById(R.id.btnSave)
        btnRead = findViewById(R.id.btnRead)
        btnDelete = findViewById(R.id.btnDelete)

        btnSave.setOnClickListener { saveToFile() }
        btnRead.setOnClickListener { readFromFile() }
        btnDelete.setOnClickListener { deleteFile() }

        checkPermissions()
    }

    private fun checkPermissions() {
        val writePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val readPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)

        if (writePermission != PackageManager.PERMISSION_GRANTED || readPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE),
                permissionRequestCode
            )
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == permissionRequestCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permissions granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permissions denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveToFile() {
        if (isExternalStorageWritable()) {
            val text = editText.text.toString()
            if (text.isNotEmpty()) {
                try {
                    val file = File(getExternalFilesDir(null), fileName)
                    val fileOutputStream = FileOutputStream(file)
                    fileOutputStream.write(text.toByteArray())
                    fileOutputStream.close()
                    Toast.makeText(this, "File saved successfully", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this, "Failed to save file", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please enter some text", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "External storage is not writable", Toast.LENGTH_SHORT).show()
        }
    }

    private fun readFromFile() {
        if (isExternalStorageReadable()) {
            try {
                val file = File(getExternalFilesDir(null), fileName)
                val fileInputStream = FileInputStream(file)
                val inputStreamReader = fileInputStream.reader()
                val text = inputStreamReader.readText()
                editText.setText(text)
                inputStreamReader.close()
                fileInputStream.close()
                Toast.makeText(this, "File read successfully", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "Failed to read file", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "External storage is not readable", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteFile() {
        val file = File(getExternalFilesDir(null), fileName)
        if (file.exists()) {
            file.delete()
            editText.setText("")
            Toast.makeText(this, "File deleted successfully", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "File does not exist", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isExternalStorageWritable(): Boolean {
        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
    }

    private fun isExternalStorageReadable(): Boolean {
        return Environment.getExternalStorageState() in
                setOf(Environment.MEDIA_MOUNTED, Environment.MEDIA_MOUNTED_READ_ONLY)
    }
}