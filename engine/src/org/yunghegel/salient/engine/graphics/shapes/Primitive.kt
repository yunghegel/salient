package org.yunghegel.salient.engine.graphics.shapes

import org.yunghegel.gdx.utils.data.ID
import org.yunghegel.salient.engine.graphics.shapes.primitives.*
import kotlin.reflect.KClass

enum class Primitive(val supplier: KClass<out InstanceSupplier>) : ID {
    _Sphere(Sphere::class),
    _Box(Box::class),
    _Capsule(Capsule::class),
    _Cone(Cone::class),
    _Cylinder(Cylinder::class),
    _Cube(Cube::class),
    _Plane(Plane::class),
    _Arc(Arc::class),
    _Circle(Circle::class),
    _Disc(Disc::class),
    _DoubleCone(DoubleCone::class),
    _FlatTopPyramid(FlatTopPyramid::class),
    _Pyramid(Pyramid::class),
    _Torus(Torus::class),
    _UVSphere(UVSphere::class),
    _Wedge(Wedge::class),;

    override val id: Int = ordinal
    override val uuid: String = name

    val integerParams = mutableMapOf<String,Int>()
    val floatParmas = mutableMapOf<String,Float>()

}
