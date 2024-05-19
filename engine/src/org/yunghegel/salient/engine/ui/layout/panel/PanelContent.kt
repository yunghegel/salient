package org.yunghegel.salient.engine.ui.layout.panel

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.badlogic.gdx.utils.OrderedSet
import org.yunghegel.gdx.utils.ext.ref
import org.yunghegel.salient.engine.ui.layout.ConstrainedMultiSplitPane
import org.yunghegel.salient.engine.ui.scene2d.STable

class PanelContent(vararg val actors: Actor) : Container<ConstrainedMultiSplitPane>() {



    class Config {
        var numActors by ref(0)
        var actors = OrderedSet<STable>()
    }

}