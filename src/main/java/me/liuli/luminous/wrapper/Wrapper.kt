package me.liuli.luminous.wrapper

@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER)
annotation class Wrapper(val type: EnumWrapType, val name: String)

enum class EnumWrapType {
    CLASS,
    FIELD,
    METHOD
}
