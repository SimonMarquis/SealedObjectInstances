/*
 * Copyright (C) 2023 Simon Marquis
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
