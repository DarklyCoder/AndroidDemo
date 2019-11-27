package com.darklycoder.android.demo.gles.render

import android.content.Context
import android.opengl.GLES20.*
import android.opengl.Matrix.*
import com.darklycoder.android.demo.R
import com.darklycoder.android.demo.gles.ShaderHelper
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * 绘制曲棍球
 *
 * 实现纹理效果
 */
class AirHockeyTexturedRender(context: Context) : BaseRender(context) {

    companion object {
        const val BYTES_PER_FLOAT = 4

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

    /**
     * 定义顶点数据处理
     */
    class VertexArray(vertexData: FloatArray) {

        private val floatBuffer: FloatBuffer = ByteBuffer
            .allocateDirect(vertexData.size * BYTES_PER_FLOAT)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .put(vertexData)

        fun setVertexAttributePoint(
            dataOffset: Int,
            attributeLocation: Int,
            componentCount: Int,
            stride: Int
        ) {
            floatBuffer.position(dataOffset)

            glVertexAttribPointer(
                attributeLocation,
                componentCount,
                GL_FLOAT,
                false,
                stride,
                floatBuffer
            )
            glEnableVertexAttribArray(attributeLocation)

            floatBuffer.position(0)
        }
    }

    /**
     * 定义桌面
     */
    class Table {
        companion object {
            private const val POSITION_COMPONENT_COUNT = 2
            private const val TEXTURE_COORDINATES_COMPONENT_COUNT = 2
            private const val STRIDE =
                (POSITION_COMPONENT_COUNT + TEXTURE_COORDINATES_COMPONENT_COUNT) * BYTES_PER_FLOAT

            private val VERTEX_DATA = floatArrayOf(
                // X,Y,S,T
                0f, 0f, 0.5f, 0.5f,
                -0.5f, -0.8f, 0f, 0.9f,
                0.5f, -0.8f, 1f, 0.9f,
                0.5f, 0.8f, 1f, 0.1f,
                -0.5f, 0.8f, 0f, 0.1f,
                -0.5f, -0.8f, 0f, 0.9f
            )
        }

        private val vertexArray = VertexArray(VERTEX_DATA)

        fun bindData(textureProgram: TextureShaderProgram) {
            vertexArray.setVertexAttributePoint(
                0,
                textureProgram.getPositionAttributeLocation(),
                POSITION_COMPONENT_COUNT,
                STRIDE
            )

            vertexArray.setVertexAttributePoint(
                POSITION_COMPONENT_COUNT,
                textureProgram.getTextureCoordinatesAttributeLocation(),
                TEXTURE_COORDINATES_COMPONENT_COUNT,
                STRIDE
            )
        }

        fun draw() {
            glDrawArrays(GL_TRIANGLE_FAN, 0, 6)
        }

    }

    /**
     * 定义木槌
     */
    class Mallet {
        companion object {
            private const val POSITION_COMPONENT_COUNT = 2
            private const val COLOR_COMPONENT_COUNT = 3
            private const val STRIDE =
                (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * BYTES_PER_FLOAT

            private val VERTEX_DATA = floatArrayOf(
                // X,Y,R,G,B
                0f, -0.4f, 0f, 0f, 1f,
                0f, 0.4f, 1f, 0f, 0f
            )
        }

        private val vertexArray = VertexArray(VERTEX_DATA)

        fun bindData(colorProgram: ColorShaderProgram) {
            vertexArray.setVertexAttributePoint(
                0,
                colorProgram.getPositionAttributeLocation(),
                POSITION_COMPONENT_COUNT,
                STRIDE
            )

            vertexArray.setVertexAttributePoint(
                POSITION_COMPONENT_COUNT,
                colorProgram.getColorAttributeLocation(),
                COLOR_COMPONENT_COUNT,
                STRIDE
            )
        }

        fun draw() {
            glDrawArrays(GL_POINTS, 0, 2)
        }
    }

    open class ShaderProgram(vertex: String, fragment: String) {

        val mProgram = ShaderHelper.buildProgram(vertex, fragment)

        fun useProgram() {
            glUseProgram(mProgram)
        }
    }

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

}