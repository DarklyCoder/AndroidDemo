package com.darklycoder.android.demo.gles.objects

import com.darklycoder.android.demo.gles.data.VertexArray
import com.darklycoder.android.demo.gles.program.ColorShaderProgramV2
import com.darklycoder.android.demo.gles.utils.GeometryHelper

class MalletV2(radius: Float, val height: Float, numPointsAroundMallet: Int) {

    companion object {
        private const val POSITION_COMPONENT_COUNT = 3
    }

    private val vertexArray: VertexArray
    private val drawList: List<GeometryHelper.DrawCommand>

    init {
        val generatedData = GeometryHelper.createMallet(
            GeometryHelper.Point(0f, 0f, 0f),
            radius,
            height,
            numPointsAroundMallet
        )
        vertexArray = VertexArray(generatedData.vertexData)
        drawList = generatedData.drawList
    }

    fun bindData(colorProgram: ColorShaderProgramV2) {
        vertexArray.setVertexAttributePoint(
            0,
            colorProgram.getPositionAttributeLocation(),
            POSITION_COMPONENT_COUNT,
            0
        )
    }

    fun draw() {
        drawList.forEach { it.draw() }
    }

}