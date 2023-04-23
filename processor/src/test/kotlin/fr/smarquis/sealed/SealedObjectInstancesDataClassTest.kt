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

import org.junit.jupiter.api.Test
import kotlin.reflect.full.declaredMemberProperties
import kotlin.test.assertContentEquals

class SealedObjectInstancesDataClassTest {

    @Test
    fun `fields of annotation class and data class are mirrored`() = assertContentEquals(
        expected = SealedObjectInstances::class.declaredMemberProperties.map { it.name to it.returnType },
        actual = SealedObjectInstancesDataClass::class.declaredMemberProperties.map { it.name to it.returnType },
    )
}
