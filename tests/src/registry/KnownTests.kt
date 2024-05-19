package registry

import com.badlogic.gdx.Game
import types.Test

object KnownTests : Map<String, Test> by mutableMapOf() {

    var old : Test? = null

    fun hookCurrent(game: Game, test :Test) {
        game.screen = test
        old?.let{ it.destroy() }
        old = test

    }

}