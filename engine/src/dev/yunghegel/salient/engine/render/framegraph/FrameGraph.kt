package dev.yunghegel.salient.engine.render.framegraph

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.Pool

class FrameGraph : Disposable {

    // Describes the properties needed to create a FrameBuffer
    data class FboDescriptor(
        val width: Int,
        val height: Int,
        val format: Pixmap.Format
    )

    // A single rendering pass with its dependencies and logic
    private class RenderPass(
        val name: String,
        val reads: Set<FrameGraphResource>,
        val writes: Set<FrameGraphResource>,
        val setup: (builder: Builder) -> Unit,
        val execute: (resources: ResourceProvider) -> Unit
    )

    // DSL builder for defining a pass
    class Builder(val name: String) {
        internal val reads = mutableSetOf<FrameGraphResource>()
        internal val writes = mutableSetOf<FrameGraphResource>()
        internal val resourceRegistry = mutableMapOf<FrameGraphResource, FboDescriptor>()

        fun reads(resource: FrameGraphResource) {
            reads.add(resource)
        }

        // Declares that a pass writes to a resource and describes its properties
        fun writesTo(resource: FrameGraphResource, width: Int, height: Int, format: Pixmap.Format = Pixmap.Format.RGBA8888) {
            writes.add(resource)
            resourceRegistry[resource] = FboDescriptor(width, height, format)
        }
    }

    // Provides access to concrete FrameBuffers during pass execution
    class ResourceProvider(private val activeFbos: Map<FrameGraphResource, FrameBuffer>) {
        fun getFbo(resource: FrameGraphResource): FrameBuffer? = activeFbos[resource]
        fun getTexture(resource: FrameGraphResource) = activeFbos[resource]?.colorBufferTexture
    }

    private val passes = mutableListOf<RenderPass>()
    private var executionOrder = listOf<RenderPass>()

    // Pools FrameBuffers by their descriptors for efficient reuse
    private val fboPools = mutableMapOf<FboDescriptor, Pool<FrameBuffer>>()

    /**
     * Adds a new rendering pass to the graph.
     */
    fun addPass(name: String, setup: Builder.() -> Unit, execute: (resources: ResourceProvider) -> Unit) {
        val builder = Builder(name)
        builder.setup()
        passes.add(RenderPass(name, builder.reads, builder.writes, setup, execute))
    }

    /**
     * Analyzes pass dependencies and determines the execution order.
     * This must be called after adding all passes and before executing.
     */
    fun compile() {
        if (passes.isEmpty()) return

        // Simple topological sort (Kahn's algorithm)
        val sorted = mutableListOf<RenderPass>()
        val dependencies = passes.associateWith { mutableSetOf<RenderPass>() }.toMutableMap()
        val graph = passes.associateWith { mutableListOf<RenderPass>() }.toMutableMap()

        // Build dependency graph
        for (pass in passes) {
            for (dependency in passes) {
                if (pass === dependency) continue
                // If 'pass' reads a resource that 'dependency' writes to,
                // then 'dependency' must run before 'pass'.
                if (pass.reads.any { it in dependency.writes }) {
                    graph[dependency]?.add(pass)
                    dependencies[pass]?.add(dependency)
                }
            }
        }

        val queue = passes.filter { dependencies[it]?.isEmpty() == true }.toMutableList()

        while (queue.isNotEmpty()) {
            val current = queue.removeFirst()
            sorted.add(current)
            graph[current]?.forEach { neighbor ->
                dependencies[neighbor]?.remove(current)
                if (dependencies[neighbor]?.isEmpty() == true) {
                    queue.add(neighbor)
                }
            }
        }

        if (sorted.size != passes.size) {
            error("FrameGraph has a cycle and cannot be compiled.")
        }
        executionOrder = sorted
        println("FrameGraph compiled. Execution order:")
        executionOrder.forEachIndexed { index, pass -> println("  ${index + 1}. ${pass.name}") }
    }

    /**
     * Executes the compiled rendering passes in order.
     */
    fun execute() {
        val activeFbos = mutableMapOf<FrameGraphResource, FrameBuffer>()
        val usedFbos = mutableListOf<Pair<Pool<FrameBuffer>, FrameBuffer>>()

        for (pass in executionOrder) {
            // Obtain FrameBuffers for the resources this pass writes to
            for (writeTarget in pass.writes) {
                // Don't create an FBO for the screen backbuffer
                if (writeTarget == FrameGraphResource.BACKBUFFER) continue

                // Find the descriptor for this resource
                val descriptor = passes.flatMap { it.setup.let { setup ->
                    val builder = Builder(it.name).also(setup)
                    builder.resourceRegistry.entries
                }}.first { it.key == writeTarget }.value


                // Get or create a pool for this descriptor
                val pool = fboPools.getOrPut(descriptor) {
                    object : Pool<FrameBuffer>() {
                        override fun newObject(): FrameBuffer = FrameBuffer(descriptor.format, descriptor.width, descriptor.height, false)
                    }
                }

                val fbo = pool.obtain()
                activeFbos[writeTarget] = fbo
                usedFbos.add(pool to fbo)
            }

            val resourceProvider = ResourceProvider(activeFbos)
            
            // Bind the correct framebuffer
            val writeFbo = if (pass.writes.contains(FrameGraphResource.BACKBUFFER)) null else resourceProvider.getFbo(pass.writes.first())
            writeFbo?.begin()

            // Execute the pass's rendering logic
            pass.execute(resourceProvider)

            writeFbo?.end()
        }

        // Free all used FBOs back to their pools for the next frame
        usedFbos.forEach { (pool, fbo) -> pool.free(fbo) }
    }

    override fun dispose() {
        fboPools.values.forEach { pool ->
            // LibGDX Pools don't have a clear/dispose method that disposes objects,
            // so we'd need a custom pool or manual tracking if FBOs need disposal.
            // For simplicity, we assume the app lifecycle handles this.
        }
        fboPools.clear()
    }
}
