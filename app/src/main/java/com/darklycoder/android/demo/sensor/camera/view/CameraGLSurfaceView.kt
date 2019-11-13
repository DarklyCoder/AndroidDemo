package com.darklycoder.android.demo.sensor.camera.view

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class CameraGLSurfaceView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : GLSurfaceView(context, attrs) {

    private var mRatioWidth = 0
    private var mRatioHeight = 0
    private var mGLRender: GLRender

    fun setAspectRatio(width: Int, height: Int) {
        if (width <= 0 || height <= 0) {
            return
        }

        mRatioWidth = width
        mRatioHeight = height
        requestLayout()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val w = MeasureSpec.getSize(widthMeasureSpec)
        val h = MeasureSpec.getSize(heightMeasureSpec)

        if (0 == mRatioWidth || 0 == mRatioHeight) {
            setMeasuredDimension(w, h)

        } else {
            setMeasuredDimension(mRatioWidth, mRatioHeight)
        }
    }

    init {
        setEGLContextClientVersion(2)

        mGLRender = GLRender()
        setRenderer(mGLRender)
        renderMode = RENDERMODE_WHEN_DIRTY
    }

}

class GLRender : GLSurfaceView.Renderer {

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {

    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {

    }

    override fun onDrawFrame(gl: GL10?) {

    }

}
