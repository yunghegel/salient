package org.yunghegel.salient.engine.system.file

import java.awt.Desktop
import java.io.File
import java.io.IOException


object FileOpener {

    fun open(file: File) {
        if (!openSystem(file.getPath()) && !openDESKTOP(file)) System.err.println(
            "unable to open file " + System.getProperty(
                "os.name"
            )
        )
    }

    private fun openSystem(what: String): Boolean {
        val os = System.getProperty("os.name").lowercase()



        if (os.contains("win")) {
            return (run("explorer", "%s", what))
        }



        if (os.contains("mac")) {
            return (run("open", "%s", what))
        }



        return run("kde-open", "%s", what) || run("gnome-open", "%s", what) || run("xdg-open", "%s", what)
    }


    private fun openDESKTOP(file: File): Boolean {
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
            try {
                Desktop.getDesktop().open(file)

                return true
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        return false
    }


    private fun run(command: String, arg: String, file: String): Boolean {
        val args = arg.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        val parts = arrayOfNulls<String>(args.size + 1)

        parts[0] = command

        for (i in args.indices) {
            parts[i + 1] = String.format(args[0], file).trim { it <= ' ' }
        }



        try {
            val p = Runtime.getRuntime().exec(parts) ?: return false



            try {
                if (p.exitValue() == 0) return true

                return false
            } catch (itse: IllegalThreadStateException) {
                return true
            }
        } catch (e: IOException) {
            //e.printStackTrace();

            return false
        }
    }
}