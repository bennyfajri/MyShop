package com.drsync.myshop

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.drsync.myshop.databinding.ActivityScanBinding

class ScanActivity : AppCompatActivity() {

    private lateinit var binding : ActivityScanBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            btnBack.setOnClickListener {
                finish()
            }
        }
    }
}