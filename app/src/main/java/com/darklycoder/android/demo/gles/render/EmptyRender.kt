package com.darklycoder.android.demo.gles.render

import android.content.Context
import android.opengl.GLES20.*
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * 空Render，了解Render相关回调
 */
class EmptyRender(context: Context) : BaseRender(context) {

    override fun onSurfaceCreated(p0: GL10?, p1: EGLConfig?) {
        super.onSurfaceCreated(p0, p1)

        // 设置背景颜色
        glClearColor(1F, 0F, 0F, 0F)
    }

    override fun onSurfaceChanged(p0: GL10?, w: Int, h: Int) {
        super.onSurfaceChanged(p0, w, h)

        // 设置视窗大小
        glViewport(0, 0, w, h)
    }

    override fun onDrawFrame(p0: GL10?) {
        super.onDrawFrame(p0)

        // 清除缓存
        glClear(GL_COLOR_BUFFER_BIT)
    }

}