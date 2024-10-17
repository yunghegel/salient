package org.yunghegel.salient.editor.plugins.intersect.tools

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.math.Plane
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.viewport.ScreenViewport
import org.yunghegel.gdx.utils.Pools
import org.yunghegel.gdx.utils.ext.int
import org.yunghegel.salient.editor.app.Gui
import org.yunghegel.salient.editor.input.ViewportController
import org.yunghegel.salient.editor.input.delegateInput
import org.yunghegel.salient.editor.input.undelegateInput
import org.yunghegel.salient.editor.plugins.intersect.lib.IntersectionQuery
import org.yunghegel.salient.engine.helpers.TextRenderer.camera
import org.yunghegel.salient.engine.system.Netgraph
import org.yunghegel.salient.engine.system.inject
import org.yunghegel.salient.engine.api.tool.ClickTool
import org.yunghegel.salient.engine.input.Input
import org.yunghegel.salient.engine.ui.UI

class IntersectorTool : ClickTool("intersector_tool") {

    val viewport : ScreenViewport = inject()

    var lastResult : IntersectionResult? = null
    var tmp : IntersectionResult? = null

    val gui : Gui by lazy {inject()}

    val viewportcontroller : ViewportController by lazy {inject()}

    override fun create(position: Vector2?) {


    }

    init {
        activate()
    }

    override fun activate() {
        delegateInput(listener = this )
        super.activate()
    }

    override fun deactivate() {
        undelegateInput(listener = this)

        super.deactivate()
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if(key == Input.Keys.CONTROL_LEFT) {
            viewportcontroller.moveTo(lastResult?.intersection ?: Vector3.Zero ,1f)
        }
        return super.touchDown(screenX, screenY, pointer, button)
    }

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {

        lastResult = query(Gdx.input.x,Gdx.input.y,IntersectionQuery.PLANE_XZ)
//        val x = screenX - viewport.screenX
//        val y = screenY - (Gdx.graphics.height - (viewport.screenY + viewport.screenHeight))
//        tmp = query(x,y,IntersectionQuery.PLANE_XZ)

        return true
    }

    fun query(type:IntersectionQuery,callback:((IntersectionResult)->Unit)?=null) : IntersectionResult? {
        return query(Gdx.input.x,Gdx.input.y,type,callback)
    }

    fun query(x:Int, y:Int, type:IntersectionQuery,callback:((IntersectionResult)->Unit)?=null) : IntersectionResult? {
        val query = Pools.vector2Pool.obtain().apply { set(x.toFloat(),y.toFloat()) }

        val ray = viewport.getPickRay(query.x,query.y)
        val res = Pools.vector3Pool.obtain()
        val intersection = when(type) {
            IntersectionQuery.PLANE_XY -> {
                Intersector.intersectRayPlane(ray,plane(type),res)
            }
            IntersectionQuery.PLANE_XZ -> {
                Intersector.intersectRayPlane(ray,plane(type),res)
            }
            IntersectionQuery.PLANE_YZ -> {
                Intersector.intersectRayPlane(ray,plane(type),res)
            }
            else -> null
        }
        if (intersection != null){
            val result = IntersectionResult(res,query)
            callback?.let { it(result) }
            return result
        }
        return null
    }

    fun plane(type: IntersectionQuery) : Plane {
        val normal = when(type) {
            IntersectionQuery.PLANE_XY -> Vector3(0f,0f,1f)
            IntersectionQuery.PLANE_XZ -> Vector3(0f,1f,0f)
            IntersectionQuery.PLANE_YZ -> Vector3(1f,0f,0f)
            else -> Vector3()
        }
        return Plane(normal,0f)
    }



    override fun render(shapeRenderer: ShapeRenderer) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        shapeRenderer.projectionMatrix = camera.combined
        lastResult?.let {
            drawResult(shapeRenderer,it)
        }
        tmp?.let {
            drawResult(shapeRenderer,it)
        }
        shapeRenderer.end()
    }

   fun drawResult(shapeRenderer: ShapeRenderer, result: IntersectionResult) {

        shapeRenderer.setColor(1f,0f,0f,1f)
        shapeRenderer.line(result.intersection,Vector3(result.intersection).add(0f,1f,0f))
        shapeRenderer.setColor(0f,0f,1f,1f)
        shapeRenderer.line(result.intersection,Vector3(result.intersection).add(1f,0f,0f))
        shapeRenderer.setColor(0f,1f,0f,1f)
        shapeRenderer.line(result.intersection,Vector3(result.intersection).add(0f,0f,1f))
    }


    inner class IntersectionResult(val intersection: Vector3, val query: Vector2) {
        val dst = camera.position.dst(intersection)
        val dst2 = camera.position.dst2(intersection)

        override fun toString(): String {
            return "[intersection=$intersection., query=$query]"
        }

        fun draw(shapeRenderer: ShapeRenderer) {
            drawResult(shapeRenderer,this)
        }

    }



}