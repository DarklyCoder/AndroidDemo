package com.darklycoder.android.demo.gles.program

import android.opengl.GLES20.*

class ColorShaderProgramV2(vertex: String, fragment: String) : ShaderProgram(vertex, fragment) {

    private var uColorLocation: Int = 0
    private var uMatrixLocation: Int = 0
    private var aPositionLocation: Int = 0

    init {
        uColorLocation = glGetUniformLocation(mProgram, "u_Color")
        uMatrixLocation = glGetUniformLocation(mProgram, "u_Matrix")
        aPositionLocation = glGetAttribLocation(mProgram, "a_Position")
    }

    fun getPositionAttributeLocation(): Int {
        return aPositionLocation
    }

    fun setUniforms(matrix: FloatArray, r: Float, g: Float, b: Float) {
        glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0)

        glUniform4f(uColorLocation, r, g, b, 1F)
    }
}