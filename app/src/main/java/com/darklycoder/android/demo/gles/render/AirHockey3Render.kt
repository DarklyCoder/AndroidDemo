package com.darklycoder.android.demo.gles.render

import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import android.opengl.Matrix.orthoM
import android.util.Log
import com.darklycoder.android.demo.gles.ShaderHelper
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * 绘制曲棍球
 *
 * 绘制桌面，适配横竖屏
 */
class AirHockey3Render : GLSurfaceView.Renderer {

    companion object {
        const val BYTES_PER_FLOAT = 4
        const val POSITION_COMPONENT_COUNT = 2
        const val COLOR_COMPONENT_COUNT = 3
        const val STRIDE = (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * BYTES_PER_FLOAT
    }

    private val tableVertices = floatArrayOf(
        // X,Y,R,G,B
        0f, 0f, 1f, 1f, 1f,
        -0.5f, -0.8f, 0.7f, 0.7f, 0.7f,
        0.5f, -0.8f, 0.7f, 0.7f, 0.7f,
        0.5f, 0.8f, 0.7f, 0.7f, 0.7f,
        -0.5f, 0.8f, 0.7f, 0.7f, 0.7f,
        -0.5f, -0.8f, 0.7f, 0.7f, 0.7f,

        -0.5f, 0f, 1f, 0f, 0f,
        0.5f, 0f, 1f, 0f, 0f,

        0f, -0.4f, 0f, 0f, 1f,
        0f, 0.4f, 1f, 0f, 0f
    )
    private val vertexData = ByteBuffer
        .allocateDirect(tableVertices.size * BYTES_PER_FLOAT)
        .order(ByteOrder.nativeOrder())
        .asFloatBuffer()

    private val vertexShaderCode =
        """
        |uniform mat4 u_Matrix;
        |
        |attribute vec4 a_Position;
        |attribute vec4 a_Color;
        |
        |varying vec4 v_Color;
        |
        |void main()
        |{
        |    v_Color = a_Color;
        |    
        |    gl_Position = u_Matrix * a_Position;
        |    gl_PointSize = 10.0;
        |}
        """.trimMargin()

    private val fragmentShaderCode =
        """
        |precision mediump float;
        |varying vec4 v_Color;
        |
        |void main()
        |{
        |    gl_FragColor = v_Color;
        |}
        """.trimMargin()

    private var mProgramId: Int = 0
    private var uMatrixLocation: Int = 0
    private var aColorLocation: Int = 0
    private var aPositionLocation: Int = 0
    private val projectionMatrix = FloatArray(16)

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

        uMatrixLocation = glGetUniformLocation(mProgramId, "u_Matrix")
        aColorLocation = glGetAttribLocation(mProgramId, "a_Color")
        aPositionLocation = glGetAttribLocation(mProgramId, "a_Position")

        vertexData.position(0)
        glVertexAttribPointer(
            aPositionLocation,
            POSITION_COMPONENT_COUNT,
            GL_FLOAT,
            false,
            STRIDE,
            vertexData
        )
        glEnableVertexAttribArray(aPositionLocation)

        vertexData.position(POSITION_COMPONENT_COUNT)
        glVertexAttribPointer(
            aColorLocation,
            COLOR_COMPONENT_COUNT,
            GL_FLOAT,
            false,
            STRIDE,
            vertexData
        )
        glEnableVertexAttribArray(aColorLocation)
    }

    override fun onSurfaceChanged(p0: GL10?, w: Int, h: Int) {
        Log.d("Render", "onSurfaceChanged,w:$w,h:$h")
        glViewport(0, 0, w, h)

        val aspectRatio = if (w > h) w.toFloat() / h else h.toFloat() / w

        if (w > h) {
            // 横屏
            orthoM(projectionMatrix, 0, -aspectRatio, aspectRatio, -1F, 1F, -1F, 1F)

        } else {
            // 竖屏
            orthoM(projectionMatrix, 0, -1F, 1F, -aspectRatio, aspectRatio, -1F, 1F)
        }
    }

    override fun onDrawFrame(p0: GL10?) {
        Log.d("Render", "onDrawFrame")
        glClear(GL_COLOR_BUFFER_BIT)

        glUniformMatrix4fv(uMatrixLocation, 1, false, projectionMatrix, 0)

        glDrawArrays(GL_TRIANGLE_FAN, 0, 6)
        glDrawArrays(GL_LINES, 6, 2)
        glDrawArrays(GL_POINTS, 8, 1)
        glDrawArrays(GL_POINTS, 9, 1)
    }

}