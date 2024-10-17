import com.badlogic.gdx.Game
import types.Test

object SalientTests : MutableMap<String, Test> by mutableMapOf() {

    var current: Test? = null

    fun hookCurrent(test: Test, game: Game) {

    }


}