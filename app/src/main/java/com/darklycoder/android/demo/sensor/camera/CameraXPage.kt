package com.darklycoder.android.demo.sensor.camera

import android.os.Build
import android.os.Bundle
import android.util.Size
import androidx.annotation.RequiresApi
import androidx.camera.core.CameraX
import androidx.camera.core.Preview
import androidx.camera.core.PreviewConfig
import com.darklycoder.android.demo.R
import com.darklycoder.android.demo.base.BasePage
import kotlinx.android.synthetic.main.activity_camera_texture.*

/**
 * 使用 CameraX 框架操作相机
 * <p>基于Camera2 api</p>
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class CameraXPage : BasePage() {

    private var mOpen = false // 相机是否开启
    private val pw = 800
    private val ph = 600

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_texture)

        mBtnToggle.setOnClickListener { toggle() }
        mBtnToggle.performClick()
    }

    /**
     * 切换相机开启/关闭
     */
    private fun toggle() {
        if (mOpen) {
            closeCamera()
            mBtnToggle.text = "打开"
            mOpen = false

        } else {
            openCamera()
            mBtnToggle.text = "关闭"
            mOpen = true
        }
    }

    private fun openCamera() {
        val previewConfig = PreviewConfig.Builder().apply {
            setTargetResolution(Size(pw, ph))
            mCameraView.setAspectRatio(pw, ph)
        }.build()

        val preview = Preview(previewConfig)

        preview.setOnPreviewOutputUpdateListener {
            mCameraView.surfaceTexture = it.surfaceTexture
        }

        CameraX.bindToLifecycle(this, preview)
    }

    private fun closeCamera() {
        CameraX.unbindAll()
    }

}