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
        data object INSTANCE : SealedClassWithCustomNames()
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
