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
import com.darklycoder.android.demo.gles.utils.GeometryHelper
import com.darklycoder.android.demo.gles.utils.ShaderHelper
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.max
import kotlin.math.min

/**
 * 绘制曲棍球
 *
 * 实现纹理效果
 * 优化木槌
 * 添加触摸事件
 */
class AirHockeyTouchRender(context: Context) : BaseRender(context) {

    companion object {
        private val vertexShaderCode =
            """
        |uniform mat4 u_Matrix;
        |
        |attribute vec4 a_Position;
        |
        |void main()
        |{
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

        private const val leftBound = -0.5f
        private const val rightBound = 0.5f
        private const val farBound = -0.8f
        private const val nearBound = 0.8f
    }

    private val viewMatrix = FloatArray(16) // 视图矩阵
    private val viewProjectionMatrix = FloatArray(16)
    private val modelViewProjectionMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)
    private val modelMatrix = FloatArray(16)
    private val invertedViewProjectionMatrix = FloatArray(16)

    private lateinit var table: Table
    private lateinit var mallet: MalletV2
    private lateinit var puck: Puck
    private lateinit var textureProgram: TextureShaderProgram
    private lateinit var colorProgram: ColorShaderProgramV2
    private var texture: Int = 0

    private var malletPressed = false
    private lateinit var blueMalletPosition: GeometryHelper.Point
    private lateinit var previousBlueMalletPosition: GeometryHelper.Point

    private lateinit var puckPosition: GeometryHelper.Point
    private lateinit var puckVector: GeometryHelper.Vector

    override fun onSurfaceCreated(p0: GL10?, p1: EGLConfig?) {
        super.onSurfaceCreated(p0, p1)

        glClearColor(0F, 0F, 0F, 0F)

        table = Table()
        mallet = MalletV2(0.09f, 0.12f, 64)
        puck = Puck(0.08f, 0.02f, 64)

        blueMalletPosition = GeometryHelper.Point(0f, mallet.height / 2, 0.4f)
        puckPosition = GeometryHelper.Point(0f, puck.height / 2, 0f)
        puckVector = GeometryHelper.Vector(0f, 0f, 0f)

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

        puckPosition = puckPosition.translate(puckVector)
        if (puckPosition.x < leftBound + puck.radius || puckPosition.x > rightBound - puck.radius) {
            puckVector = GeometryHelper.Vector(-puckVector.x, puckVector.y, puckVector.z)
            puckVector = puckVector.scale(0.9f)
        }
        if (puckPosition.z < farBound + puck.radius || puckPosition.z > nearBound - puck.radius) {
            puckVector = GeometryHelper.Vector(puckVector.x, puckVector.y, -puckVector.z)
            puckVector = puckVector.scale(0.9f)
        }
        puckPosition = GeometryHelper.Point(
            clamp(puckPosition.x, leftBound + puck.radius, rightBound - puck.radius),
            puckPosition.y,
            clamp(puckPosition.z, farBound + puck.radius, nearBound - puck.radius)
        )
        puckVector = puckVector.scale(0.99f)

        multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0)

        invertM(invertedViewProjectionMatrix, 0, viewProjectionMatrix, 0)

        positionTableInScene()
        textureProgram.useProgram()
        textureProgram.setUniforms(modelViewProjectionMatrix, texture)
        table.bindData(textureProgram)
        table.draw()

        positionObjectInScene(0f, mallet.height / 2, -0.4f)
        colorProgram.useProgram()
        colorProgram.setUniforms(modelViewProjectionMatrix, 1f, 0f, 0f)
        mallet.bindData(colorProgram)
        mallet.draw()

        positionObjectInScene(blueMalletPosition.x, blueMalletPosition.y, blueMalletPosition.z)
        colorProgram.setUniforms(modelViewProjectionMatrix, 0f, 0f, 1f)
        mallet.draw()

        positionObjectInScene(puckPosition.x, puckPosition.y, puckPosition.z)
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
        translateM(modelMatrix, 0, x, y, z)
        multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix, 0, modelMatrix, 0)
    }

    override fun handleTouchPress(normalX: Float, normalY: Float) {
        super.handleTouchPress(normalX, normalY)

        val ray = convertNormalized2DPointToRay(normalX, normalY)

        val malletBoundingSphere = GeometryHelper.Sphere(
            GeometryHelper.Point(
                blueMalletPosition.x,
                blueMalletPosition.y,
                blueMalletPosition.z
            ),
            mallet.height / 2f
        )

        malletPressed = GeometryHelper.intersects(malletBoundingSphere, ray)
    }

    override fun handleTouchMove(normalX: Float, normalY: Float) {
        super.handleTouchMove(normalX, normalY)

        if (malletPressed) {
            val ray = convertNormalized2DPointToRay(normalX, normalY)
            val plane = GeometryHelper.Plane(
                GeometryHelper.Point(0f, 0f, 0f),
                GeometryHelper.Vector(0f, 1f, 0f)
            )
            val touchedPoint = GeometryHelper.intersectionPoint(ray, plane)
            previousBlueMalletPosition = blueMalletPosition
            blueMalletPosition = GeometryHelper.Point(
                clamp(touchedPoint.x, leftBound + mallet.radius, rightBound - mallet.radius),
                mallet.height / 2,
                clamp(touchedPoint.z, 0 + mallet.radius, nearBound - mallet.radius)
            )

            val distance = GeometryHelper.vectorBetween(blueMalletPosition, puckPosition).length()
            if (distance < (puck.radius + mallet.radius)) {
                puckVector =
                    GeometryHelper.vectorBetween(previousBlueMalletPosition, blueMalletPosition)
            }
        }
    }

    private fun convertNormalized2DPointToRay(
        normalizedX: Float,
        normalizedY: Float
    ): GeometryHelper.Ray {
        val nearPointNdc = floatArrayOf(normalizedX, normalizedY, -1f, 1f)
        val farPointNdc = floatArrayOf(normalizedX, normalizedY, 1f, 1f)

        val nearPointWorld = FloatArray(4)
        val farPointWorld = FloatArray(4)

        multiplyMV(nearPointWorld, 0, invertedViewProjectionMatrix, 0, nearPointNdc, 0)
        multiplyMV(farPointWorld, 0, invertedViewProjectionMatrix, 0, farPointNdc, 0)

        divideByW(nearPointWorld)
        divideByW(farPointWorld)

        val nearPointRay =
            GeometryHelper.Point(nearPointWorld[0], nearPointWorld[1], nearPointWorld[2])
        val farPointRay = GeometryHelper.Point(farPointWorld[0], farPointWorld[1], farPointWorld[2])

        return GeometryHelper.Ray(
            nearPointRay,
            GeometryHelper.vectorBetween(nearPointRay, farPointRay)
        )
    }

    private fun divideByW(vector: FloatArray) {
        vector[0] /= vector[3]
        vector[1] /= vector[3]
        vector[2] /= vector[3]
    }

    private fun clamp(value: Float, min: Float, max: Float): Float {
        return min(max, max(value, min))
    }

}