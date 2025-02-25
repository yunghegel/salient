package org.yunghegel.salient.engine.helpers

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Matrix3
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.Vector4
import kotlinx.serialization.Contextual
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.builtins.FloatArraySerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.yunghegel.salient.engine.api.Settable
import kotlin.reflect.KClass


typealias Ignore = Transient

object FileHandleSerializer : KSerializer<SFile> {
    override val descriptor = String.serializer().descriptor
    override fun serialize(encoder: Encoder, value: FileHandle) {
        encoder.encodeString(value.path())
    }
    override fun deserialize(decoder: Decoder): FileHandle {
        return Gdx.files.absolute(decoder.decodeString())
    }
}
typealias SFile = @Serializable(with = FileHandleSerializer::class) FileHandle

class ClassSerializer<T> : KSerializer<Class<T>> {
    override val descriptor = String.serializer().descriptor
    override fun serialize(encoder: Encoder, value: Class<T>) {
        encoder.encodeString(value.name)
    }
    override fun deserialize(decoder: Decoder): Class<T> {
        return Class.forName(decoder.decodeString()) as Class<T>
    }
}

typealias SClass<T> = @Serializable(with = ClassSerializer::class) Class<T>

object KSClassSerializer : KSerializer<KClass<*>> {
    override val descriptor = String.serializer().descriptor
    override fun serialize(encoder: Encoder, value: KClass<*>) {
        encoder.encodeString(value.qualifiedName!!)
    }

    override fun deserialize(decoder: Decoder): KClass<*> {
        return Class.forName(decoder.decodeString()).kotlin
    }
}

typealias KSClass<T> = @Serializable(with = KSClassSerializer::class) KClass<T>

val IntSerializer = Int.serializer()
val FloatSerializer = Float.serializer()
val StringSerializer = String.serializer()


object Vector2Serializer : KSerializer<Vector2f> {
    override val descriptor = buildClassSerialDescriptor("Vector2") {
        element("values", FloatArraySerializer().descriptor)
    }

    override fun serialize(encoder: Encoder, value: Vector2f) {
        val values = floatArrayOf(value.x, value.y)
        encoder.encodeSerializableValue(FloatArraySerializer(), values)

    }

    override fun deserialize(decoder: Decoder): Vector2f {
        val values = decoder.decodeSerializableValue(FloatArraySerializer())
        return Vector2f(values[0], values[1])
    }
}

@Serializable(with = Vector2Serializer::class)
class Vector2f(x: Float, y: Float) : Vector2(x, y) {
    constructor() : this(0f, 0f)
}

object Vector3Serializer : KSerializer<Vector3f> {
    override val descriptor = buildClassSerialDescriptor("Vector3") {
        element("values", FloatArraySerializer().descriptor)
    }

    override fun serialize(encoder: Encoder, value: Vector3f) {
        val values = floatArrayOf(value.x, value.y, value.z)
        encoder.encodeSerializableValue(FloatArraySerializer(), values)

    }

    override fun deserialize(decoder: Decoder): Vector3f {
        val values = decoder.decodeSerializableValue(FloatArraySerializer())
        return Vector3f(values[0], values[1], values[2])
    }
}

@Serializable(with = Vector3Serializer::class)
class Vector3f(x: Float, y: Float, z: Float) : @Contextual Vector3(x, y, z) {
    constructor() : this(0f, 0f, 0f)
}


object Vector4Serializer : KSerializer<Vector4f> {
    override val descriptor = buildClassSerialDescriptor("Vector4") {
        element("values", FloatArraySerializer().descriptor)
    }

    override fun serialize(encoder: Encoder, value: Vector4f) {
        val values = floatArrayOf(value.x, value.y, value.z, value.w)
        encoder.encodeSerializableValue(FloatArraySerializer(), values)
    }

    override fun deserialize(decoder: Decoder): Vector4f {
        val values = decoder.decodeSerializableValue(FloatArraySerializer())
        return Vector4f(values[0], values[1], values[2], values[3])
    }
}

@Serializable(with = Vector4Serializer::class)
class Vector4f(x: Float, y: Float, z: Float, w: Float) : @Contextual Vector4(x, y, z, w)

object Matrix4Serializer : KSerializer<Matrix4f> {
    override val descriptor = buildClassSerialDescriptor("Matrix4") {
        element("val", FloatArraySerializer().descriptor)
    }

    override fun serialize(encoder: Encoder, value: Matrix4f) {
        encoder.encodeSerializableValue(FloatArraySerializer(), value.`val`)
    }

    override fun deserialize(decoder: Decoder): Matrix4f {
        val values = decoder.decodeSerializableValue(FloatArraySerializer())
        return Matrix4f().apply { set(values) }
    }
}

@Serializable(with = Matrix4Serializer::class)
class Matrix4f() : Matrix4()

object Matrix3Serializer : KSerializer<Matrix3f> {
    override val descriptor = buildClassSerialDescriptor("Matrix3") {
        element("values", FloatArraySerializer().descriptor)
    }

    override fun serialize(encoder: Encoder, value: Matrix3f) {
        encoder.encodeSerializableValue(FloatArraySerializer(), value.`val`)
    }

    override fun deserialize(decoder: Decoder): Matrix3f {
        val values = decoder.decodeSerializableValue(FloatArraySerializer())
        return Matrix3f().apply { set(values) }
    }
}

@Serializable(with = Matrix3Serializer::class)
class Matrix3f() : Matrix3()

object ColorSerializer : KSerializer<RGBColor> {
    override val descriptor = buildClassSerialDescriptor("Color") {
        element("values", FloatArraySerializer().descriptor)
    }

    override fun serialize(encoder: Encoder, value: RGBColor) {
        val values = floatArrayOf(value.r, value.g, value.b, value.a)
        encoder.encodeSerializableValue(FloatArraySerializer(), values)
    }

    override fun deserialize(decoder: Decoder): RGBColor {
        val values = decoder.decodeSerializableValue(FloatArraySerializer())
        return RGBColor(values[0], values[1], values[2], values[3])

    }
}

@Serializable(with = ColorSerializer::class)
class RGBColor(r: Float, g: Float, b: Float, a: Float) : Color(r, g, b, a) {
    constructor() : this(0f, 0f, 0f, 1f)


    companion object {
        val WHITE: RGBColor = RGBColor().set(Color.WHITE) as RGBColor
        val BLACK: RGBColor = RGBColor().set(Color.BLACK) as RGBColor
        val RED: RGBColor = RGBColor().set(Color.RED) as RGBColor
        val GREEN: RGBColor = RGBColor().set(Color.GREEN) as RGBColor
        val BLUE: RGBColor = RGBColor().set(Color.BLUE) as RGBColor
        val YELLOW: RGBColor = RGBColor().set(Color.YELLOW) as RGBColor
        val CYAN: RGBColor = RGBColor().set(Color.CYAN) as RGBColor
        val MAGENTA: RGBColor = RGBColor().set(Color.MAGENTA) as RGBColor
        val LIGHT_GRAY: RGBColor = RGBColor().set(Color.LIGHT_GRAY) as RGBColor
        val GRAY: RGBColor = RGBColor().set(Color.GRAY) as RGBColor
        val DARK_GRAY: RGBColor = RGBColor().set(Color.DARK_GRAY) as RGBColor
        val PINK: RGBColor = RGBColor().set(Color.PINK) as RGBColor
        val ORANGE: RGBColor = RGBColor().set(Color.ORANGE) as RGBColor
        val CORAL: RGBColor = RGBColor().set(Color.CORAL) as RGBColor
        val BROWN: RGBColor = RGBColor().set(Color.BROWN) as RGBColor
        val SKY: RGBColor = RGBColor().set(Color.SKY) as RGBColor
        val SALMON: RGBColor = RGBColor().set(Color.SALMON) as RGBColor
        val TEAL: RGBColor = RGBColor().set(Color.TEAL) as RGBColor
        val OLIVE: RGBColor = RGBColor().set(Color.OLIVE) as RGBColor
        val PURPLE: RGBColor = RGBColor().set(Color.PURPLE) as RGBColor
        val VIOLET: RGBColor = RGBColor().set(Color.VIOLET) as RGBColor
        val MAROON: RGBColor = RGBColor().set(Color.MAROON) as RGBColor

    }
}