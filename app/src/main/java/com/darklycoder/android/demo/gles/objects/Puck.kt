package com.darklycoder.android.demo.gles.objects

import com.darklycoder.android.demo.gles.data.VertexArray
import com.darklycoder.android.demo.gles.program.ColorShaderProgram
import com.darklycoder.android.demo.gles.utils.GeometryHelper

class Puck(val radius: Float, val height: Float, numPointsAroundPuck: Int) {

    companion object{
        private val POSITION_COMPONENT_COUNT = 3
    }

    private val vertexArray: VertexArray
    private val drawList: List<GeometryHelper.DrawCommand>

    init {
        val cylinder =
            GeometryHelper.Cylinder(
                GeometryHelper.Point(
                    0f,
                    0f,
                    0f
                ),
                radius,
                height
            )
        val generatedData =
            GeometryHelper.createPuck(
                cylinder,
                numPointsAroundPuck
            )

        vertexArray = VertexArray(generatedData.vertexData)
        drawList = generatedData.drawList
    }

    fun bindData(colorProgram: ColorShaderProgram) {
        vertexArray.setVertexAttributePoint(
            0,
            colorProgram.getColorAttributeLocation(),
            2,
            0
        )
    }

    fun draw() {
        drawList.forEach { it.draw() }
    }

}