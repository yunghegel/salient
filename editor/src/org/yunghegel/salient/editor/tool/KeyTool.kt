package org.yunghegel.salient.editor.tool

abstract class KeyTool(name:String, val key: Int) : InputTool(name) {


    override fun keyDown(keycode: Int): Boolean {
        if (keycode == key) {
            run()
        }
        return super.keyDown(keycode)
    }

    protected abstract fun run()

}