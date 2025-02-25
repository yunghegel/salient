package org.yunghegel.salient.editor.ui.data

import com.badlogic.gdx.Gdx
import org.yunghegel.salient.engine.ui.layout.TimeSeriesGraphActor
import org.yunghegel.salient.engine.ui.scene2d.STable

class ProfilerPanel : STable() {

    val performance: TimeSeriesGraphActor = TimeSeriesGraphActor()

    init {
        performance.setValueProvider { Gdx.graphics.deltaTime }
        performance.setUpdateInterval(0.1f)
        add(performance).grow()
    }

}