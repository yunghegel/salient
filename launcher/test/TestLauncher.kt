import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import kotlin.test.Test

class TestLauncher {
    @Test fun main() {
        val cfg = Lwjgl3ApplicationConfiguration()
        cfg.run {
            setWindowedMode(1280,720)
            setOpenGLEmulation(Lwjgl3ApplicationConfiguration.GLEmulation.GL32,3,2)
            setBackBufferConfig(8,8,8,8,24,8,8)

        }
        Lwjgl3Application(TestApplication(),cfg)
    }
}
