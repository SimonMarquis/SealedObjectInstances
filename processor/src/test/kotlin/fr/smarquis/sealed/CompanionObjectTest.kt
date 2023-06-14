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
import kotlin.test.assertSame

class CompanionObjectTest {

    @SealedObjectInstances
    sealed class AnnotatedSealedClassWithCompanionObject {
        companion object
        object INSTANCE : AnnotatedSealedClassWithCompanionObject()
    }

    @Test
    fun testAnnotatedSealedClassWithCompanionObject() = assertSame(
        expected = AnnotatedSealedClassWithCompanionObject::class.sealedObjectInstances().single(),
        actual = AnnotatedSealedClassWithCompanionObject.sealedObjectInstances().single(),
    )

    sealed class SealedClassWithAnnotatedCompanionObject {
        @SealedObjectInstances companion object
        object INSTANCE : SealedClassWithAnnotatedCompanionObject()
    }

    @Test
    fun testSealedClassWithAnnotatedCompanionObject() = assertSame(
        expected = SealedClassWithAnnotatedCompanionObject::class.sealedObjectInstances().single(),
        actual = SealedClassWithAnnotatedCompanionObject.sealedObjectInstances().single(),
    )

    @SealedObjectInstances(name = "onSealedClass")
    sealed class SealedClassWithCompanionObjectBothAnnotated {
        @SealedObjectInstances(name = "onCompanionObject")
        companion object
        object INSTANCE : SealedClassWithCompanionObjectBothAnnotated()
    }

    @Test
    fun testSealedClassWithCompanionObjectBothAnnotated() = assertSame(
        expected = SealedClassWithAnnotatedCompanionObject::class.sealedObjectInstances().single(),
        actual = SealedClassWithAnnotatedCompanionObject.sealedObjectInstances().single(),
    )

    sealed class SealedClassWithNameCompanionObject {
        @SealedObjectInstances companion object Foo
        object INSTANCE : SealedClassWithNameCompanionObject()
    }

    @Test
    fun testSealedClassWithNameCompanionObject() = assertSame(
        expected = SealedClassWithNameCompanionObject::class.sealedObjectInstances().single(),
        actual = SealedClassWithNameCompanionObject.sealedObjectInstances().single(),
    )

    @SealedObjectInstances
    sealed class AnnotatedSealedClassWithGeneric<T> {
        companion object
        object INSTANCE : AnnotatedSealedClassWithGeneric<Unit>()
    }

    @Test
    fun testAnnotatedSealedClassWithGeneric() = assertSame(
        expected = AnnotatedSealedClassWithGeneric::class.sealedObjectInstances().single(),
        actual = AnnotatedSealedClassWithGeneric.sealedObjectInstances().single(),
    )
}
