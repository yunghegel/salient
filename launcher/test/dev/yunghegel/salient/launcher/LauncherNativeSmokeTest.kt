package dev.yunghegel.salient.launcher

import kotlin.test.Test
import kotlin.test.assertTrue

class LauncherNativeSmokeTest {

    @Test
    fun `main prints compiled without throwing`() {
        println("[DEBUG_LOG] Starting Launcher.main smoke test to verify basic execution path.")
        try {
            Launcher.main(emptyArray())
            println("[DEBUG_LOG] Launcher.main executed successfully.")
            assertTrue(true, "Launcher.main should run without exceptions")
        } catch (t: Throwable) {
            println("[DEBUG_LOG] Launcher.main threw exception: ${'$'}{t::class.simpleName}: ${'$'}{t.message}")
            throw t
        }
    }
}
