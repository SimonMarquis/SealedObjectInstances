package fr.smarquis.sealed

@SealedObjectInstances
sealed class MySealedClass {
    object Object : MySealedClass()
    data class Data(val any: Any) : MySealedClass() {
        object NestedObject : MySealedClass()
    }
}

object OutsideObject : MySealedClass()
