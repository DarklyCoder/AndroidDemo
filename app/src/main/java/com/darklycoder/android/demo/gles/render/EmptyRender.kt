package com.darklycoder.android.demo.gles.render

import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import android.util.Log
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class EmptyRender : GLSurfaceView.Renderer {

    override fun onSurfaceCreated(p0: GL10?, p1: EGLConfig?) {
        Log.d("Render", "onSurfaceCreated")
        glClearColor(1F, 0F, 0F, 0F)
    }

    override fun onSurfaceChanged(p0: GL10?, w: Int, h: Int) {
        Log.d("Render", "onSurfaceChanged")
        glViewport(0, 0, w, h)
    }

    override fun onDrawFrame(p0: GL10?) {
        Log.d("Render", "onDrawFrame")
        glClear(GL_COLOR_BUFFER_BIT)
    }

}