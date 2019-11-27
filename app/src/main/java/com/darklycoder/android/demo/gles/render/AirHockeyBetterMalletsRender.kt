package com.darklycoder.android.demo.gles.render

import android.content.Context
import android.opengl.GLES20.*
import android.opengl.Matrix.*
import com.darklycoder.android.demo.R
import com.darklycoder.android.demo.gles.objects.MalletV2
import com.darklycoder.android.demo.gles.objects.Puck
import com.darklycoder.android.demo.gles.objects.Table
import com.darklycoder.android.demo.gles.program.ColorShaderProgramV2
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
        |
        |void main()
        |{
        |    
        |    gl_Position = u_Matrix * a_Position;
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

    private val viewMatrix = FloatArray(16) // 视图矩阵
    private val viewProjectionMatrix = FloatArray(16)
    private val modelViewProjectionMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)
    private val modelMatrix = FloatArray(16)

    private lateinit var table: Table
    private lateinit var mallet: MalletV2
    private lateinit var puck: Puck
    private lateinit var textureProgram: TextureShaderProgram
    private lateinit var colorProgram: ColorShaderProgramV2
    private var texture: Int = 0

    override fun onSurfaceCreated(p0: GL10?, p1: EGLConfig?) {
        super.onSurfaceCreated(p0, p1)

        glClearColor(0F, 0F, 0F, 0F)

        table = Table()
        mallet = MalletV2(0.08f, 0.15f, 32)
        puck = Puck(0.06f, 0.02f, 32)

        textureProgram = TextureShaderProgram(textureVertexShaderCode, textureFragmentShaderCode)
        colorProgram = ColorShaderProgramV2(vertexShaderCode, fragmentShaderCode)

        texture = ShaderHelper.loadTexture(mContext, R.drawable.air_hockey_surface)
    }

    override fun onSurfaceChanged(p0: GL10?, w: Int, h: Int) {
        super.onSurfaceChanged(p0, w, h)

        glViewport(0, 0, w, h)

        ShaderHelper.perspectiveM(projectionMatrix, 45f, w * 1f / h, 1f, 10f)
        setLookAtM(viewMatrix, 0, 0f, 1.5f, 2.2f, 0f, 0f, 0f, 0f, 1f, 0f)
    }

    override fun onDrawFrame(p0: GL10?) {
        super.onDrawFrame(p0)

        glClear(GL_COLOR_BUFFER_BIT)

        multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0)

        positionTableInScene()
        textureProgram.useProgram()
        textureProgram.setUniforms(modelViewProjectionMatrix, texture)
        table.bindData(textureProgram)
        table.draw()

//        positionObjectInScene(0f, mallet.height / 2, -0.4f)
//        colorProgram.useProgram()
//        colorProgram.setUniforms(modelViewProjectionMatrix, 1f, 0f, 0f)
//        mallet.bindData(colorProgram)
//        mallet.draw()

//        positionObjectInScene(0f, mallet.height / 2, 0.4f)
//        colorProgram.setUniforms(modelViewProjectionMatrix, 0f, 0f, 1f)
//        mallet.bindData(colorProgram)
//        mallet.draw()

        positionObjectInScene(0f, puck.height / 2, 0f)
        colorProgram.setUniforms(modelViewProjectionMatrix, 0.8f, 0.8f, 1f)
        puck.bindData(colorProgram)
        puck.draw()
    }

    private fun positionTableInScene() {
        setIdentityM(modelMatrix, 0)
        rotateM(modelMatrix, 0, -90f, 1f, 0f, 0f)
        multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix, 0, modelMatrix, 0)
    }

    private fun positionObjectInScene(x: Float, y: Float, z: Float) {
        setIdentityM(modelMatrix, 0)
        rotateM(modelMatrix, 0, 0F, x, y, z)
        multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix, 0, modelMatrix, 0)
    }

}