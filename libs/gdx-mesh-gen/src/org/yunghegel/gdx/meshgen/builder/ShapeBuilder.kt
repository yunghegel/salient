package org.yunghegel.gdx.meshgen.builder

import com.badlogic.gdx.graphics.Mesh
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.model.MeshPart
import com.badlogic.gdx.graphics.g3d.model.Node
import com.badlogic.gdx.graphics.g3d.model.NodePart
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.GdxRuntimeException


    public class ShapeBuilder {
        /** The model currently being build  */
        private var model: Model? = null

        /** The node currently being build  */
        private var node: Node? = null

        /** The mesh builders created between begin and end  */
        private val builders = Array<ShapeMeshBuilder>()

        private val tmpTransform = Matrix4()

        private fun getBuilder(attributes: VertexAttributes): ShapeMeshBuilder {
            for (mb in builders) if (mb.attributes == attributes && mb.lastIndex() < MeshBuilder.MAX_VERTICES / 2) return mb
            val result = ShapeMeshBuilder()
            result.begin(attributes)
            builders.add(result)
            return result
        }

        /** Begin building a new model  */
        fun begin() {
            if (model != null) throw GdxRuntimeException("Call end() first")
            node = null
            model = Model()
            builders.clear()
        }

        /** End building the model.
         * @return The newly created model. Call the [Model.dispose] method when no longer used.
         */
        fun end(): Model? {
            if (model == null) throw GdxRuntimeException("Call begin() first")
            val result= model
            endnode()
            model = null

            for (mb in builders) mb.end()
            builders.clear()

            if (result != null) {
                rebuildReferences(result)
            }
            return result
        }

        private fun endnode() {
            if (node != null) {
                node = null
            }
        }

        /** Adds the [Node] to the model and sets it active for building. Use any of the part(...) method to add a NodePart.  */
        protected fun node(node: Node?): Node? {
            if (model == null) throw GdxRuntimeException("Call begin() first")

            endnode()

            model!!.nodes.add(node)
            this.node = node

            return node
        }

        /** Add a node to the model. Use any of the part(...) method to add a NodePart.
         * @return The node being created.
         */
        fun node(): Node {
            val node = Node()
            node(node)
            node.id = "node" + model!!.nodes.size
            return node
        }

        /** Adds the nodes of the specified model to a new node of the model being build. After this method the given model can no
         * longer be used. Do not call the [Model.dispose] method on that model.
         * @return The newly created node containing the nodes of the given model.
         */
        fun node(id: String?, model: Model): Node {
            val node = Node()
            node.id = id
            node.addChildren(model.nodes)
            node(node)
            for (disposable in model.managedDisposables) manage(disposable)
            return node
        }

        /** Add the [Disposable] object to the model, causing it to be disposed when the model is disposed.  */
        fun manage(disposable: Disposable?) {
            if (model == null) throw GdxRuntimeException("Call begin() first")
            model!!.manageDisposable(disposable)
        }

        /** Adds the specified MeshPart to the current Node. The Mesh will be managed by the model and disposed when the model is
         * disposed. The resources the Material might contain are not managed, use [.manage] to add those to the
         * model.  */
        fun part(meshpart: MeshPart?, material: Material?) {
            if (node == null) node()
            node!!.parts.add(NodePart(meshpart, material))
        }

        /** Adds the specified mesh part to the current node. The Mesh will be managed by the model and disposed when the model is
         * disposed. The resources the Material might contain are not managed, use [.manage] to add those to the
         * model.
         * @return The added MeshPart.
         */
        fun part(id: String?, mesh: Mesh?, primitiveType: Int, offset: Int, size: Int, material: Material?): MeshPart {
            val meshPart = MeshPart()
            meshPart.id = id
            meshPart.primitiveType = primitiveType
            meshPart.mesh = mesh
            meshPart.offset = offset
            meshPart.size = size
            part(meshPart, material)
            return meshPart
        }

        /** Adds the specified mesh part to the current node. The Mesh will be managed by the model and disposed when the model is
         * disposed. The resources the Material might contain are not managed, use [.manage] to add those to the
         * model.
         * @return The added MeshPart.
         */
        fun part(id: String?, mesh: Mesh, primitiveType: Int, material: Material?): MeshPart {
            return part(id, mesh, primitiveType, 0, mesh.numIndices, material)
        }

        /** Creates a new MeshPart within the current Node and returns a [MeshPartBuilder] which can be used to build the shape
         * of the part. If possible a previously used [MeshPartBuilder] will be reused, to reduce the number of mesh binds.
         * Therefore you can only build one part at a time. The resources the Material might contain are not managed, use
         * [.manage] to add those to the model.
         * @return The [MeshPartBuilder] you can use to build the MeshPart.
         */
        fun part(id: String?, primitiveType: Int, attributes: VertexAttributes, material: Material?): ShapeMeshBuilder {
            val builder = getBuilder(attributes)
            part(builder.part(id, primitiveType), material)
            return builder
        }

        /** Creates a new MeshPart within the current Node and returns a [MeshPartBuilder] which can be used to build the shape
         * of the part. If possible a previously used [MeshPartBuilder] will be reused, to reduce the number of mesh binds.
         * Therefore you can only build one part at a time. The resources the Material might contain are not managed, use
         * [.manage] to add those to the model.
         * @param attributes bitwise mask of the [com.badlogic.gdx.graphics.VertexAttributes.Usage], only Position, Color, Normal
         * and TextureCoordinates is supported.
         * @return The [MeshPartBuilder] you can use to build the MeshPart.
         */
        fun part(id: String?, primitiveType: Int, attributes: Long, material: Material?): ShapeMeshBuilder {
            return part(id, primitiveType, MeshBuilder.createAttributes(attributes), material)
        }

      
  
   


      

        /** Resets the references to [Material]s, [Mesh]es and [MeshPart]s within the model to the ones used within
         * it's nodes. This will make the model responsible for disposing all referenced meshes.  */
        fun rebuildReferences(model: Model) {
            model.materials.clear()
            model.meshes.clear()
            model.meshParts.clear()
            for (node in model.nodes) rebuildReferences(model, node)
        }

        private fun rebuildReferences(model: Model, node: Node) {
            for (mpm in node.parts) {
                if (!model.materials.contains(mpm.material, true)) model.materials.add(mpm.material)
                if (!model.meshParts.contains(mpm.meshPart, true)) {
                    model.meshParts.add(mpm.meshPart)
                    if (!model.meshes.contains(mpm.meshPart.mesh, true)) model.meshes.add(mpm.meshPart.mesh)
                    model.manageDisposable(mpm.meshPart.mesh)
                }
            }
            for (child in node.children) rebuildReferences(model, child)
        }

   
       

    }
