package com.darklycoder.android.demo.gles.objects

import android.opengl.GLES20.GL_POINTS
import android.opengl.GLES20.glDrawArrays
import com.darklycoder.android.demo.gles.data.Constants
import com.darklycoder.android.demo.gles.data.VertexArray
import com.darklycoder.android.demo.gles.program.ColorShaderProgram

class Mallet {

    companion object {
        private const val POSITION_COMPONENT_COUNT = 2
        private const val COLOR_COMPONENT_COUNT = 3
        private const val STRIDE =
            (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * Constants.BYTES_PER_FLOAT

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