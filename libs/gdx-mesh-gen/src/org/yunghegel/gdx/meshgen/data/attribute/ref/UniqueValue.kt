package org.yunghegel.gdx.meshgen.data.attribute.ref
/*
 * Used when we want to resolve a value which is reactive to changes in a widely used dependency.
 * It refers to an object of a type unique to the dependency, i.e. it is one of its computed (dependent) values.
 * The dynamic reference resolves this type from a pointer which describes it. It is used to define the delegation of changes
 * emitted by the dependency.
 *
 * {<@Param Type the type of the value to be resolved
 */

interface DynamicReference<Type,Value, Pointer: TypePointer<Type>> {

    fun get()

}