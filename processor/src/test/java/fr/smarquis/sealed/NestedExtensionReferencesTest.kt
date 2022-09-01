package fr.smarquis.sealed

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class NestedExtensionReferencesTest {

    @SealedObjectInstances
    sealed class Sealed {
        object Object : Sealed()
        companion object {
            val values get() = Sealed::class.sealedObjectInstances()
            val lazyValues by lazy(Sealed::class::sealedObjectInstances)
        }
    }

    @Test
    fun `referencing extension from within the class compiles and returns the same values`() {
        assertEquals(Sealed::class.sealedObjectInstances(), Sealed.values)
        assertEquals(Sealed::class.sealedObjectInstances(), Sealed.lazyValues)
    }

}
