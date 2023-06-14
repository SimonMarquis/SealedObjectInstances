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

import fr.smarquis.sealed.SealedObjectInstances.RawType.Set
import fr.smarquis.sealed.SealedObjectInstances.Visibility.Unspecified
import kotlin.reflect.KClass
import kotlin.reflect.KFunction1

/**
 * This repeatable annotation is the main entry point to configure the generated extensions.
 */
@Repeatable
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class SealedObjectInstances(
    /**
     * Method name to access sealed object instances.
     */
    val name: String = "sealedObjectInstances",
    /**
     * The collection type the generated method.
     */
    val rawType: RawType = Set,
    /**
     * The visibility of the generated method.
     */
    val visibility: Visibility = Unspecified,
    /**
     * The file name for the generated method. The default filename (when blank) will be the sealed class name with `$sealedObjectInstances` postfix.
     */
    val fileName: String = "",
    /**
     * Add return type to the generated method.
     */
    val returnType: Boolean = true,
) {

    enum class RawType(
        internal val kClass: KClass<*>,
        internal val builder: KFunction1<kotlin.Array<out Any>, Any>,
    ) {
        Array(kotlin.Array::class, ::arrayOf),
        List(kotlin.collections.List::class, ::listOf),
        Set(kotlin.collections.Set::class, ::setOf),
    }

    enum class Visibility : Comparable<Visibility> { Unspecified, Public, Internal, Private }
}

/**
 * This intermediate data class is necessary to easily compare two [SealedObjectInstances],
 * because [hashCode] and [equals] can not be part of the annotation class.
 */
internal data class SealedObjectInstancesDataClass(
    val name: String,
    val rawType: SealedObjectInstances.RawType,
    val visibility: SealedObjectInstances.Visibility,
    val fileName: String,
    val returnType: Boolean,
) {
    companion object {
        internal fun SealedObjectInstances.toDataClass() = SealedObjectInstancesDataClass(
            name = name,
            rawType = rawType,
            visibility = visibility,
            fileName = fileName,
            returnType = returnType,
        )
    }
}
