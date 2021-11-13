package me.liuli.luminous.wrapper

@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
annotation class Wrapper(val type: EnumWrapType, val name: String)

enum class EnumWrapType {
    CLASS,
    FIELD,
    METHOD
}
