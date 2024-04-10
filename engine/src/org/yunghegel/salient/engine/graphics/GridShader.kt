package org.yunghegel.salient.engine.graphics

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g3d.Renderable
import com.badlogic.gdx.graphics.g3d.Shader
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader
import com.badlogic.gdx.graphics.g3d.utils.BaseShaderProvider
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder
import com.badlogic.gdx.graphics.glutils.ShaderProgram


val vInfo1 = MeshPartBuilder.VertexInfo()
val vInfo2 = MeshPartBuilder.VertexInfo()
val vInfo3 = MeshPartBuilder.VertexInfo()
val vInfo4 = MeshPartBuilder.VertexInfo()



class GridShader(val config : GridConfig = GridConfig()) : BaseShader(){
    val VERTEX_PATH = "shaders/grid.vert"
    val FRAGMENT_PATH = "shaders/grid.frag"

    val UNIFORM_PROJ_MATRIX = register(Uniform("u_projTrans"))
    val UNIFORM_VIEW_MATRIX = register(Uniform("u_viewTrans"))

    val UNIFORM_CAMERA_POSITION = register(Uniform("u_camPosition"))

    val UNIFORM_NEAR_PLANE = register(Uniform("u_near"))
    val UNIFORM_FAR_PLANE = register(Uniform("u_far"))


    val UNIFORM_GRID_CELL_SIZE = register(Uniform("u_gridCellSize"))
    val UNIFORM_GRID_COLOR_THIN = register(Uniform("u_gridColorThin"))
    val UNIFORM_GRID_COLOR_THICK = register(Uniform("u_gridColorThick"))
    val UNIFORM_GRID_SIZE = register(Uniform("u_gridSize"))
    val UNIFORM_GRID_MIN_PIXELS_BETWEEN_LINES = register(Uniform("u_gridMinPixelsBetweenCells"))

    private var minPixelsBetweenCells = 2.0f
    private var gridCellSize = 0.025f
    private var gridColorThin = Color(0.2f,0.2f,0.2f,1f)
    private var gridColorThick = Color(0.25f,0.25f,0.25f,1f)
    private var gridSize = 10f

    class GridShaderProvider(val config: GridConfig = GridConfig()) : BaseShaderProvider() {
        override fun createShader(renderable: Renderable?): BaseShader {
            return GridShader(config)
        }
    }

    init {
        val vert = Gdx.files.internal(VERTEX_PATH).readString()
        val frag = Gdx.files.internal(FRAGMENT_PATH).readString()
        program = ShaderProgram(vert, frag)
    }

    override fun init() {
        super.init(program,null)
    }

    override fun compareTo(other: Shader?): Int {
        return 0
    }

    override fun canRender(instance: Renderable?): Boolean {
        return true
    }

    private fun setGridUniforms(){
        set(UNIFORM_GRID_CELL_SIZE,config.gridCellSize)
        set(UNIFORM_GRID_COLOR_THIN,config.gridColorThin)
        set(UNIFORM_GRID_COLOR_THICK,config.gridColorThick)
        set(UNIFORM_GRID_SIZE,config.gridSize)
        set(UNIFORM_GRID_MIN_PIXELS_BETWEEN_LINES,config.minPixelsBetweenCells)
    }

    override fun render(renderable: Renderable?) {
        super.render(renderable)
        set(UNIFORM_PROJ_MATRIX,camera.projection)
        set(UNIFORM_VIEW_MATRIX,camera.view)
        set(UNIFORM_CAMERA_POSITION,camera.position.cpy())
        set(UNIFORM_NEAR_PLANE,camera.near)
        set(UNIFORM_FAR_PLANE,camera.far)
        setGridUniforms()
        Gdx.gl.glEnable(GL20.GL_BLEND)
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA,GL20.GL_ONE_MINUS_SRC_ALPHA)


//        ensure that the grid is drawn below everything else

        renderable?.meshPart?.render(program)
        Gdx.gl.glDisable(GL20.GL_BLEND)
        Gdx.gl.glDisable((GL20.GL_DEPTH_TEST))
        Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT)
    }



}

