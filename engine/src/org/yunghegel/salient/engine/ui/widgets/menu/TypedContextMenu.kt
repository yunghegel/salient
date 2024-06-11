package org.yunghegel.salient.engine.ui.widgets.menu

import com.kotcrab.vis.ui.widget.MenuItem

class TypedContextMenuItem<In,Out>(name:String , val input: In,val transformer: (In)->Out ) : MenuItem(name) {

    fun clicked() : Out {
        return transformer(input)
    }
}