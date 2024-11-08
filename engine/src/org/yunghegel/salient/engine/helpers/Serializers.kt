package org.yunghegel.salient.engine.helpers

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder


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