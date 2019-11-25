package com.darklycoder.android.demo.gles

import android.opengl.GLES20.*

object ShaderHelper {

    /**
     * 编译着色器
     */
    fun compileShader(type: Int, shaderCode: String): Int {
        // 创建着色器对象
        val shaderObjId = glCreateShader(type)
        if (0 == shaderObjId) {
            // 编译失败
            return 0
        }

        glShaderSource(shaderObjId, shaderCode)
        glCompileShader(shaderObjId)
        // 获取编译状态
        val compileStatus = IntArray(1)
        glGetShaderiv(shaderObjId, GL_COMPILE_STATUS, compileStatus, 0)
        if (0 == compileStatus[0]) {
            glDeleteShader(shaderObjId)
            // 编译失败
            return 0
        }

        return shaderObjId
    }

    /**
     * 链接着色器
     */
    fun linkProgram(vertexShaderId: Int, fragmentShaderId: Int): Int {
        val programObjId = glCreateProgram()
        if (0 == programObjId) {
            return 0
        }

        glAttachShader(programObjId, vertexShaderId)
        glAttachShader(programObjId, fragmentShaderId)

        glLinkProgram(programObjId)

        val linkStatus = IntArray(1)
        glGetProgramiv(programObjId, GL_LINK_STATUS, linkStatus, 0)
        if (0 == linkStatus[0]) {
            glDeleteProgram(programObjId)
            return 0
        }

        return programObjId
    }

    /**
     * 验证是否可用
     */
    fun validProgram(programObjId: Int): Boolean {
        glValidateProgram(programObjId)

        val validateStatus = IntArray(1)
        glGetProgramiv(programObjId, GL_VALIDATE_STATUS, validateStatus, 0)

        return 0 != validateStatus[0]
    }

}