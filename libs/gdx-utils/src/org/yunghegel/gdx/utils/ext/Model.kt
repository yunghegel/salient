package org.yunghegel.gdx.utils.ext

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.Renderable
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute
import com.badlogic.gdx.graphics.g3d.model.Node
import com.badlogic.gdx.graphics.g3d.model.NodePart
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.BoxShapeBuilder
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.collision.BoundingBox
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.Pool
import net.mgsx.gltf.scene3d.attributes.PBRColorAttribute
import org.yunghegel.gdx.utils.Pools
import java.util.function.Consumer

fun eachNodeRecusrsive(nodes: Iterable<Node>, callback: Consumer<Node>) {
    for (node in nodes) {
        callback.accept(node)
        if (node.hasChildren()) eachNodeRecusrsive(node.children, callback)
    }
}

fun eachNodeRecusrsive(node: Node, callback: Consumer<Node>) {
    callback.accept(node)
    if (node.hasChildren()) eachNodeRecusrsive(node.children, callback)
}

fun collectNodeParts(modelInstance: ModelInstance): Array<NodePart> {
    val results = Array<NodePart>()
    eachNodeRecusrsive(
        modelInstance.nodes
    ) { node: Node ->
        results.addAll(
            node.parts
        )
    }
    return results
}

fun collectNodes(modelInstance: ModelInstance): Array<Node> {
    val results = Array<Node>()
    eachNodeRecusrsive(
        modelInstance.nodes
    ) { node: Node ->
        results.add(
            node
        )
    }
    return results
}

fun collectNodes(model: Model): Array<Node> {
    val results = Array<Node>()
    eachNodeRecusrsive(
        model.nodes
    ) { node: Node ->
        results.add(
            node
        )
    }
    return results
}

fun eachNodePartRecusrsive(nodes: Iterable<Node>, callback:(NodePart)->Unit) {
    eachNodeRecusrsive(
        nodes
    ) { node: Node ->
        for (part in node.parts) {
            callback(part)
        }
    }
}

fun eachNodePartRecusrsive(node: Node, callback:(NodePart)->Unit) {
    eachNodeRecusrsive(
        node
    ) { n: Node ->
        for (part in n.parts) {
            callback(part)
        }
    }
}

private val renderables = Array<Renderable>()
private val pool: Pool<Renderable> = Pools.RenderablePool()
private val tmpVec0 = Vector3()

/**
 * Gets the total bone count for the given model based on having
 * one renderable.
 *
 * @param model the model to count bones for
 * @return the bone count
 */
fun getBoneCount(model: Model?): Int {
    var numBones = 0

    val instance = ModelInstance(model)
    instance.getRenderables(renderables, pool)

    // Bones appear to be copied to each NodePart
    // So we just count the first renderable that has bones
    // and break
    for (renderable in renderables) {
        if (renderable.bones != null) {
            numBones += renderable.bones.size
            break
        }
    }

    renderables.clear()
    pool.clear()

    return numBones
}

fun isVisible(cam: Camera, modelInstance: ModelInstance, center: Vector3?, radius: Float): Boolean {
    tmpVec0.set(center).mul(modelInstance.transform)
    return cam.frustum.sphereInFrustum(tmpVec0, radius)
}

/**
 * Checks if visible to camera using boundsInFrustum and dimensions
 */
fun isVisible(cam: Camera, modelInstance: ModelInstance, center: Vector3?, dimensions: Vector3?): Boolean {
    modelInstance.transform.getTranslation(tmpVec0)
    tmpVec0.add(center)
    return cam.frustum.boundsInFrustum(tmpVec0, dimensions)
}

fun getVerticesCount(model: Model): Int {
    var vertices = 0
    for (mesh in model.meshes) {
        vertices += mesh.numVertices
    }
    return vertices
}

/**
 * Get Indices count of a Model
 */
fun getIndicesCount(model: Model): Float {
    var indices = 0
    for (mesh in model.meshes) {
        indices += mesh.numIndices
    }
    return indices.toFloat()
}

fun fatten(model: Model, amount: Float) {
    val pos = Vector3()
    val nor = Vector3()
    for (node in model.nodes) {
        for (n in node.parts) {
            val mesh = n.meshPart.mesh
            val buf = mesh.verticesBuffer
            val lastFloat = mesh.numVertices * mesh.vertexSize / 4
            val vertexFloats = (mesh.vertexSize / 4)
            val posAttr = mesh.vertexAttributes.findByUsage(VertexAttributes.Usage.Position)
            val norAttr = mesh.vertexAttributes.findByUsage(VertexAttributes.Usage.Normal)
            require(!(posAttr == null || norAttr == null)) { "Position/normal vertex attribute not found" }
            val pOff = posAttr.offset / 4
            val nOff = norAttr.offset / 4

            var i = 0
            while (i < lastFloat) {
                pos.x = buf[pOff + i]
                pos.y = buf[pOff + i + 1]
                pos.z = buf[pOff + i + 2]

                nor.x = buf[nOff + i]
                nor.y = buf[nOff + i + 1]
                nor.z = buf[nOff + i + 2]

                nor.nor().scl(amount)

                buf.put(pOff + i, pos.x + nor.x)
                buf.put(pOff + i + 1, pos.y + nor.y)
                buf.put(pOff + i + 2, pos.z + nor.z)
                i += vertexFloats
            }
        }
    }
}

fun createBoundsRenderable(boundingBox: BoundingBox?): ModelInstance {
    val modelBuilder = ModelBuilder()
    modelBuilder.begin()
    val builder = modelBuilder.part(
        "BoundingBox",
        GL20.GL_LINES,
        (VertexAttributes.Usage.Position or VertexAttributes.Usage.ColorUnpacked).toLong(),
        Material().apply {
            set(BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA))
            set(BlendingAttribute(0.5f))
        }
    )
    builder.setColor(Color.CORAL)
    BoxShapeBuilder.build(builder, boundingBox)
    val boundingBoxModel = modelBuilder.end()

    return ModelInstance(boundingBoxModel)
}

fun createRayRenderable(origin: Vector3?, direction: Vector3, length: Float, color: Color?): ModelInstance {
    val modelBuilder = ModelBuilder()
    modelBuilder.begin()
    val material = Material(PBRColorAttribute.createDiffuse(color))
    val builder = modelBuilder.part(
        "ray",
        GL20.GL_LINES,
        (VertexAttributes.Usage.Position or VertexAttributes.Usage.ColorUnpacked).toLong(),
        material
    )
    builder.setColor(color)

    builder.line(origin, direction.scl(length).add(origin))
    val model = modelBuilder.end()
    return ModelInstance(model)
}

val Model.boundingRadius get() = run {
    var radius = 0f

    for (mesh in meshes) {
        val r = mesh.calculateRadius(Vector3(), 0, mesh.numIndices, Matrix4().idt())
        if (r > radius) radius = r
    }
    radius
}

val Model.instance get() = ModelInstance(this)