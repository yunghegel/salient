//import imgui.classes.Context
//import imgui.impl.gl.ImplGL3
//import imgui.impl.glfw.ImplGlfw
//import org.lwjgl.glfw.GLFW
//import org.lwjgl.opengl.GL
//import org.lwjgl.opengl.GL11
//import uno.glfw.GlfwWindow

//class ImGuiTest : ApplicationAdapter()  {
//
//    private var lwjglGlfw: ImplGlfw? = null
//    private var implgl3: ImplGL3? = null
//    private var ctx: Context? = null
//    private var window: GlfwWindow? = null
//    private var windowHandle: Long = 0
//
//    override fun create() {
//        GL.createCapabilities()
//        ctx = Context()
//        windowHandle = (Gdx.graphics as Lwjgl3Graphics).window.windowHandle
//        window = GlfwWindow(windowHandle)
//        GLFW.glfwMakeContextCurrent(windowHandle)
//        lwjglGlfw = ImplGlfw(window!!, true, null)
//        implgl3 = ImplGL3()
//        ImGui.styleColorsDark(null)
//    }
//
//    override fun render() {
//
//        implgl3!!.newFrame()
//        lwjglGlfw!!.newFrame()
//
//        ImGui.run {
//            newFrame()
//            text("Hello, LibGDX")
//            render()
//
//            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT)
//            drawData?.let { implgl3!!.renderDrawData(it) }
//        }
//    }
//
//    override fun dispose() {
//        lwjglGlfw!!.shutdown()
//        ctx!!.shutdown()
//    }
//
//}
//
//fun main() {
//    val config = Lwjgl3ApplicationConfiguration()
//    Lwjgl3Application(ImGuiTest(), config)
//}