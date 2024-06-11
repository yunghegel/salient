package org.yunghegel.salient.engine.api.ecs

import com.badlogic.gdx.Game
import org.yunghegel.salient.engine.scene3d.GameObject
import org.yunghegel.salient.engine.system.result
import kotlin.reflect.KClass

interface ComponentCloneable<T:BaseComponent> {

    val type : KClass<out BaseComponent>

    fun clone(target: GameObject) : T

    fun apply(comp: T, target: GameObject)

    fun validate(target : GameObject) : Result<T> {
       return result {
           value("Success") {
              val comp = clone(target)
                apply(comp,target)
               if (comp !in target.components) {
                     target.add(comp)
               }

                comp
           }
           error { ComponentCloneException(type) }
       }
    }

    class ComponentCloneException(val component: KClass<out BaseComponent>) : Throwable(generateMessage(component))
        companion object {
            fun generateMessage (component: KClass<out BaseComponent>) : String {
                return "Component ${component.simpleName} could not be cloned"
            }
        }
}

