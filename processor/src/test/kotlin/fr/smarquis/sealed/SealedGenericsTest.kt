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

import kotlin.test.Test
import kotlin.test.assertEquals

@Suppress("USELESS_CAST")
class SealedGenericsTest {

    @SealedObjectInstances
    sealed class SealedSingleGeneric<T> {
        data object INSTANCE : SealedSingleGeneric<Exception>()
    }

    @Test
    fun `generic with one parameter`() = assertEquals(
        expected = setOf(SealedSingleGeneric.INSTANCE),
        actual = SealedSingleGeneric::class.sealedObjectInstances() as Set<SealedSingleGeneric<out Any>>,
    )

    @SealedObjectInstances
    sealed class SealedMultipleGenerics<A, B, C> {
        data object INSTANCE : SealedMultipleGenerics<Any, Unit, String>()
    }

    @Test
    fun `generic with multiple parameters`() = assertEquals(
        expected = setOf(SealedMultipleGenerics.INSTANCE),
        actual = SealedMultipleGenerics::class.sealedObjectInstances() as Set<SealedMultipleGenerics<out Any, out Any, out Any>>,
    )

    @SealedObjectInstances
    sealed class SealedCovariantGeneric<out T> {
        data object INSTANCE : SealedCovariantGeneric<String>()
    }

    @Test
    fun `generic covariant`() = assertEquals(
        expected = setOf(SealedCovariantGeneric.INSTANCE),
        actual = SealedCovariantGeneric::class.sealedObjectInstances(),
    )

    @SealedObjectInstances
    sealed class SealedCovariantBoundedGeneric<out T : Number> {
        data object INSTANCE : SealedCovariantBoundedGeneric<Long>()
    }

    @Test
    fun `generic covariant with bounded`() = assertEquals(
        expected = setOf(SealedCovariantBoundedGeneric.INSTANCE),
        actual = SealedCovariantBoundedGeneric::class.sealedObjectInstances(),
    )

    @SealedObjectInstances
    sealed class SealedContravariantGeneric<in T> {
        data object INSTANCE : SealedContravariantGeneric<String>()
    }

    @Test
    fun `generic contravariant`() = assertEquals(
        expected = setOf(SealedContravariantGeneric.INSTANCE),
        actual = SealedContravariantGeneric::class.sealedObjectInstances(),
    )

    @SealedObjectInstances
    sealed class SealedContravariantBoundedGeneric<in T : Number> {
        data object INSTANCE : SealedContravariantBoundedGeneric<Long>()
    }

    @Test
    fun `generic contravariant bounded`() = assertEquals(
        expected = setOf(SealedContravariantBoundedGeneric.INSTANCE),
        actual = SealedContravariantBoundedGeneric::class.sealedObjectInstances(),
    )

    @SealedObjectInstances
    sealed class SealedBoundedSingleGeneric<T : Exception> {
        data object INSTANCE : SealedBoundedSingleGeneric<RuntimeException>()
    }

    @Test
    fun `generic bounded with one parameter`() = assertEquals(
        expected = setOf(SealedBoundedSingleGeneric.INSTANCE),
        actual = SealedBoundedSingleGeneric::class.sealedObjectInstances() as Set<SealedBoundedSingleGeneric<out Exception>>,
    )

    @SealedObjectInstances
    sealed class SealedBoundedMultipleGeneric<A : Any, E : Exception, N : Number> {
        data object INSTANCE : SealedBoundedMultipleGeneric<String, RuntimeException, Int>()
    }

    @Test
    fun `generic bounded with multiple parameters`() = assertEquals(
        expected = setOf(SealedBoundedMultipleGeneric.INSTANCE),
        actual = SealedBoundedMultipleGeneric::class.sealedObjectInstances() as Set<SealedBoundedMultipleGeneric<out Any, out Exception, out Number>>,
    )

    @SealedObjectInstances
    sealed class SealedBoundedCovariantGeneric<out T : Exception> {
        data object INSTANCE : SealedBoundedCovariantGeneric<RuntimeException>()
    }

    @Test
    fun `generic bounded covariant`() = assertEquals(
        expected = setOf(SealedBoundedCovariantGeneric.INSTANCE),
        actual = SealedBoundedCovariantGeneric::class.sealedObjectInstances() as Set<SealedBoundedCovariantGeneric<Exception>>,
    )

    @SealedObjectInstances
    sealed class SealedBoundedContravariantGeneric<in T : Exception> {
        data object INSTANCE : SealedBoundedContravariantGeneric<RuntimeException>()
    }

    @Test
    fun `generic bounded contravariant`() = assertEquals(
        expected = setOf(SealedBoundedContravariantGeneric.INSTANCE),
        actual = SealedBoundedContravariantGeneric::class.sealedObjectInstances() as Set<SealedBoundedContravariantGeneric<*>>,
    )
}
