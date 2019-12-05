package com.darklycoder.android.demo.gles.render

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.Log
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

open class BaseRender(context: Context) : GLSurfaceView.Renderer {

    val tag = "OpenGLRender"
    val mContext = context

    override fun onSurfaceCreated(p0: GL10?, p1: EGLConfig?) {
        Log.d(tag, "onSurfaceCreated")
    }

    override fun onSurfaceChanged(p0: GL10?, w: Int, h: Int) {
        Log.d(tag, "onSurfaceChanged,w:$w,h:$h")
    }

    override fun onDrawFrame(p0: GL10?) {
        Log.d(tag, "onDrawFrame")
    }

    open fun handleTouchPress(normalX: Float, normalY: Float) {
        Log.d(tag, "handleTouchPress,normalX:$normalX,normalY:$normalY")
    }

    open fun handleTouchMove(normalX: Float, normalY: Float) {
        Log.d(tag, "handleTouchMove,normalX:$normalX,normalY:$normalY")
    }

}