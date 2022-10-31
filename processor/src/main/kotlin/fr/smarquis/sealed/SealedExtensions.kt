package fr.smarquis.sealed

import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import kotlin.reflect.KClass

//region KSP
internal fun KSClassDeclaration.sealedObjectInstances() = recursiveSealedObjectInstances()

private tailrec fun KSClassDeclaration.recursiveSealedObjectInstances(
    sealedSubclasses: List<KSClassDeclaration> = listOf(this),
    acc: Set<KSClassDeclaration> = emptySet(),
): Set<KSClassDeclaration> = when {
    sealedSubclasses.isEmpty() -> acc
    else -> recursiveSealedObjectInstances(
        acc = acc + sealedSubclasses.filter { it.classKind == ClassKind.OBJECT },
        sealedSubclasses = sealedSubclasses.flatMap { it.getSealedSubclasses() },
    )
}
//endregion

//region Reflect
/**
 * @return the [Set] of [T] instances through reflection.
 */
fun <T : Any> KClass<T>.reflectSealedObjectInstances(): Set<T> = recursiveSealedObjectInstances()

private tailrec fun <T : Any> KClass<T>.recursiveSealedObjectInstances(
    sealedSubclasses: List<KClass<out T>> = listOf(this),
    acc: Set<T> = emptySet(),
): Set<T> = when {
    sealedSubclasses.isEmpty() -> acc
    else -> recursiveSealedObjectInstances(
        acc = acc + sealedSubclasses.mapNotNull { it.objectInstance },
        sealedSubclasses = sealedSubclasses.flatMap { it.sealedSubclasses },
    )
}
//endregion
