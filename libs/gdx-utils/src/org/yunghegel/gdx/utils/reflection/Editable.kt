package org.yunghegel.gdx.utils.reflection


@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD, AnnotationTarget.FUNCTION, AnnotationTarget.TYPE)
annotation class Editable(
    /** name in the editor, default is field/type name  */
    val name: String = "",

    val group: String = "default",
    /** use predefined type hints  */
    val type: EnumType = EnumType.AUTO,

    val tooltip: String = "",
    /** field should be updated at each frames  */
    val realtime: Boolean = false,
    /** only display value : user can't edit value manually  */
    val readonly: Boolean = false

)
