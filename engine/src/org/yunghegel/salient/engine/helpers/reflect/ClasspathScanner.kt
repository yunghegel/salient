package org.yunghegel.salient.engine.helpers.reflect

import com.google.common.reflect.ClassPath
import java.lang.reflect.Field
import java.lang.reflect.Method

class ClasspathScanner(private val root: String) {

    val classes : Set<ClassPath.ClassInfo>

    init {
        classes = scan(root)
    }


    fun process(action: (ClassPath.ClassInfo) -> Unit) {
        classes.forEach(action)
    }


    companion object {

        fun scan(path: String): Set<ClassPath.ClassInfo> {
            val classLoader = ClassLoader.getSystemClassLoader()
            return ClassPath.from(classLoader).getTopLevelClassesRecursive(path)
        }
    }

}

fun ClassPath.ClassInfo.iterateProperties(action: (Array<Field>, Array<Method>, Array<out Class<*>>) -> Unit) {
    val clazz = load()
    val fields = clazz.declaredFields
    val methods = clazz.declaredMethods
    val interfaces = clazz.interfaces
    action(fields, methods, interfaces)
}
