package org.yunghegel.gdx.lwjgl.par

import org.lwjgl.system.MemoryStack.stackPush
import org.lwjgl.util.nfd.NFDFilterItem
import org.lwjgl.util.nfd.NativeFileDialog.*
import org.lwjgl.util.par.ParShapes
import org.lwjgl.util.par.ParShapes.npar_shapes_export
import org.lwjgl.util.par.ParShapesMesh

class ParPrimitives {

    fun createCylinder(slices:Int,stacks:Int) : ParShapesMesh {
        val mesh = ParShapes.par_shapes_create_trefoil_knot(slices,stacks,2f)!!
        ParMeshGdxConverter(mesh)
        return mesh
    }

    private fun exportMesh(mesh:ParShapesMesh) {
        stackPush().use { stack ->
            val filter = NFDFilterItem.malloc(1, stack)
            filter[0]
                .name(stack.UTF8("Wavefront Object"))
                .spec(stack.UTF8("obj"))

            val pp = stack.mallocPointer(1)

            val result: Int = NFD_SaveDialog(pp, filter, null, "mesh.obj")
            when (result) {
                NFD_OKAY -> {
                    val path = pp[0]
                    npar_shapes_export(mesh.address(), path)
                    NFD_FreePath(path)
                }

                NFD_ERROR -> System.err.format("Error: %s\n", NFD_GetError())
                else -> {
                    println("User pressed cancel.")
                }
            }
        }
    }

}
fun main(){
headlessTest {
val par = ParPrimitives()
 val cylinder = par.createCylinder(10, 10)
 val size = cylinder?.npoints()
 val positions = cylinder?.normals(size!! * 3)
 val arr = positions!!.array()
 println(size)
 println(arr)
}
}
