package com.darklycoder.android.demo.gles.render

import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import android.util.Log
import com.darklycoder.android.demo.gles.ShaderHelper
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class AirHockeyRender : GLSurfaceView.Renderer {

    companion object {
        const val BYTES_PER_FLOAT = 4
        const val POSITION_COMPONENT_COUNT = 2
    }

    private val tableVertices = floatArrayOf(
        -0.5f, -0.5f,
        0.5f, 0.5f,
        -0.5f, 0.5f,

        -0.5f, -0.5f,
        0.5f, -0.5f,
        0.5f, 0.5f,

        -0.5f, 0f,
        0.5f, 0f,

        0f, -0.25f,
        0f, 0.25f
    )
    private val vertexData = ByteBuffer
        .allocateDirect(tableVertices.size * BYTES_PER_FLOAT)
        .order(ByteOrder.nativeOrder())
        .asFloatBuffer()

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

    init {
        vertexData.put(tableVertices)
    }

    override fun onSurfaceCreated(p0: GL10?, p1: EGLConfig?) {
        Log.d("Render", "onSurfaceCreated")
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
        Log.d("Render", "onSurfaceChanged,w:$w,h:$h")
        glViewport(0, 0, w, h)
    }

    override fun onDrawFrame(p0: GL10?) {
        Log.d("Render", "onDrawFrame")
        glClear(GL_COLOR_BUFFER_BIT)

        glUniform4f(uColorLocation, 1f, 1f, 1f, 1f)
        glDrawArrays(GL_TRIANGLES, 0, 6)

        glUniform4f(uColorLocation, 1f, 0f, 1f, 1f)
        glDrawArrays(GL_LINES, 6, 2)

        glUniform4f(uColorLocation, 0f, 0f, 1f, 1f)
        glDrawArrays(GL_POINTS, 8, 1)

        glUniform4f(uColorLocation, 0f, 0f, 1f, 1f)
        glDrawArrays(GL_POINTS, 9, 1)
    }

}