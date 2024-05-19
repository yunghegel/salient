import com.badlogic.gdx.Game
import types.Test

object SalientTests : MutableMap<String, Test> by mutableMapOf() {

    var current: Test? = null

    fun hookCurrent(test: Test, game: Game) {
        current = test
        game.screen = test
        set(test.name, test)
        if (test.name !in SalientTests) {
            SalientTests[test.name] = test
        }
        println("hooked ${test.name} to game")
    }


}