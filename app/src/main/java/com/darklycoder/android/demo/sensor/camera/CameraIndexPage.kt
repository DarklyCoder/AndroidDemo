package com.darklycoder.android.demo.sensor.camera

import android.Manifest.permission.CAMERA
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.annotation.TargetApi
import android.content.Intent
import android.os.Build
import android.os.Bundle
import com.darklycoder.android.demo.R
import com.darklycoder.android.demo.base.BasePage
import kotlinx.android.synthetic.main.activity_camera_index.*

class CameraIndexPage : BasePage() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_index)

        requestPermissions()
        mBtnCamera1.setOnClickListener { startActivity(Intent(this, CameraV1Page::class.java)) }
        mBtnCamera2.setOnClickListener { startActivity(Intent(this, CameraV2Page::class.java)) }
        mBtnCameraX.setOnClickListener { startActivity(Intent(this, CameraXPage::class.java)) }
        mBtnCameraGL.setOnClickListener { startActivity(Intent(this, CameraGLSurfaceViewPage::class.java)) }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun requestPermissions() {
        requestPermissions(arrayOf(WRITE_EXTERNAL_STORAGE, CAMERA), 100)
    }

}