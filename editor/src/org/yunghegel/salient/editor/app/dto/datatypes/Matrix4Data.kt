package org.yunghegel.salient.engine.api.model.dto.datatypes

import com.badlogic.gdx.math.Matrix4
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.FloatArraySerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
data class Matrix4Data(val m00: Float, val m01: Float, val m02: Float, val m03: Float,
                       val m10: Float, val m11: Float, val m12: Float, val m13: Float,
                       val m20: Float, val m21: Float, val m22: Float, val m23: Float,
                       val m30: Float, val m31: Float, val m32: Float, val m33: Float) {

    constructor(matrix: Matrix4):this(
        matrix.`val`[0], matrix.`val`[1], matrix.`val`[2], matrix.`val`[3],
        matrix.`val`[4], matrix.`val`[5], matrix.`val`[6], matrix.`val`[7],
        matrix.`val`[8], matrix.`val`[9], matrix.`val`[10], matrix.`val`[11],
        matrix.`val`[12], matrix.`val`[13], matrix.`val`[14], matrix.`val`[15]
                                     )

    companion object {

        val identity = Matrix4Data(
            1f, 0f, 0f, 0f,
            0f, 1f, 0f, 0f,
            0f, 0f, 1f, 0f,
            0f, 0f, 0f, 1f
        )

        fun fromMat4(matrix: Matrix4): Matrix4Data {
            return Matrix4Data(
                matrix.`val`[0], matrix.`val`[1], matrix.`val`[2], matrix.`val`[3],
                matrix.`val`[4], matrix.`val`[5], matrix.`val`[6], matrix.`val`[7],
                matrix.`val`[8], matrix.`val`[9], matrix.`val`[10], matrix.`val`[11],
                matrix.`val`[12], matrix.`val`[13], matrix.`val`[14], matrix.`val`[15]
            )
        }

        fun toMat4(matrix: Matrix4Data): Matrix4 {
            return Matrix4(
                floatArrayOf(
                    matrix.m00, matrix.m01, matrix.m02, matrix.m03,
                    matrix.m10, matrix.m11, matrix.m12, matrix.m13,
                    matrix.m20, matrix.m21, matrix.m22, matrix.m23,
                    matrix.m30, matrix.m31, matrix.m32, matrix.m33
                )
            )
        }
    }

    class Matrix4DataSerializer: KSerializer<Matrix4Data> {

        override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Matrix4Data") {
            element<Array<Float>>("values")
        }

        override fun serialize(encoder: Encoder, value: Matrix4Data) {
            encoder.beginStructure(descriptor)
            encoder.encodeSerializableValue(
                FloatArraySerializer(), floatArrayOf(
                    value.m00, value.m01, value.m02, value.m03,
                    value.m10, value.m11, value.m12, value.m13,
                    value.m20, value.m21, value.m22, value.m23,
                    value.m30, value.m31, value.m32, value.m33
                                                    )
                                           )

        }

        override fun deserialize(decoder: Decoder): Matrix4Data {
            val values = decoder.decodeSerializableValue(FloatArraySerializer())
            return Matrix4Data(
                values[0], values[1], values[2], values[3],
                values[4], values[5], values[6], values[7],
                values[8], values[9], values[10], values[11],
                values[12], values[13], values[14], values[15]
            )
        }

    }

}