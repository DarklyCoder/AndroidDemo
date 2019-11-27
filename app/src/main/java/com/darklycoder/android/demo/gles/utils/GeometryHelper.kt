package com.darklycoder.android.demo.gles.utils

import android.opengl.GLES20.*
import kotlin.math.cos
import kotlin.math.sin

/**
 * 几何图形公共类
 */
class GeometryHelper {

    interface DrawCommand {

        fun draw()
    }

    /**
     * 点
     */
    class Point(val x: Float, val y: Float, val z: Float) {

        fun translateY(distance: Float): Point {
            return Point(
                x,
                y + distance,
                z
            )
        }
    }

    /**
     * 圆
     */
    class Circle(val center: Point, val radius: Float) {

        fun scale(scale: Float): Circle {
            return Circle(
                center,
                radius * scale
            )
        }
    }

    /**
     * 圆柱体
     */
    class Cylinder(val center: Point, val radius: Float, val height: Float)

    data class GeneratedData(val vertexData: FloatArray, val drawList: List<DrawCommand>)

    class ObjectBuilder(sizeInVertices: Int) {

        companion object {
            private const val FLOATS_PER_VERTEX = 3
        }

        private var offset: Int = 0
        private val vertexData = FloatArray(sizeInVertices * FLOATS_PER_VERTEX)
        private val drawList = ArrayList<DrawCommand>()

        /**
         * 添加圆
         */
        fun appendCircle(circle: Circle, numPoints: Int) {
            val startVertex = offset / FLOATS_PER_VERTEX
            val numVertices =
                sizeOfCircleInVertices(
                    numPoints
                )

            vertexData[offset++] = circle.center.x
            vertexData[offset++] = circle.center.y
            vertexData[offset++] = circle.center.z

            for (i in 0..numPoints) {
                val angleInRadians = (i * 1F / numPoints) * (Math.PI * 2F)

                vertexData[offset++] =
                    (circle.center.x + circle.radius * cos(angleInRadians)).toFloat()

                vertexData[offset++] = circle.center.y

                vertexData[offset++] =
                    (circle.center.z + circle.radius * sin(angleInRadians)).toFloat()
            }

            drawList.add(object :
                DrawCommand {
                override fun draw() {
                    glDrawArrays(GL_TRIANGLE_FAN, startVertex, numVertices)
                }
            })
        }

        /**
         * 添加曲面
         */
        fun appendOpenCylinder(cylinder: Cylinder, numPoints: Int) {
            val startVertex = offset / FLOATS_PER_VERTEX
            val numVertices =
                sizeOfOpenCylinderInVertices(
                    numPoints
                )

            val yStart = cylinder.center.y - (cylinder.height / 2F)
            val yEnd = cylinder.center.y + (cylinder.height / 2F)

            for (i in 0..numPoints) {
                val angleInRadians = (i * 1F / numPoints) * (Math.PI * 2F)
                val xPosition =
                    (cylinder.center.x + cylinder.radius * cos(angleInRadians)).toFloat()
                val zPosition =
                    (cylinder.center.z + cylinder.radius * sin(angleInRadians)).toFloat()

                vertexData[offset++] = xPosition
                vertexData[offset++] = yStart
                vertexData[offset++] = zPosition

                vertexData[offset++] = xPosition
                vertexData[offset++] = yEnd
                vertexData[offset++] = zPosition
            }

            drawList.add(object :
                DrawCommand {
                override fun draw() {
                    glDrawArrays(GL_TRIANGLE_STRIP, startVertex, numVertices)
                }
            })
        }

        fun build(): GeneratedData {
            return GeneratedData(
                vertexData,
                drawList
            )
        }

    }

    companion object {

        /**
         * 圆柱体顶部顶点数
         */
        fun sizeOfCircleInVertices(numPoints: Int): Int {
            return 1 + (numPoints + 1)
        }

        /**
         * 圆柱体侧面顶点数
         */
        fun sizeOfOpenCylinderInVertices(numPoints: Int): Int {
            return (numPoints + 1) * 2
        }

        /**
         * 创建冰球(由一个圆柱体顶部和圆柱体侧面构成)
         */
        fun createPuck(puck: Cylinder, numPoints: Int): GeneratedData {
            val size = sizeOfCircleInVertices(
                numPoints
            ) + sizeOfOpenCylinderInVertices(
                numPoints
            )

            val builder =
                ObjectBuilder(
                    size
                )

            val puckTop =
                Circle(
                    puck.center.translateY(puck.height / 2f),
                    puck.radius
                )
            builder.appendCircle(puckTop, numPoints)
            builder.appendOpenCylinder(puck, numPoints)

            return builder.build()
        }

        /**
         * 创建木槌（两个圆柱，类似印章）
         */
        fun createMallet(
            center: Point,
            radius: Float,
            height: Float,
            numPoints: Int
        ): GeneratedData {
            val size =
                sizeOfCircleInVertices(
                    numPoints
                ) * 2 + sizeOfOpenCylinderInVertices(
                    numPoints
                ) * 2

            val builder =
                ObjectBuilder(
                    size
                )

            // 创建底部圆柱
            val baseHeight = height * 0.25F
            val baseCircle =
                Circle(
                    center.translateY(-baseHeight),
                    radius
                )
            val baseCylinder =
                Cylinder(
                    baseCircle.center.translateY(-baseHeight / 2F),
                    radius,
                    height
                )

            builder.appendCircle(baseCircle, numPoints)
            builder.appendOpenCylinder(baseCylinder, numPoints)

            val handleHeight = height * 0.75F
            val handleRadius = radius / 3F
            val handleCircle =
                Circle(
                    center.translateY(height * 0.5f),
                    handleRadius
                )
            val handleCylinder =
                Cylinder(
                    baseCircle.center.translateY(-handleHeight / 2F),
                    handleRadius,
                    handleHeight
                )

            builder.appendCircle(handleCircle, numPoints)
            builder.appendOpenCylinder(handleCylinder, numPoints)

            return builder.build()
        }
    }

}