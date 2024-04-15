package org.yunghegel.salient.engine.graphics

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Frustum
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import net.mgsx.gltf.scene3d.lights.PointLightEx
import net.mgsx.gltf.scene3d.lights.SpotLightEx
import org.yunghegel.salient.engine.system.inject


import kotlin.math.cbrt
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class DebugDrawer {

    val renderer: ShapeRenderer by lazy { inject() }
    val font: BitmapFont   by lazy { inject() }
    var batch: SpriteBatch = SpriteBatch()

    fun drawWireDisc(position: Vector3, axis: Vector3, radius: Float) {
        begin()
        drawWireDiscInternal(position, axis, radius)
        end()
    }

    fun drawWireSphere(position: Vector3, radius: Float) {
        begin()
        drawWireSphereInternal(position, radius)
        end()
    }

    fun drawWireCube(position: Vector3, size: Vector3) {
        begin()
        drawWireCubeInternal(position, size)
        end()
    }

    fun drawWireFrustum(frustum: Frustum) {
        begin()
        drawWireFrustumInternal(frustum)
        end()
    }

    fun drawWireCone(position: Vector3, axis: Vector3, radius: Float, height: Float) {
        begin()
        drawWireConeInternal(position, axis, radius, height)
        end()
    }


    private fun begin() {
        Gdx.gl.glDisable(GL20.GL_DEPTH_TEST)
        Gdx.gl.glLineWidth(1f)

        camera = inject()

        renderer.projectionMatrix = (camera as PerspectiveCamera).combined
        renderer.begin(ShapeRenderer.ShapeType.Line)
        renderer.color = color
    }

    private fun end() {
        renderer.end()

        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST)
    }

    private fun drawWireDiscInternal(position: Vector3, axis: Vector3, radius: Float) {
        val o = Vector3(position)
        val d = Vector3(axis).nor()
        val r = Vector3(0f, 0f, 1f)

        if (axis.epsilonEquals(Vector3.Z)) {
            r[1f, 0f] = 0f
        }

        r.crs(d).nor()
        val u = Vector3(d).crs(r).nor()

        val tau = Math.PI.toFloat() * 2
        val segments = 48
        val step = tau / segments

        val current = Vector3()
        val next = Vector3()

        val a = Vector3()
        val b = Vector3()

        for (i in 0 until segments) {
            val sin = sin((i * step).toDouble()).toFloat() * radius
            val cos = cos((i * step).toDouble()).toFloat() * radius

            a.set(u).scl(cos)
            b.set(r).scl(sin)
            current.set(a).add(b).add(o)

            val nextsin = sin(((i + 1) * step).toDouble()).toFloat() * radius
            val nextcos = cos(((i + 1) * step).toDouble()).toFloat() * radius

            a.set(u).scl(nextcos)
            b.set(r).scl(nextsin)
            next.set(a).add(b).add(o)

            renderer.line(current, next)
        }
    }

    private fun drawWireSphereInternal(position: Vector3, radius: Float) {
        // Draw axially aligned discs
        //drawWireDisc(origin, Vector3.X, radius);
        //drawWireDisc(origin, Vector3.Y, radius);
        //drawWireDisc(origin, Vector3.Z, radius);

        // Draw disc that encompasses sphere according to camera perspective.

        val normal = Vector3(position).sub(camera!!.position)
        val sqrMag = normal.len2()
        val n0 = radius * radius
        val n1 = n0 * n0 / sqrMag
        val n2 = sqrt((n0 - n1).toDouble()).toFloat()

        val a = Vector3(normal).scl(n0 / sqrMag)
        val b = Vector3(position).sub(a)

        drawWireDiscInternal(b, normal, n2)
    }

    private fun drawWireCubeInternal(position: Vector3, size: Vector3) {
        val px = position.x
        val py = position.y
        val pz = position.z
        val sx = size.x
        val sy = size.y
        val sz = size.z

        val p0 = Vector3(px - sx, py - sy, pz - sz)
        val p1 = Vector3(px + sx, py - sy, pz - sz)
        val p2 = Vector3(px + sx, py + sy, pz - sz)
        val p3 = Vector3(px - sx, py + sy, pz - sz)
        val p4 = Vector3(px - sx, py - sy, pz + sz)
        val p5 = Vector3(px + sx, py - sy, pz + sz)
        val p6 = Vector3(px + sx, py + sy, pz + sz)
        val p7 = Vector3(px - sx, py + sy, pz + sz)

        // Bottom
        renderer.line(p0, p1)
        renderer.line(p1, p2)
        renderer.line(p2, p3)
        renderer.line(p3, p0)

        // Top
        renderer.line(p4, p5)
        renderer.line(p5, p6)
        renderer.line(p6, p7)
        renderer.line(p7, p4)

        // Sides
        renderer.line(p0, p4)
        renderer.line(p1, p5)
        renderer.line(p2, p6)
        renderer.line(p3, p7)
    }

    private fun drawWireFrustumInternal(frustum: Frustum) {
        for (i in 0..3) {
            val startPoint = frustum.planePoints[i]
            val endPoint = if (i != 3) frustum.planePoints[i + 1] else frustum.planePoints[0]

            renderer.line(startPoint.x, startPoint.y, startPoint.z, endPoint.x, endPoint.y, endPoint.z)
        }

        for (i in 0..3) {
            val startPoint = frustum.planePoints[i]
            val endPoint = frustum.planePoints[i + 4]

            renderer.line(startPoint.x, startPoint.y, startPoint.z, endPoint.x, endPoint.y, endPoint.z)
        }

        for (i in 4..7) {
            val startPoint = frustum.planePoints[i]
            val endPoint = if (i != 7) frustum.planePoints[i + 1] else frustum.planePoints[4]

            renderer.line(startPoint.x, startPoint.y, startPoint.z, endPoint.x, endPoint.y, endPoint.z)
        }
    }

    private fun drawWireConeInternal(position: Vector3, axis: Vector3, radius: Float, height: Float) {
        drawWireDiscInternal(position, axis, radius)

        val o = Vector3(axis).nor().scl(height).add(position)
        val r = Vector3(Vector3.Z)
        r.crs(axis).nor().scl(radius)
        val s = Vector3(r)
        s.add(position)

        renderer.line(o.x, o.y, o.z, s.x, s.y, s.z)

        s.set(r).scl(-1f)
        s.add(position)

        renderer.line(o.x, o.y, o.z, s.x, s.y, s.z)

        r.nor().crs(axis).scl(radius)
        s.set(r).add(position)

        renderer.line(o.x, o.y, o.z, s.x, s.y, s.z)

        s.set(r).scl(-1f)
        s.add(position)

        renderer.line(o.x, o.y, o.z, s.x, s.y, s.z)
    }

    private fun drawSpotLightInternal(spotLightEx: SpotLightEx) {
        val position = spotLightEx.position
        val direction = spotLightEx.direction
        val radius = spotLightEx.intensity
        val height = spotLightEx.range
        val angle = spotLightEx.cutoffAngle
        val cosAngle = cos(angle.toDouble()).toFloat()
        val sinAngle = sin(angle.toDouble()).toFloat()
        val coneRadius = radius * sinAngle
        val coneHeight = radius * cosAngle

        //line from position to direction scaled by height
        val discCenter = Vector3(direction).nor().scl(height).add(position)
    }

    fun keyValue(pos: Vector2, key: String, value: String, font: BitmapFont) {
        batch.begin()
        font.draw(batch, "$key:$value", pos.x, pos.y)
        batch.end()
    }

    fun drawSpotLight(obj: SpotLightEx) {
        val position = obj.position
        val radius = obj.intensity
        val height = obj.range
        val angle = obj.cutoffAngle
        val cosAngle = cos(angle.toDouble()).toFloat()
        val sinAngle = sin(angle.toDouble()).toFloat()
        val coneRadius = radius * sinAngle
        val coneHeight = radius * cosAngle

        //line from position to direction scaled by height
        val discCenter = Vector3(obj.direction).nor().scl(height).add(position)

        //drawWireDisc(position, obj.direction, radius);
        drawWireCone(position, obj.direction, coneRadius, coneHeight)
        drawWireSphere(position, radius)
        drawWireSphere(discCenter, coneRadius)
    }

    fun drawPointLight(pointLightEx: PointLightEx, selected: Boolean) {
        if (selected) {
            setColor(Color.ORANGE)
        } else {
            setColor(Color(0f, 0f, 0f, 0.5f))
        }
        drawWireSphere(pointLightEx.position, cbrt(pointLightEx.intensity.toDouble()).toFloat())
    }

    companion object {
        private var camera: Camera? = null
        private var color: Color = Color.BLACK

        fun setColor(color: Color) {
            Companion.color = color
        }
    }
}
