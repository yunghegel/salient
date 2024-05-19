package org.yunghegel.salient.engine.api.tool

abstract class KeyTool(name:String) : InputTool(name) {

    val keymap : HashMap<Int,()->Unit> = HashMap()

    override fun keyDown(keycode: Int): Boolean {
        if(keymap.containsKey(keycode)){
            keymap[keycode]?.invoke()
            return true
        }
        return super.keyDown(keycode)
    }

    protected abstract fun run(keycode:Int)

}