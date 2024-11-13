package com.boniantech.handlingpermissions

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val button: Button = findViewById(R.id.button)
        button.setOnClickListener {
            askForPermissions()
        }
    }

    private fun askForPermissions() {
        val neededPermissions = mutableListOf<String>()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_MEDIA_VIDEO
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                neededPermissions.add(Manifest.permission.READ_MEDIA_VIDEO)
            }

            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_MEDIA_IMAGES
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                neededPermissions.add(Manifest.permission.READ_MEDIA_IMAGES)
            }

            if (neededPermissions.isNotEmpty()) {
                requestPermissions(
                    neededPermissions.toTypedArray(),
                    READ_MEDIA_IMAGE_VIDEO_REQUEST_CODE
                )
            } else {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.READ_MEDIA_AUDIO
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    requestPermissions(
                        arrayOf(Manifest.permission.READ_MEDIA_AUDIO),
                        READ_MEDIA_AUDIO_REQUEST_CODE
                    )
                } else {
                    Toast.makeText(baseContext, "Permissions granted", Toast.LENGTH_SHORT).show()
                }
            }
        } else if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                != PackageManager.PERMISSION_GRANTED
            ) {
                neededPermissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }

            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
                != PackageManager.PERMISSION_GRANTED
            ) {
                neededPermissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }

            if (neededPermissions.isNotEmpty()) {
                requestPermissions(
                    neededPermissions.toTypedArray(),
                    READ_WRITE_FILE_REQUEST_CODE
                )
            } else {
                Toast.makeText(baseContext, "Permissions granted", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(baseContext, "Permissions granted", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        try {
            when (requestCode) {
                READ_MEDIA_IMAGE_VIDEO_REQUEST_CODE -> if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    if (ActivityCompat.checkSelfPermission(
                            this,
                            Manifest.permission.READ_MEDIA_AUDIO
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        requestPermissions(
                            arrayOf(Manifest.permission.READ_MEDIA_AUDIO),
                            READ_MEDIA_AUDIO_REQUEST_CODE
                        )
                    } else {
                        Toast.makeText(baseContext, "Permissions granted", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    showPermissionNeededDialog()
                }

                READ_MEDIA_AUDIO_REQUEST_CODE -> if ((grantResults.isNotEmpty() &&
                            grantResults.contains(PackageManager.PERMISSION_GRANTED))) {
                    Toast.makeText(baseContext, "Permissions granted", Toast.LENGTH_SHORT).show()
                } else {
                    showPermissionNeededDialog()
                }

                READ_WRITE_FILE_REQUEST_CODE -> if ((grantResults.isNotEmpty() &&
                            grantResults.contains(PackageManager.PERMISSION_GRANTED))) {
                    Toast.makeText(baseContext, "Permissions granted", Toast.LENGTH_SHORT).show()
                } else {
                    showPermissionNeededDialog()
                }

                else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        } catch (e: Exception) {
            Toast.makeText(baseContext, e.localizedMessage, Toast.LENGTH_LONG).show()
        }
    }

    private fun showPermissionNeededDialog() {
        val alertDialog =
            AlertDialog.Builder(this)
                .setTitle("Permission needed")
                .setMessage("Is it ok to open the settings screen so that you can grant the permission?")
                .setCancelable(false)
                .setPositiveButton("Settings Screen") { dialog, _ ->
                    dialog.dismiss()
                    val intent: Intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    intent.setData(Uri.parse("package:" + "com.boniantech.handlingpermissions"))
                    startActivity(intent)
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()

        alertDialog.show()
    }

    companion object {
        private const val READ_MEDIA_IMAGE_VIDEO_REQUEST_CODE = 1
        private const val READ_MEDIA_AUDIO_REQUEST_CODE = 2
        private const val READ_WRITE_FILE_REQUEST_CODE = 3
    }
}