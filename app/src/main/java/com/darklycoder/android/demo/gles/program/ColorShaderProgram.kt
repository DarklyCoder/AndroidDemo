package com.darklycoder.android.demo.gles.program

import android.opengl.GLES20.*

class ColorShaderProgram(vertex: String, fragment: String) : ShaderProgram(vertex, fragment) {

    private var uMatrixLocation: Int = 0
    private var aPositionLocation: Int = 0
    private var aColorLocation: Int = 0

    init {
        uMatrixLocation = glGetUniformLocation(mProgram, "u_Matrix")
        aPositionLocation = glGetAttribLocation(mProgram, "a_Position")
        aColorLocation = glGetAttribLocation(mProgram, "a_Color")
    }

    fun getPositionAttributeLocation(): Int {
        return aPositionLocation
    }

    fun getColorAttributeLocation(): Int {
        return aColorLocation
    }

    fun setUniforms(matrix: FloatArray) {
        glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0)
    }
}