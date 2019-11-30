package com.darklycoder.android.demo.gles

import android.opengl.GLSurfaceView
import android.os.Bundle
import android.view.MotionEvent
import com.darklycoder.android.demo.base.BasePage
import com.darklycoder.android.demo.gles.render.AirHockeyTouchRender
import com.darklycoder.android.demo.gles.render.BaseRender

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

        val render = createRender()
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

    private fun createRender(): BaseRender {
//        return EmptyRender(this)
//        return AirHockeyRender(this)
//        return AirHockey2Render(this)
//        return AirHockey3Render(this)
//        return AirHockey3DRender(this)
//        return AirHockeyTexturedRender(this)
//        return AirHockeyBetterMalletsRender(this)
        return AirHockeyTouchRender(this)
    }

}