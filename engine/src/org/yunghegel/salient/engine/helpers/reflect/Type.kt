package org.yunghegel.salient.engine.helpers.reflect

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.Vector4
import kotlin.Float
import kotlin.reflect.KClass

enum class Type(val type: KClass<*>,val default: Any) {

    Float(kotlin.Float::class,-1f),
    Int(kotlin.Int::class,-1),
    String(kotlin.String::class,"<err>"),
    Boolean(kotlin.Boolean::class,false),
    Vec2(Vector2::class, Vector2(-1f,-1f)),
    Vec3(Vector3::class, Vector3(-1f,-1f,-1f)),
    Vec4(Vector4::class, Vector4(-1f,-1f,-1f,-1f)),
    Col(Color::class,Color(-1f,-1f,-1f,-1f)),
    Mat4(Matrix4::class,Matrix4()),
    Unknown(Any::class,Any())

}