package com.drsync.myshop


import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.drsync.myshop.databinding.ActivityScanBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class ScanActivity : AppCompatActivity() {

    private lateinit var codeScanner: CodeScanner
    private var hasCameraPermissionGranted: Boolean = false
    private lateinit var binding: ActivityScanBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //get camera permisssion
        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)

        binding.apply {
            btnBack.setOnClickListener {
                finish()
            }
        }

        codeScanner = CodeScanner(this, binding.scanView)

        codeScanner.apply {
            camera = CodeScanner.CAMERA_BACK
            formats = CodeScanner.ALL_FORMATS
            autoFocusMode = AutoFocusMode.SAFE
            scanMode = ScanMode.SINGLE
            isAutoFocusEnabled = true
            isFlashEnabled = false
            startPreview()
        }

        codeScanner.setDecodeCallback {
            runOnUiThread {
                val intent = Intent()
                intent.putExtra(Constant.SCAN_RESULT, it.text)
                setResult(Constant.SCAN_RESULT_CODE, intent)
                finish()
            }
        }

        codeScanner.errorCallback = ErrorCallback {
            runOnUiThread {
                Toast.makeText(
                    this,
                    "Camera initialization error: ${it.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private val cameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            hasCameraPermissionGranted = isGranted
            if(!isGranted) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                    showNotificationPermissionRationale()
                } else {
                    showSettingDialog()
                }
            }
        }

    private fun showSettingDialog() {
        MaterialAlertDialogBuilder(
            this,
            com.google.android.material.R.style.MaterialAlertDialog_Material3
        )
            .setTitle(getString(R.string.camera_permission))
            .setMessage(getString(R.string.camera_permission_message))
            .setPositiveButton(getString(R.string.ok)) { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.parse("package:$packageName")
                startActivity(intent)
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

    private fun showNotificationPermissionRationale() {
        MaterialAlertDialogBuilder(
            this,
            com.google.android.material.R.style.MaterialAlertDialog_Material3
        )
            .setTitle(getString(R.string.attention))
            .setMessage(getString(R.string.camera_rationale_message))
            .setPositiveButton(getString(R.string.ok)) { _, _ ->
                if (Build.VERSION.SDK_INT >= 33) {
                    cameraPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

    override fun onResume() {
        super.onResume()
        if (hasCameraPermissionGranted) {
            codeScanner.startPreview()
        }
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }
}