package com.darklycoder.android.demo.sensor.camera

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.ImageFormat
import android.hardware.camera2.*
import android.media.ImageReader
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.util.Size
import android.view.Surface
import android.view.SurfaceHolder
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.darklycoder.android.demo.R
import com.darklycoder.android.demo.base.BasePage
import kotlinx.android.synthetic.main.activity_camera.*

/**
 * 使用 Camera2 的api操作相机
 *
 * <p>有兼容性问题，使用前先判断 HAL 版本</p>
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class CameraV2Page : BasePage() {

    companion object {
        private const val TAG = "CameraV2Page"
    }

    private lateinit var mCameraManager: CameraManager
    private var mCameraDevice: CameraDevice? = null
    private var mSurface: Surface? = null
    private var mPreviewSession: CameraCaptureSession? = null
    private var mOpen = false // 相机是否开启
    private val mLock = Object()
    private var mRequestBuilder: CaptureRequest.Builder? = null
    private lateinit var mImageReader: ImageReader
    private lateinit var mPreviewSize: Size
    private lateinit var mHandler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        mCameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager

        val handlerThread = HandlerThread("CameraThread")
        handlerThread.start()
        mHandler = Handler(handlerThread.looper)

        mPreviewSize = getPreviewSize(SurfaceHolder::class.java) ?: return
        mCameraView.setAspectRatio(mPreviewSize.width, mPreviewSize.height)
        mCameraView.holder.setFixedSize(mPreviewSize.width, mPreviewSize.height)

        mCameraView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                if (null != mSurface && null != mCameraDevice) {
                    // 停止预览
                    stopPreview()
                    mSurface = null
                }

                mSurface = holder.surface
                setPreview(mCameraDevice, mSurface)
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
                stopPreview()
                mSurface = null
            }
        })

        mBtnToggle.setOnClickListener { toggle() }
        mBtnToggle.performClick()
    }

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

    /**
     * 获取预览尺寸
     *
     * <p>获取的尺寸和 Camera1 不完全相同，可能会导致相机预览界面拉伸</p>
     */
    private fun <T> getPreviewSize(
        cls: Class<T>,
        facing: Int = CameraCharacteristics.LENS_FACING_BACK
    ): Size? {
        synchronized(mLock) {
            try {
                var cameraInfo: CameraCharacteristics
                val list = mCameraManager.cameraIdList

                list.forEach {
                    cameraInfo = mCameraManager.getCameraCharacteristics(it)
                    if (cameraInfo.get(CameraCharacteristics.LENS_FACING) == facing) {
                        val configMap =
                            cameraInfo.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
                        val sizeList = configMap?.getOutputSizes(cls)

                        val level =
                            cameraInfo.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL)

                        Toast.makeText(this, "Camera2 level: $level", Toast.LENGTH_SHORT).show()

                        if (level == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY) {
                            Toast.makeText(this, "版本过低，推荐使用Camera1", Toast.LENGTH_SHORT).show()
                        }
                        return getMatchSize(sizeList)
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return null
    }

    private fun getMatchSize(sizeList: Array<Size>?): Size? {
        if (sizeList.isNullOrEmpty()) {
            return null
        }

        // TODO 获取最合适的尺寸

        return sizeList[0]
    }

    /**
     * 找到相机id
     */
    private fun findCameraId(facing: Int = CameraCharacteristics.LENS_FACING_BACK): String? {
        synchronized(mLock) {
            try {
                var cameraInfo: CameraCharacteristics
                val list = mCameraManager.cameraIdList

                list.forEach {
                    cameraInfo = mCameraManager.getCameraCharacteristics(it)
                    if (cameraInfo.get(CameraCharacteristics.LENS_FACING) == facing) {
                        return it
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }

            return null
        }
    }

    /**
     * 打开相机
     */
    @SuppressLint("MissingPermission")
    private fun openCamera(facing: Int = CameraCharacteristics.LENS_FACING_BACK) {
        synchronized(mLock) {
            val cameraId = findCameraId(facing) ?: return

            try {
                mCameraManager.openCamera(cameraId, mCameraStateCallback, mHandler)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private val mCameraStateCallback = object : CameraDevice.StateCallback() {
        override fun onOpened(camera: CameraDevice) {
            Log.d(TAG, "camera onOpened")

            mCameraDevice = camera

            mImageReader = ImageReader.newInstance(
                mPreviewSize.width,
                mPreviewSize.height,
                ImageFormat.YUV_420_888,
                2
            ).apply {
                setOnImageAvailableListener(mImageAvailableListener, mHandler)
            }

            setPreview(mCameraDevice, mSurface)
        }

        override fun onDisconnected(camera: CameraDevice) {
            Log.d(TAG, "camera onDisconnected")
        }

        override fun onError(camera: CameraDevice, error: Int) {
            Log.e(TAG, "camera onError：$error")
        }
    }

    private val mImageAvailableListener = ImageReader.OnImageAvailableListener {
        Log.d(TAG, "camera OnImageAvailableListener")

        val image = it.acquireLatestImage()
        image.close()
    }

    private fun closeCamera() {
        synchronized(mLock) {
            try {
                stopPreview()
                mImageReader.close()
                mCameraDevice?.close()

            } catch (e: Exception) {
                e.printStackTrace()

            } finally {
                mCameraDevice = null
            }
        }
    }

    /**
     * 开启预览
     */
    private fun setPreview(cameraDevice: CameraDevice?, surface: Surface?) {
        synchronized(mLock) {
            try {
                if (null == cameraDevice || null == surface) {
                    return
                }

                mRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
                mRequestBuilder?.addTarget(surface)
                mRequestBuilder?.addTarget(mImageReader.surface)

                cameraDevice.createCaptureSession(
                    arrayListOf(surface, mImageReader.surface),
                    mCaptureSessionStateCallback,
                    mHandler
                )

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private val mCaptureSessionStateCallback = object : CameraCaptureSession.StateCallback() {
        override fun onConfigureFailed(session: CameraCaptureSession) {
            Log.d(TAG, "camera onConfigureFailed")
        }

        override fun onConfigured(session: CameraCaptureSession) {
            mPreviewSession = session

            val request = mRequestBuilder?.build() ?: return
            session.setRepeatingRequest(request, null, null)
        }
    }

    private fun stopPreview() {
        synchronized(mLock) {
            try {
                mPreviewSession?.stopRepeating()

            } catch (e: Exception) {
                e.printStackTrace()

            } finally {
                mPreviewSession = null
            }
        }
    }

}