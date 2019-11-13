package com.darklycoder.android.demo.sensor.camera

import android.os.Bundle
import com.darklycoder.android.demo.R
import com.darklycoder.android.demo.base.BasePage
import kotlinx.android.synthetic.main.activity_camera.*

/**
 * 使用 GLSurfaceView 作为预览界面
 */
class CameraGLSurfaceViewPage : BasePage() {

    private var mOpen = false // 相机是否开启
    private val mLock = Object()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_gl)

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

    override fun onDestroy() {
        super.onDestroy()
        closeCamera()
    }

    private fun openCamera() {

    }

    private fun closeCamera() {

    }

}