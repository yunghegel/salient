package org.yunghegel.gdx.utils.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.utils.UIUtils
import com.kotcrab.vis.ui.util.dialog.InputDialogListener
import ktx.collections.GdxArray
import org.lwjgl.PointerBuffer
import org.lwjgl.system.MemoryStack
import org.lwjgl.util.tinyfd.TinyFileDialogs
import org.yunghegel.gdx.utils.ext.colorToHex
import org.yunghegel.gdx.utils.ext.stringToUnsignedCharByteBuffer


object Dialogs {

    fun multipleFileDialog(
        title: String?,
        default: String = System.getProperty("user.home"),
        filterPatterns: Array<String>?,
        filterDescription: String?
    ): GdxArray<FileHandle>? {
        //fix file path characters
        var defaultPath = default
        if (UIUtils.isWindows) {
            defaultPath = defaultPath.replace("/", "\\")
            if (!defaultPath.endsWith("\\")) defaultPath += "\\"
        } else {
            defaultPath = defaultPath.replace("\\", "/")
            if (!defaultPath.endsWith("/")) defaultPath += "/"
        }

        val stack = MemoryStack.stackPush()

        var filtersPointerBuffer: PointerBuffer? = null
        if (filterPatterns != null) {
            filtersPointerBuffer = stack.mallocPointer(filterPatterns.size)

            for (i in filterPatterns.indices) {
                filtersPointerBuffer.put(stack.UTF8("*." + filterPatterns[i]))
            }
            filtersPointerBuffer.flip()
        }

        val response =
            TinyFileDialogs.tinyfd_openFileDialog(title, defaultPath, filtersPointerBuffer, filterDescription, true)
                ?: return null

        val strings = response.split("\\|".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val returnValue = GdxArray<FileHandle>()
        for (string in strings) {
            val file = Gdx.files.absolute(string)
            if (!file.exists()) continue
            returnValue.add(file)
        }

        if (returnValue.isEmpty) return null

        return returnValue
    }

    fun openFileDialog(
        title: String?,
        default: String = System.getProperty("user.home"),
        filterPatterns: Array<String>?,
        filterDescription: String?
    ): FileHandle? {
        //fix file path characters
        var defaultPath = default
        if (UIUtils.isWindows) {
            defaultPath = defaultPath.replace("/", "\\")
            if (!defaultPath.endsWith("\\")) defaultPath += "\\"
        } else {
            defaultPath = defaultPath.replace("\\", "/")
            if (!defaultPath.endsWith("/")) defaultPath += "/"
        }

        val stack = MemoryStack.stackPush()

        var filtersPointerBuffer: PointerBuffer? = null
        if (filterPatterns != null) {
            filtersPointerBuffer = stack.mallocPointer(filterPatterns.size)

            for (i in filterPatterns.indices) {
                filtersPointerBuffer.put(stack.UTF8("*." + filterPatterns[i]))
            }
            filtersPointerBuffer.flip()
        }

        val response =
            TinyFileDialogs.tinyfd_openFileDialog(title, defaultPath, filtersPointerBuffer, filterDescription, true)
                ?: return null

        val file = Gdx.files.absolute(response)

        if (!file.exists()) return null

        return file
    }

    fun saveFileDialog(
        title: String?,
        default: String = System.getProperty("user.home"),
        defaultName: String?,
        filterPatterns: Array<String>?,
        filterDescription: String?
    ): FileHandle? {
        //fix file path characters
        var defaultPath = default
        if (UIUtils.isWindows) {
            defaultPath = defaultPath.replace("/", "\\")
            if (!defaultPath.endsWith("\\")) defaultPath += "\\"
        } else {
            defaultPath = defaultPath.replace("\\", "/")
            if (!defaultPath.endsWith("/")) defaultPath += "/"
        }

        val stack = MemoryStack.stackPush()

        var filtersPointerBuffer: PointerBuffer? = null
        if (filterPatterns != null) {
            filtersPointerBuffer = stack.mallocPointer(filterPatterns.size)

            for (i in filterPatterns.indices) {
                filtersPointerBuffer.put(stack.UTF8("*." + filterPatterns[i]))
            }
            filtersPointerBuffer.flip()
        }

        val response =
            TinyFileDialogs.tinyfd_saveFileDialog(title, defaultPath, filtersPointerBuffer, filterDescription)
                ?: return null

        return Gdx.files.absolute(response)
    }

}

enum class MessageType(val tinyfdString: String) {
    OK("ok"),
    OK_CANCEL("okcancel"),
    YES_NO("yesno"),
    YES_NO_CANCEL("yesnocancel")
}

enum class IconType(val tinyfdString: String) {
    INFO("info"),
    WARNING("warning"),
    ERROR("error"),
    QUESTION("question")
}

fun openFiles(title: String?, default: String = System.getProperty("user.home"), filterPatterns: Array<String>?, filterDescription: String?, handler: (FileHandle)->Unit): GdxArray<FileHandle>? {
    val files = Dialogs.multipleFileDialog(title, default, filterPatterns, filterDescription)
    files?.forEach { handler(it) }
    return files
}

fun openFile(title: String?, default: String = System.getProperty("user.home"), filterPatterns: Array<String>?, filterDescription: String?, handler: (FileHandle)->Unit): FileHandle? {
    val file = Dialogs.openFileDialog(title, default, filterPatterns, filterDescription)
    file?.let { handler(it) }
    return file
}

fun saveFileAs(title: String?, default: String = System.getProperty("user.home"), defaultName: String?, filterPatterns: Array<String>?, filterDescription: String?, handler: (FileHandle)->Unit): FileHandle? {
    val file = Dialogs.saveFileDialog(title, default, defaultName, filterPatterns, filterDescription)
    file?.let { handler(it) }
    return file
}

class DialogInputListenerBuilder {
    var onFinished: ((String) -> Unit)? = null
    var onCancelled: (() -> Unit)? = null

    fun onFinished(block: (String) -> Unit) {
        onFinished = block
    }

    fun onCancelled(block: () -> Unit) {
        onCancelled = block
    }
}

fun dialogInput(title: CharSequence, message: CharSequence, default: CharSequence, listenerBuilder: DialogInputListenerBuilder.() -> Unit) {
    val builder = DialogInputListenerBuilder().apply(listenerBuilder)
    val listener = object : InputDialogListener {
        override fun finished(input: String) {
            builder.onFinished?.invoke(input)
        }

        override fun canceled() {
            builder.onCancelled?.invoke()
        }
    }

    TinyFileDialogs.tinyfd_inputBox(title, message, default)?.let {
        listener.finished(it)
    } ?: listener.canceled()
}

fun colorInput(title: CharSequence = "Color Picker", listenerBuilder: DialogInputListenerBuilder.() -> Unit) {
    val builder = DialogInputListenerBuilder().apply(listenerBuilder)
    val listener = object : InputDialogListener {
        override fun finished(input: String) {
            builder.onFinished?.invoke(input)
        }

        override fun canceled() {
            builder.onCancelled?.invoke()
        }
    }

    val stack = MemoryStack.stackPush()
    val resultbuffer = stack.malloc(3)

    TinyFileDialogs.tinyfd_colorChooser(title, colorToHex(Color.WHITE), stringToUnsignedCharByteBuffer(colorToHex(Color.WHITE)),resultbuffer)?.let {
        listener.finished(it)
    } ?: listener.canceled()
}

fun messageBox(title: CharSequence, message: CharSequence, type: MessageType, icon: IconType = IconType.INFO, default: Boolean, handle: (Boolean?)->Unit) {
    val res = TinyFileDialogs.tinyfd_messageBox(title, message, type.tinyfdString, icon.tinyfdString, default)
    handle(res)
}