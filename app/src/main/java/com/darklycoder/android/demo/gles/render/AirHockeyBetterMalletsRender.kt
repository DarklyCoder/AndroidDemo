package com.darklycoder.android.demo.gles.render

import android.content.Context
import android.opengl.GLES20.*
import android.opengl.Matrix.*
import com.darklycoder.android.demo.R
import com.darklycoder.android.demo.gles.objects.Mallet
import com.darklycoder.android.demo.gles.objects.Table
import com.darklycoder.android.demo.gles.program.ColorShaderProgram
import com.darklycoder.android.demo.gles.program.TextureShaderProgram
import com.darklycoder.android.demo.gles.utils.ShaderHelper
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * 绘制曲棍球
 *
 * 实现纹理效果
 * 优化木槌
 */
class AirHockeyBetterMalletsRender(context: Context) : BaseRender(context) {

    companion object {
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

        private val textureVertexShaderCode =
            """
        |uniform mat4 u_Matrix;
        |
        |attribute vec4 a_Position;
        |attribute vec2 a_TextureCoordinates;
        |
        |varying vec2 v_TextureCoordinates;
        |
        |void main()
        |{
        |   v_TextureCoordinates = a_TextureCoordinates;
        |   gl_Position = u_Matrix * a_Position;
        |}
        """.trimMargin()

        private val textureFragmentShaderCode =
            """
        |precision mediump float;
        |
        |uniform sampler2D u_TextureUnit;
        |varying vec2 v_TextureCoordinates;
        |
        |void main()
        |{
        |    gl_FragColor = texture2D(u_TextureUnit, v_TextureCoordinates);
        |}
        """.trimMargin()
    }

    private val projectionMatrix = FloatArray(16)
    private val modelMatrix = FloatArray(16)
    private lateinit var table: Table
    private lateinit var mallet: Mallet
    private lateinit var textureProgram: TextureShaderProgram
    private lateinit var colorProgram: ColorShaderProgram
    private var texture: Int = 0

    override fun onSurfaceCreated(p0: GL10?, p1: EGLConfig?) {
        super.onSurfaceCreated(p0, p1)

        glClearColor(0F, 0F, 0F, 0F)

        table = Table()
        mallet = Mallet()

        textureProgram = TextureShaderProgram(textureVertexShaderCode, textureFragmentShaderCode)
        colorProgram = ColorShaderProgram(vertexShaderCode, fragmentShaderCode)

        texture = ShaderHelper.loadTexture(mContext, R.drawable.air_hockey_surface)
    }

    override fun onSurfaceChanged(p0: GL10?, w: Int, h: Int) {
        super.onSurfaceChanged(p0, w, h)

        glViewport(0, 0, w, h)

        ShaderHelper.perspectiveM(projectionMatrix, 45f, w * 1f / h, 1f, 10f)
        setIdentityM(modelMatrix, 0)
        translateM(modelMatrix, 0, 0f, 0f, -2.75f)
        rotateM(modelMatrix, 0, -60f, 1f, 0f, 0f)

        val tmp = FloatArray(16)
        multiplyMM(tmp, 0, projectionMatrix, 0, modelMatrix, 0)
        System.arraycopy(tmp, 0, projectionMatrix, 0, tmp.size)
    }

    override fun onDrawFrame(p0: GL10?) {
        super.onDrawFrame(p0)

        glClear(GL_COLOR_BUFFER_BIT)

        textureProgram.useProgram()
        textureProgram.setUniforms(projectionMatrix, texture)
        table.bindData(textureProgram)
        table.draw()

        colorProgram.useProgram()
        colorProgram.setUniforms(projectionMatrix)
        mallet.bindData(colorProgram)
        mallet.draw()
    }

}