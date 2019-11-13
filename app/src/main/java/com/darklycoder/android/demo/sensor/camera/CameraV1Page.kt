package com.darklycoder.android.demo.sensor.camera

import android.hardware.Camera
import android.os.Bundle
import android.view.SurfaceHolder
import com.darklycoder.android.demo.R
import com.darklycoder.android.demo.base.BasePage
import kotlinx.android.synthetic.main.activity_camera.*

/**
 * 使用 Camera1 的api操作相机
 */
class CameraV1Page : BasePage() {

    private var mCamera: Camera? = null
    private var mSurface: SurfaceHolder? = null
    private var mOpen = false // 相机是否开启
    private val mLock = Object()

    private val pw = 1270
    private val ph = 720

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        mCameraView.holder.setFixedSize(pw, ph)

        mCameraView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                if (null != mSurface && null != mCamera) {
                    // 停止预览
                    stopPreview(mCamera)
                    mSurface = null
                }

                mSurface = holder
                setPreview(mCamera, mSurface)
            }

            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {

            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                // 停止预览
                stopPreview(mCamera)
                mSurface = null
            }
        })

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
            setPreview(mCamera, mSurface)
            mBtnToggle.text = "关闭"
            mOpen = true
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        closeCamera()
    }

    private fun openCamera(facing: Int = Camera.CameraInfo.CAMERA_FACING_BACK) {
        synchronized(mLock) {
            try {
                val num = Camera.getNumberOfCameras()
                val cameraInfo = Camera.CameraInfo()
                for (i in 0 until num) {
                    Camera.getCameraInfo(i, cameraInfo)
                    if (cameraInfo.facing == facing) {
                        mCamera = Camera.open(facing)
                        break
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun closeCamera() {
        synchronized(mLock) {
            try {
                mCamera?.setErrorCallback(null)
                mCamera?.setPreviewCallbackWithBuffer(null)
                mCamera?.stopPreview()
                mCamera?.release()

            } catch (e: Exception) {
                e.printStackTrace()

            } finally {
                mCamera = null
            }
        }
    }

    /**
     * 设置预览
     */
    private fun setPreview(camera: Camera?, surface: SurfaceHolder?) {
        synchronized(mLock) {
            if (null == camera || null == surface) {
                return
            }

            try {
                camera.parameters?.apply {
                    setPreviewSize(pw, ph)
                    camera.parameters = this
                }

                camera.setPreviewDisplay(surface)
                camera.startPreview()

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 停止预览
     */
    private fun stopPreview(camera: Camera?) {
        synchronized(mLock) {
            if (null == camera) {
                return
            }

            try {
                camera.setPreviewCallbackWithBuffer(null)
                camera.stopPreview()

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}