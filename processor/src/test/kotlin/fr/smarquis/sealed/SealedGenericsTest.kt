package fr.smarquis.sealed

import kotlin.test.Test
import kotlin.test.assertEquals


class SealedGenericsTest {

    @SealedObjectInstances
    sealed class SealedSingleGeneric<T> {
        object INSTANCE : SealedSingleGeneric<Exception>()
    }

    @Test
    fun `generic with one parameter`() = assertEquals(
        expected = setOf(SealedSingleGeneric.INSTANCE),
        actual = SealedSingleGeneric::class.sealedObjectInstances(),
    )

    @SealedObjectInstances
    sealed class SealedMultipleGenerics<A, B, C> {
        object INSTANCE : SealedMultipleGenerics<Any, Unit, String>()
    }

    @Test
    fun `generic with multiple parameters`() = assertEquals(
        expected = setOf(SealedMultipleGenerics.INSTANCE),
        actual = SealedMultipleGenerics::class.sealedObjectInstances(),
    )

    @SealedObjectInstances
    sealed class SealedCovariantGeneric<out T> {
        object INSTANCE : SealedCovariantGeneric<String>()
    }

    @Test
    fun `generic covariant`() = assertEquals(
        expected = setOf(SealedCovariantGeneric.INSTANCE),
        actual = SealedCovariantGeneric::class.sealedObjectInstances(),
    )

    @SealedObjectInstances
    sealed class SealedContravariantGeneric<in T> {
        object INSTANCE : SealedContravariantGeneric<String>()
    }

    @Test
    fun `generic contravariant`() = assertEquals(
        expected = setOf(SealedContravariantGeneric.INSTANCE),
        actual = SealedContravariantGeneric::class.sealedObjectInstances(),
    )

    @SealedObjectInstances
    sealed class SealedBoundedSingleGeneric<T : Exception> {
        object INSTANCE : SealedBoundedSingleGeneric<RuntimeException>()
    }

    @Test
    fun `generic bounded with one parameter`() = assertEquals(
        expected = setOf(SealedBoundedSingleGeneric.INSTANCE),
        actual = SealedBoundedSingleGeneric::class.sealedObjectInstances(),
    )

    @SealedObjectInstances
    sealed class SealedBoundedMultipleGeneric<A : Any, E : Exception, N : Number> {
        object INSTANCE : SealedBoundedMultipleGeneric<String, RuntimeException, Int>()
    }

    @Test
    fun `generic bounded with multiple parameters`() = assertEquals(
        expected = setOf(SealedBoundedMultipleGeneric.INSTANCE),
        actual = SealedBoundedMultipleGeneric::class.sealedObjectInstances(),
    )

    @SealedObjectInstances
    sealed class SealedBoundedCovariantGeneric<out T : Exception> {
        object INSTANCE : SealedBoundedCovariantGeneric<RuntimeException>()
    }

    @Test
    fun `generic bounded covariant`() = assertEquals(
        expected = setOf(SealedCovariantGeneric.INSTANCE),
        actual = SealedCovariantGeneric::class.sealedObjectInstances(),
    )

    @SealedObjectInstances
    sealed class SealedBoundedContravariantGeneric<in T : Exception> {
        object INSTANCE : SealedBoundedContravariantGeneric<RuntimeException>()
    }

    @Test
    fun `generic bounded contravariant`() = assertEquals(
        expected = setOf(SealedBoundedContravariantGeneric.INSTANCE),
        actual = SealedBoundedContravariantGeneric::class.sealedObjectInstances(),
    )

}

