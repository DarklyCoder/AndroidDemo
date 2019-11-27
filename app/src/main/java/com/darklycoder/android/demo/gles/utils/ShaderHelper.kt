package com.darklycoder.android.demo.gles.utils

import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLES20.*
import android.opengl.GLUtils
import kotlin.math.tan

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

    /**
     * 创建投影矩阵
     */
    fun perspectiveM(m: FloatArray, yFovInDegrees: Float, aspect: Float, n: Float, f: Float) {
        val angleInRadians = yFovInDegrees * Math.PI / 180F
        val a: Float = (1f / tan(angleInRadians / 2)).toFloat()

        m.forEachIndexed { index, _ ->
            m[index] = when (index) {
                0 -> a / aspect

                5 -> a

                10 -> -(f + n) / (f - n)

                11 -> -1f

                14 -> -(2f * f * n) / (f - n)

                else -> 0f
            }
        }
    }

    /**
     * 加载纹理，获取纹理id
     */
    fun loadTexture(context: Context, resId: Int): Int {
        val textureObjId = IntArray(1)
        glGenTextures(1, textureObjId, 0)
        if (0 == textureObjId[0]) {
            // 创建纹理失败
            return 0
        }

        val option = BitmapFactory.Options()
        option.inScaled = false
        val bmp = BitmapFactory.decodeResource(context.resources, resId, option)
        if (null == bmp) {
            glDeleteTextures(1, textureObjId, 0)
            return 0
        }

        // 绑定纹理
        glBindTexture(GL_TEXTURE_2D, textureObjId[0])

        // 设置纹理过滤
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)

        // 加载纹理
        GLUtils.texImage2D(GL_TEXTURE_2D, 0, bmp, 0)
        bmp.recycle()

        // 生成mip贴图
        glGenerateMipmap(GL_TEXTURE_2D)

        // 取消绑定
        glBindTexture(GL_TEXTURE_2D, 0)

        return textureObjId[0]
    }

    fun buildProgram(vertexShaderSource: String, fragmentShaderSource: String): Int {
        val vertex =
            compileShader(
                GL_VERTEX_SHADER,
                vertexShaderSource
            )
        val fragment =
            compileShader(
                GL_FRAGMENT_SHADER,
                fragmentShaderSource
            )

        return linkProgram(
            vertex,
            fragment
        )
    }

}