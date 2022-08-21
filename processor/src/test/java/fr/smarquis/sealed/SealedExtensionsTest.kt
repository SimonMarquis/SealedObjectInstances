package fr.smarquis.sealed


import kotlin.test.Test
import kotlin.test.assertEquals

class SealedExtensionsTest {

    @Test
    fun `reflect extension produces the same output`() = assertEquals(
        expected = MySealedClass::class.sealedObjectInstances(),
        actual = MySealedClass::class.reflectSealedObjectInstances(),
    )

}