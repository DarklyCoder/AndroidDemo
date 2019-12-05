package com.darklycoder.android.demo.gles

import android.opengl.GLSurfaceView
import android.os.Bundle
import android.view.MotionEvent
import com.darklycoder.android.demo.base.BasePage
import com.darklycoder.android.demo.gles.render.*

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

        val render = createRender(8)
        mGlSurfaceView.setRenderer(render)

        mGlSurfaceView.renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY

        mGlSurfaceView.setOnTouchListener { view, motionEvent ->
            if (null == motionEvent) {
                return@setOnTouchListener false
            }

            val normalX = motionEvent.x / view.width * 2 - 1
            val normalY = -(motionEvent.y / view.height * 2 - 1)

            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    mGlSurfaceView.queueEvent { render.handleTouchPress(normalX, normalY) }
                }

                MotionEvent.ACTION_MOVE -> {
                    mGlSurfaceView.queueEvent { render.handleTouchMove(normalX, normalY) }
                }
            }

            true
        }
    }

    override fun onResume() {
        super.onResume()
        mGlSurfaceView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mGlSurfaceView.onPause()
    }

    private fun createRender(type: Int = 1): BaseRender {
        return when (type) {
            2 -> AirHockeyRender(this)
            3 -> AirHockey2Render(this)
            4 -> AirHockey3Render(this)
            5 -> AirHockey3DRender(this)
            6 -> AirHockeyTexturedRender(this)
            7 -> AirHockeyBetterMalletsRender(this)
            8 -> AirHockeyTouchRender(this)
            else -> EmptyRender(this)
        }
    }

}