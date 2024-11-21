package org.yunghegel.salient.engine.graphics.shapes

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g3d.Model
import org.yunghegel.gdx.utils.data.ID
import org.yunghegel.salient.engine.graphics.shapes.primitives.*
import kotlin.reflect.KClass

enum class Primitive(val supplier: KClass<out InstanceSupplier>, val icon: String = "Part") : ID {
    _Sphere(Sphere::class,"sphere") {
        init {
            integerParams["divisionsU"] = 16
            integerParams["divisionsV"] = 16
            floatParmas["radius"] = 1f
        }
    },
    _Box(Box::class,"cube")
    {
        init {
            floatParmas["width"] = 1f
            floatParmas["height"] = 1f
            floatParmas["depth"] = 1f
        }
    },
    _Capsule(Capsule::class) {
        init {
            floatParmas["radius"] = 1f
            floatParmas["height"] = 1f
            integerParams["divisions"] = 16
        }
    },
    _Cone(Cone::class,"cone") {
        init {
            floatParmas["width"] = 1f
            floatParmas["height"] = 1f
            floatParmas["depth"] = 1f
            integerParams["divisions"] = 16
        }
    },
    _Cylinder(Cylinder::class,"cylinder") {
        init {
            floatParmas["radius"] = 1f
            floatParmas["height"] = 1f
            floatParmas["depth"] = 1f
            integerParams["divisions"] = 16
        }
    },
    _Cube(Cube::class,"cube"){
        init {
            floatParmas["size"] = 1f
        }
    },
    _Plane(Plane::class,"plane"){
        init {
            floatParmas["scale"] = 1f
        }
    },
    _Arc(Arc::class) {
        init {
            floatParmas["startAngle"] = 0f
            floatParmas["endAngle"] = 180f
            floatParmas["radius"] = 1f
            integerParams["vertices"] = 16
        }
    },
    _Circle(Circle::class) {
        init {
            floatParmas["radius"] = 1f
            integerParams["vertices"] = 16
            floatParmas["centerY"] = 0f
        }
    },
    _Disc(Disc::class)
    {
        init {
            floatParmas["innerRadius"] = 0f
            floatParmas["outerRadius"] = 1f
            integerParams["discSegments"] = 16
            integerParams["rotationSegments"] = 16
        }
    },
    _DoubleCone(DoubleCone::class)
    {
        init {
            floatParmas["radius"] = 1f
            floatParmas["height"] = 1f
            integerParams["vertices"] = 16
            integerParams["divisions"] = 16
        }
    },
    _FlatTopPyramid(FlatTopPyramid::class)
        {
        init {
            floatParmas["size"] = 1f
            floatParmas["topScale"] = 1f
        }
    },
    _Pyramid(Pyramid::class)
    {
        init {
            floatParmas["width"] = 1f
            floatParmas["height"] = 1f
        }
    },
    _Torus(Torus::class,"torus"){
        init {
            floatParmas["minorRadius"] = 1f
            floatParmas["majorRadius"] = 0.25f
            integerParams["minorSegments"] = 16
            integerParams["majorSegments"] = 16
        }
    },
    _UVSphere(UVSphere::class){
        init {
            floatParmas["radius"] = 1f
            integerParams["rings"] = 16
            integerParams["segments"] = 16
        }
    },
    _Wedge(Wedge::class,"Part_Wedge"){
        init {
            floatParmas["radius"] = 1f
            floatParmas["angle"] = 180f
        }
    };

    override val id: Int = ordinal
    override val uuid: String = "primitive_${name.lowercase()}_$id"

    val integerParams = mutableMapOf<String,Int>()

    val floatParmas = mutableMapOf<String,Float>().apply {
        this["r"] = 0f
        this["g"] = 0f
        this["b"] = 0f
        this["a"] = 1f
    }

    var color: Color =  Color.WHITE

    val defaultParams: ShapeParameters
        get() = ShapeParameters(this).apply {
            integerParams.forEach { (name, value) -> int(name, value) }
            floatParmas.forEach { (name, value) -> float(name, value) }
        }



    companion object {
        fun fromParams(params: ShapeParameters): PrimitiveModel {
            val color = params.floatParmas["r"]?.let { r ->
                params.floatParmas["g"]?.let { g ->
                    params.floatParmas["b"]?.let { b ->
                        params.floatParmas["a"]?.let { a ->
                            Color(r, g, b, a)
                        }
                    }
                }
            }    ?: Color.WHITE

             when (params.kind) {
                _Sphere -> {
                    val radius = params.floatParmas["radius"] ?: 1f
                    val divisionsU = params.integerParams["divisionsU"] ?: 16
                    val divisionsV = params.integerParams["divisionsV"] ?: 16
                    return Sphere(radius, divisionsU, divisionsV,color).createModel(params)
                }
                _Box -> {
                    val width = params.floatParmas["width"] ?: 1f
                    val height = params.floatParmas["height"] ?: 1f
                    val depth = params.floatParmas["depth"] ?: 1f
                    return Box(width, depth, height,color).createModel(params)
                }
                _Capsule -> {
                    val radius = params.floatParmas["radius"] ?: 0.5f
                    val height = params.floatParmas["height"] ?: 1f
                    val divisions = params.integerParams["divisions"] ?: 16
                    return Capsule(radius, height, divisions,color).createModel(params)
                }
                _Cone -> {
                    val width = params.floatParmas["width"] ?: 1f
                    val height = params.floatParmas["height"] ?: 1f
                    val depth = params.floatParmas["depth"] ?: 1f
                    val divisions = params.integerParams["divisions"] ?: 16
                    return Cone(width, height, depth, divisions,color).createModel(params)
                }
                _Cylinder -> {
                    val radius = params.floatParmas["radius"] ?: 1f
                    val height = params.floatParmas["height"] ?: 1f
                    val depth = params.floatParmas["depth"] ?: 1f
                    val divisions = params.integerParams["divisions"] ?: 16
                    return Cylinder(radius, height, depth, divisions,color).createModel(params)
                }
                _Cube -> {
                    val size = params.floatParmas["size"] ?: 1f
                    return Cube(size).createModel(params)
                }
                _Plane -> {
                    val width = params.floatParmas["width"] ?: 1f
                    return Plane(width, color).createModel(params)
                }
                _Arc -> {
                    val startAngle = params.floatParmas["startAngle"] ?: 0f
                    val endAngle = params.floatParmas["endAngle"] ?: 180f
                    val radius = params.floatParmas["radius"] ?: 1f
                    val vertices = params.integerParams["vertices"] ?: 16
                    return Arc(startAngle, endAngle, radius, vertices, color).createModel(params)
                }
                _Circle -> {
                    val radius = params.floatParmas["radius"] ?: 1f
                    val vertices = params.integerParams["vertices"] ?: 16
                    val centerY = params.floatParmas["centerY"] ?: 0f
                    return Circle(color,vertices,radius,centerY).createModel(params)
                }
                _Disc -> {
                    val innerRadius = params.floatParmas["innerRadius"] ?: 0f
                    val outerRadius = params.floatParmas["outerRadius"] ?: 1f
                    val discSegments = params.integerParams["discSegments"] ?: 16
                    val rotationSegments = params.integerParams["rotationSegments"] ?: 16
                    return Disc(innerRadius, outerRadius, discSegments, rotationSegments, color).createModel(params)
                }
                _DoubleCone -> {
                    val radius = params.floatParmas["radius"] ?: 1f
                    val height = params.floatParmas["height"] ?: 1f
                    val vertices = params.integerParams["vertices"] ?: 16
                    val divisions = params.integerParams["divisions"] ?: 16
                    return DoubleCone(radius, height, vertices, divisions, color).createModel(params)
                }
                _FlatTopPyramid -> {
                    val size = params.floatParmas["size"] ?: 1f
                    val topScale = params.floatParmas["topScale"] ?: 1f
                    return FlatTopPyramid(size, topScale, color).createModel(params)
                }
                _Pyramid -> {
                    val width = params.floatParmas["width"] ?: 1f
                    val height = params.floatParmas["height"] ?: 1f
                    return Pyramid(width, height, color).createModel(params)
                }
                _Torus -> {
                    val minorRadius = params.floatParmas["minorRadius"] ?: 1f
                    val majorRadius = params.floatParmas["majorRadius"] ?: 0.25f
                    val minorSegments = params.integerParams["minorSegments"] ?: 16
                    val majorSegments = params.integerParams["majorSegments"] ?: 16
                    return Torus(minorRadius, majorRadius, minorSegments, majorSegments, color).createModel(params)
                }
                _UVSphere -> {
                    val radius = params.floatParmas["radius"] ?: 1f
                    val rings = params.integerParams["rings"] ?: 16
                    val segments = params.integerParams["segments"] ?: 16
                    return UVSphere(radius, rings, segments, color).createModel(params)
                }
                _Wedge -> {
                    val radius = params.floatParmas["radius"] ?: 1f
                    val angle = params.floatParmas["angle"] ?: 180f
                    return Wedge(color,radius, angle).createModel(params)
                }
            }

        }
    }

}
