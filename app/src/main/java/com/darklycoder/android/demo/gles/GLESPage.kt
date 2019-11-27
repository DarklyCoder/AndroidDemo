package com.darklycoder.android.demo.gles

import android.opengl.GLSurfaceView
import android.os.Bundle
import com.darklycoder.android.demo.base.BasePage
import com.darklycoder.android.demo.gles.render.AirHockeyTexturedRender

/**
 * GLES demo
 */
class GLESPage : BasePage() {

    private lateinit var mGlSurfaceView: GLSurfaceView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mGlSurfaceView = GLSurfaceView(this)
        setContentView(mGlSurfaceView)

        initParams()
    }

    private fun initParams() {
        mGlSurfaceView.setEGLContextClientVersion(2)
        mGlSurfaceView.setRenderer(AirHockeyTexturedRender(this))
        mGlSurfaceView.renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
    }

    override fun onResume() {
        super.onResume()
        mGlSurfaceView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mGlSurfaceView.onPause()
    }

}