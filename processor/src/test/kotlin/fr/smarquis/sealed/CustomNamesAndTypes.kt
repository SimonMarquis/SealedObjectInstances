package fr.smarquis.sealed

import fr.smarquis.sealed.CustomNamesAndTypes.SealedClassWithCustomNames.INSTANCE
import fr.smarquis.sealed.SealedObjectInstances.RawType.Array
import fr.smarquis.sealed.SealedObjectInstances.RawType.List
import fr.smarquis.sealed.SealedObjectInstances.RawType.Set
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertSame


class CustomNamesAndTypes {

    @SealedObjectInstances // This will produce the default name/type extension
    @SealedObjectInstances(name = "array", rawType = Array)
    @SealedObjectInstances(name = "list", rawType = List)
    @SealedObjectInstances(name = "set", rawType = Set)
    sealed class SealedClassWithCustomNames {
        object INSTANCE : SealedClassWithCustomNames()
    }

    @Test
    fun testAllTypes() {
        /*Default*/
        assertSame(INSTANCE, SealedClassWithCustomNames::class.sealedObjectInstances().single())
        /*Custom types & names */
        assertContentEquals(arrayOf(INSTANCE), SealedClassWithCustomNames::class.array())
        assertContentEquals(listOf(INSTANCE), SealedClassWithCustomNames::class.list())
        assertEquals(setOf(INSTANCE), SealedClassWithCustomNames::class.set())
    }

}
