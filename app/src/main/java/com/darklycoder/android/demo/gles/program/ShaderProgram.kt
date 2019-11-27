package com.darklycoder.android.demo.gles.program

import android.opengl.GLES20.*

import com.darklycoder.android.demo.gles.utils.ShaderHelper

open class ShaderProgram(vertex: String, fragment: String) {

    val mProgram = ShaderHelper.buildProgram(vertex, fragment)

    fun useProgram() {
        glUseProgram(mProgram)
    }

}