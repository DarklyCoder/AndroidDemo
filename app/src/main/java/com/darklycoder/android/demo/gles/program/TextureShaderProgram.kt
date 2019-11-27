package com.darklycoder.android.demo.gles.program

import android.opengl.GLES20.*

class TextureShaderProgram(vertex: String, fragment: String) : ShaderProgram(vertex, fragment) {

    private var uMatrixLocation: Int = 0
    private var uTextureLocation: Int = 0
    private var aPositionLocation: Int = 0
    private var aTextureCoordinatesLocation: Int = 0

    init {
        uMatrixLocation = glGetUniformLocation(mProgram, "u_Matrix")
        uTextureLocation = glGetUniformLocation(mProgram, "u_TextureUnit")

        aPositionLocation = glGetAttribLocation(mProgram, "a_Position")
        aTextureCoordinatesLocation = glGetAttribLocation(mProgram, "a_TextureCoordinates")
    }

    fun getPositionAttributeLocation(): Int {
        return aPositionLocation
    }

    fun getTextureCoordinatesAttributeLocation(): Int {
        return aTextureCoordinatesLocation
    }

    fun setUniforms(matrix: FloatArray, textureId: Int) {
        // 传递矩阵
        glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0)

        // 设置活动的纹理单元为0
        glActiveTexture(GL_TEXTURE0)

        // 绑定纹理
        glBindTexture(GL_TEXTURE_2D, textureId)

        // 把绑定的纹理单元传给着色器
        glUniform1i(uTextureLocation, 0)
    }

}