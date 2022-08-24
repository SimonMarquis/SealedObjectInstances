package fr.smarquis.sealed

fun main() {
    FeatureFlag::class.sealedObjectInstances().let(::println)
    Debug::class.sealedObjectInstances().let(::println)
    UI::class.sealedObjectInstances().let(::println)
}
