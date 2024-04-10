package org.yunghegel.gdx.utils.reflection


import org.yunghegel.gdx.utils.ui.DefaultFieldEditor
import org.yunghegel.gdx.utils.ui.FieldEditor
import kotlin.reflect.KClass

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD, AnnotationTarget.FUNCTION, AnnotationTarget.TYPE)
annotation class Editable(
    /** name in the editor, default is field/type name  */
    val value: String = "",
    /** editor group name, default is no group  */
    val group: String = "",
    /** use predefined type hints  */
    val type: EnumType = EnumType.AUTO,
    val editor: KClass<out FieldEditor> = DefaultFieldEditor::class,
    val doc: String = "",
    /** field should be updated at each frames  */
    val realtime: Boolean = false,
    /** only display value : user can't edit value manually  */
    val readonly: Boolean = false

)
