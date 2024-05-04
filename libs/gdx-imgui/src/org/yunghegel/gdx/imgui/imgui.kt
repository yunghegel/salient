package org.yunghegel.gdx.imgui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Graphics
import imgui.ImGui
import imgui.ImGuiIO
import imgui.gl3.ImGuiImplGl3
import imgui.glfw.ImGuiImplGlfw
import imgui.type.ImFloat
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL


object imgui {

    var imGuiGlfw: ImGuiImplGlfw = ImGuiImplGlfw()
    var imGuiGl3: ImGuiImplGl3 = ImGuiImplGl3()

    fun init() {
        val windowHandle = (Gdx.graphics as Lwjgl3Graphics).window.windowHandle
        GLFW.glfwMakeContextCurrent(windowHandle)
        GL.createCapabilities()
        ImGui.createContext()
        val io: ImGuiIO = ImGui.getIO()
        io.iniFilename = null
        io.fonts.addFontDefault()
        io.fonts.build()
        imGuiGlfw.init(windowHandle, true)
        imGuiGl3.init("#version 110")
    }

    fun begin() {
        imGuiGlfw.newFrame()
        ImGui.newFrame()
    }

    fun end() {
        ImGui.end()
        ImGui.render()
        imGuiGl3.renderDrawData(ImGui.getDrawData())
    }

}