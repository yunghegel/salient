package org.yunghegel.salient.engine.ui.widgets.notif

import com.badlogic.gdx.graphics.Color

enum class Severity(val color: Color) {

    INFO(Color.WHITE),
    WARNING(Color.YELLOW),
    ERROR(Color.ORANGE),
    CRITICAL(Color.RED)

}