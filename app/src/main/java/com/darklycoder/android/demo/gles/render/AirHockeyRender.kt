package com.darklycoder.android.demo.gles.render

import android.content.Context
import android.opengl.GLES20.*
import com.darklycoder.android.demo.gles.utils.ShaderHelper
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * 绘制曲棍球
 *
 * 绘制简单桌面
 */
class AirHockeyRender(context: Context) : BaseRender(context) {

    companion object {
        const val BYTES_PER_FLOAT = 4
        const val POSITION_COMPONENT_COUNT = 2
    }

    // 定义桌面相关顶点
    private val tableVertices = floatArrayOf(
        // X,Y
        -0.5f, -0.5f,
        0.5f, 0.5f,
        -0.5f, 0.5f,

        -0.5f, -0.5f,
        0.5f, -0.5f,
        0.5f, 0.5f,

        // line
        -0.5f, 0f,
        0.5f, 0f,

        // point
        0f, -0.25f,
        0f, 0.25f
    )
    private val vertexData = ByteBuffer
        .allocateDirect(tableVertices.size * BYTES_PER_FLOAT)
        .order(ByteOrder.nativeOrder())
        .asFloatBuffer()
        .put(tableVertices)

    private val vertexShaderCode =
        """
        |attribute vec4 a_Position;
        |
        |void main()
        |{
        |    gl_Position = a_Position;
        |    gl_PointSize = 10.0;
        |}
        """.trimMargin()

    private val fragmentShaderCode =
        """
        |precision mediump float;
        |uniform vec4 u_Color;
        |
        |void main()
        |{
        |    gl_FragColor = u_Color;
        |}
        """.trimMargin()

    private var mProgramId: Int = 0
    private var uColorLocation: Int = 0
    private var aPositionLocation: Int = 0

    override fun onSurfaceCreated(p0: GL10?, p1: EGLConfig?) {
        super.onSurfaceCreated(p0, p1)

        glClearColor(0F, 0F, 0F, 0F)

        val vertexShader = ShaderHelper.compileShader(GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = ShaderHelper.compileShader(GL_FRAGMENT_SHADER, fragmentShaderCode)
        mProgramId = ShaderHelper.linkProgram(vertexShader, fragmentShader)
        val valid = ShaderHelper.validProgram(mProgramId)

        glUseProgram(mProgramId)

        uColorLocation = glGetUniformLocation(mProgramId, "u_Color")
        aPositionLocation = glGetAttribLocation(mProgramId, "a_Position")

        vertexData.position(0)
        glVertexAttribPointer(
            aPositionLocation,
            POSITION_COMPONENT_COUNT,
            GL_FLOAT,
            false,
            0,
            vertexData
        )
        glEnableVertexAttribArray(aPositionLocation)
    }

    override fun onSurfaceChanged(p0: GL10?, w: Int, h: Int) {
        super.onSurfaceChanged(p0, w, h)

        glViewport(0, 0, w, h)
    }

    override fun onDrawFrame(p0: GL10?) {
        super.onDrawFrame(p0)

        glClear(GL_COLOR_BUFFER_BIT)

        // 绘制三角形
        glUniform4f(uColorLocation, 1f, 1f, 1f, 1f)
        glDrawArrays(GL_TRIANGLES, 0, 6)

        // 绘制line
        glUniform4f(uColorLocation, 1f, 0f, 1f, 1f)
        glDrawArrays(GL_LINES, 6, 2)

        // 绘制point
        glUniform4f(uColorLocation, 0f, 0f, 1f, 1f)
        glDrawArrays(GL_POINTS, 8, 1)

        glUniform4f(uColorLocation, 0f, 0f, 1f, 1f)
        glDrawArrays(GL_POINTS, 9, 1)
    }

}