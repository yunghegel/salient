package org.yunghegel.gdx.renderer.shader

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.glutils.ShaderPart
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.graphics.glutils.ShaderStage
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.GdxRuntimeException
import com.badlogic.gdx.utils.ObjectMap
import java.io.IOException

class ShaderLoader(var root: FileHandle) {

    constructor(internalPath:String) : this(Gdx.files.internal(internalPath))

    var snippets: ObjectMap<String, ObjectMap<String, String?>?> = ObjectMap()
    private val includes = Array<String>()

    fun load(prefix:String, vertex: String, fragment: String): ShaderProgram {
        var vertex = vertex
        var fragment = fragment
        val out = StringBuilder()
        load(out, vertex)
        vertex = out.toString()
        includes.clear()
        out.setLength(0)
        load(out, fragment)
        fragment = out.toString()
        includes.clear()
        println(prefix + vertex)
        println(prefix + fragment)
        return ShaderProgram(prefix  + vertex, prefix + fragment)
    }

    fun load(prefix:String, stage: ShaderStage, source:String) : ShaderPart {
        val out = StringBuilder()
        load(out, source)
        return ShaderPart(stage,prefix + out.toString())
    }

    fun load(name: String): String {
        val out = StringBuilder()
        load(out, name)
        includes.clear()
        return out.toString()
    }

    fun load(out: StringBuilder, name: String) {
        val idx = name.lastIndexOf(':')
        val fileName = if (idx < 0) name else name.substring(0, idx)
        val snipName = if (idx < 0 || (idx >= name.length - 1)) "" else name.substring(idx + 1)
        var snips = snippets[fileName, null]
        if (snips == null) {
            snips = parse(root.child(fileName))
            snippets.put(fileName, snips)
        }
        val result = snips[snipName, null]
            ?: throw GdxRuntimeException("No snippet [" + snipName + "] in file " + root.child(fileName).path())
        parse(out, fileName, result)
    }

    fun parse(out: StringBuilder, currentFile: String, code: String) {
        val lines = code.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        var idx: Int
        var jdx: Int = 0
        for (line in lines) {
            if (((line.indexOf("#include").also { idx = it }) == 0) && ((line.indexOf("\"", idx).also { idx = it }) > 0)
                && ((line.indexOf("\"", ++idx).also { jdx = it }) > idx)
            ) {
                var name = line.substring(idx, jdx)
                if (name.length > 0) {
                    if (name[0] == ':') name = currentFile + name
                    if (!includes.contains(name, false)) {
                        includes.add(name)
                        load(out, name)
                    }
                }
            } else out.append(line.trim { it <= ' ' }).append("\r\n")
        }
    }

    protected fun parse(file: FileHandle): ObjectMap<String, String?> {
        val result = ObjectMap<String, String?>()
        val reader = file.reader(1024)
        var line: String
        var snipName = ""
        stringBuilder.setLength(0)
        var idx: Int =0
        try {
            while ((reader.readLine().also { line = it?: "" }) != null) {
                if ((line.length > 3) && line[0] == '[' && (line.indexOf(']').also { idx = it }) > 1) {
                    if (snipName.length > 0 || stringBuilder.length > 0) result.put(snipName, stringBuilder.toString())
                    stringBuilder.setLength(0)
                    snipName = line.substring(1, idx)
                } else stringBuilder.append(line.trim { it <= ' ' }).append("\r\n")
            }
        } catch (e: IOException) {
            throw GdxRuntimeException(e)
        }
        if (snipName.length > 0 || stringBuilder.length > 0) result.put(snipName, stringBuilder.toString())
        return result
    }

    override fun toString(): String {
        stringBuilder.setLength(0)
        for (entry in snippets.entries()) {
            stringBuilder.append(entry.key).append(": {")
            for (snipname in entry.value!!.keys()) stringBuilder.append(snipname).append(", ")
            stringBuilder.append("}\n")
        }
        return stringBuilder.toString()
    }

    companion object {
        val stringBuilder: StringBuilder = StringBuilder()

        fun load(root: FileHandle, name: String): String {
            return ShaderLoader(root).load(name)
        }
    }
}
