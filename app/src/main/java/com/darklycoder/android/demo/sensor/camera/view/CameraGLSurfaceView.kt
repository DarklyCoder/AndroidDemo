package com.darklycoder.android.demo.sensor.camera.view

import android.content.Context
import android.graphics.SurfaceTexture
import android.opengl.GLES11Ext
import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import java.nio.ByteOrder
import java.nio.ByteBuffer
import java.nio.FloatBuffer


class CameraGLSurfaceView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : GLSurfaceView(context, attrs) {

    private var mRatioWidth = 0
    private var mRatioHeight = 0
    private var mGLRender: GLRender

    fun setAspectRatio(width: Int, height: Int) {
        if (width <= 0 || height <= 0) {
            return
        }

        mRatioWidth = width
        mRatioHeight = height
        requestLayout()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val w = MeasureSpec.getSize(widthMeasureSpec)
        val h = MeasureSpec.getSize(heightMeasureSpec)

        if (0 == mRatioWidth || 0 == mRatioHeight) {
            setMeasuredDimension(w, h)

        } else {
            setMeasuredDimension(mRatioWidth, mRatioHeight)
        }
    }

    init {
        setEGLContextClientVersion(2)

        mGLRender = GLRender()
        setRenderer(mGLRender)
        renderMode = RENDERMODE_WHEN_DIRTY
    }

}

class GLRender : GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener {

    private val vertexShaderCode = "uniform mat4 textureTransform;\n" +
            "attribute vec2 inputTextureCoordinate;\n" +
            "attribute vec4 position;            \n" +//NDK坐标点
            "varying   vec2 textureCoordinate; \n" +//纹理坐标点变换后输出

            "\n" +
            " void main() {\n" +
            "     gl_Position = position;\n" +
            "     textureCoordinate = inputTextureCoordinate;\n" +
            " }"

    private val fragmentShaderCode = "#extension GL_OES_EGL_image_external : require\n" +
            "precision mediump float;\n" +
            "uniform samplerExternalOES videoTex;\n" +
            "varying vec2 textureCoordinate;\n" +
            "\n" +
            "void main() {\n" +
            "    vec4 tc = texture2D(videoTex, textureCoordinate);\n" +
            "    float color = tc.r * 0.3 + tc.g * 0.59 + tc.b * 0.11;\n" +  //所有视图修改成黑白

            "    gl_FragColor = vec4(color,color,color,1.0);\n" +
            //                "    gl_FragColor = vec4(tc.r,tc.g,tc.b,1.0);\n" +
            "}\n"

    private lateinit var mSurfaceTexture: SurfaceTexture

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        glClearColor(0F, 0F, 0F, 0F)
        mSurfaceTexture = SurfaceTexture(createOESTextureObject())

        createProgram()
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        glClear(GL_COLOR_BUFFER_BIT)
    }

    private fun createOESTextureObject(): Int {
        val tex = IntArray(1)
        // 生成一个纹理
        glGenTextures(1, tex, 0)
        // 将此纹理绑定到外部纹理上
        glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, tex[0])
        //设置纹理过滤参数
        glTexParameterf(
            GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
            GL10.GL_TEXTURE_MIN_FILTER,
            GL_NEAREST.toFloat()
        )
        glTexParameterf(
            GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
            GL10.GL_TEXTURE_MAG_FILTER,
            GL_LINEAR.toFloat()
        )
        glTexParameterf(
            GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
            GL10.GL_TEXTURE_WRAP_S,
            GL_CLAMP_TO_EDGE.toFloat()
        )
        glTexParameterf(
            GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
            GL10.GL_TEXTURE_WRAP_T,
            GL_CLAMP_TO_EDGE.toFloat()
        )
        glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0)

        return tex[0]
    }

    private var mProgram: Int = -1

    private fun loadShader(type: Int, shaderCode: String): Int {
        val shader = glCreateShader(type)
        // 添加上面编写的着色器代码并编译它
        glShaderSource(shader, shaderCode)
        glCompileShader(shader)
        return shader
    }

    private fun createProgram() {
        val vertexShader = loadShader(GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = loadShader(GL_FRAGMENT_SHADER, fragmentShaderCode)
        // 创建空的OpenGL ES程序
        mProgram = glCreateProgram()
        // 添加顶点着色器到程序中
        glAttachShader(mProgram, vertexShader)
        // 添加片段着色器到程序中
        glAttachShader(mProgram, fragmentShader)
        // 创建OpenGL ES程序可执行文件
        glLinkProgram(mProgram)
        // 释放shader资源
        glDeleteShader(vertexShader)
        glDeleteShader(fragmentShader)
    }

    private var mPosBuffer: FloatBuffer? = null
    private var mTexBuffer: FloatBuffer? = null
    private val mPosCoordinate = floatArrayOf(-1f, -1f, -1f, 1f, 1f, -1f, 1f, 1f)
    // 顺时针转90并沿Y轴翻转  后摄像头正确，前摄像头上下颠倒
    private val mTexCoordinateBackRight = floatArrayOf(1f, 1f, 0f, 1f, 1f, 0f, 0f, 0f)
    // 顺时针旋转90  后摄像头上下颠倒了，前摄像头正确
    private val mTexCoordinateForntRight = floatArrayOf(0f, 1f, 1f, 1f, 0f, 0f, 1f, 0f)

    private var uPosHandle: Int = 0
    private var aTexHandle: Int = 0
    private var mMVPMatrixHandle: Int = 0
    private val mProjectMatrix = FloatArray(16)
    private val mCameraMatrix = FloatArray(16)
    private val mMVPMatrix = FloatArray(16)
    private val mTempMatrix = FloatArray(16)
    private val camera_status = 1

    private fun activeProgram() {
        glUseProgram(mProgram)
        mSurfaceTexture.setOnFrameAvailableListener(this)

        // 获取顶点着色器的位置的句柄
        uPosHandle = glGetAttribLocation(mProgram, "position")
        aTexHandle = glGetAttribLocation(mProgram, "inputTextureCoordinate")
        mMVPMatrixHandle = glGetUniformLocation(mProgram, "textureTransform")

        mPosBuffer = convertToFloatBuffer(mPosCoordinate)
        mTexBuffer = if (camera_status == 0) {
            convertToFloatBuffer(mTexCoordinateBackRight)

        } else {
            convertToFloatBuffer(mTexCoordinateForntRight)
        }

        glVertexAttribPointer(uPosHandle, 2, GL_FLOAT, false, 0, mPosBuffer)
        glVertexAttribPointer(aTexHandle, 2, GL_FLOAT, false, 0, mTexBuffer)

        // 启用顶点位置的句柄
        glEnableVertexAttribArray(uPosHandle)
        glEnableVertexAttribArray(aTexHandle)
    }

    private fun convertToFloatBuffer(buffer: FloatArray): FloatBuffer {
        val fb = ByteBuffer.allocateDirect(buffer.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        fb.put(buffer)
        fb.position(0)
        return fb
    }

    override fun onFrameAvailable(surfaceTexture: SurfaceTexture?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
