package fr.smarquis.sealed

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class NestedReferenceReturnsInvalidCollectionTest {

    @SealedObjectInstances
    sealed class SealedClass {
        object Object : SealedClass()
        companion object {
            val values = SealedClass::class.sealedObjectInstances()
        }
    }

    @Test
    fun `this is a bug`() {
        assertEquals(
            expected = SealedClass::class.sealedObjectInstances(), // [SealedClass$Object]
            actual = SealedClass.values, // [null]
        )
    }

}
