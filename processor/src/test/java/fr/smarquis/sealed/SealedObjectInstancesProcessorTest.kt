package fr.smarquis.sealed

import kotlin.test.Test
import kotlin.test.assertEquals

class SealedObjectInstancesProcessorTest {

    @Test
    fun sealedObjectInstances() {
        assertEquals(
            expected = setOf(
                OtherFileObject,
                OutsideObject,
                MySealedClass.Data.NestedObject,
                MySealedClass.Object,
            ),
            actual = MySealedClass::class.sealedObjectInstances(),
        )
    }

}
