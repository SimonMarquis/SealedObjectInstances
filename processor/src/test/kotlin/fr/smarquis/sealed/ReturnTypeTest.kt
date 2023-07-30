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

import com.tschuchort.compiletesting.KotlinCompilation.ExitCode
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.compile
import com.tschuchort.compiletesting.resolve
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCompilerApi::class)
class ReturnTypeTest {

    @Test
    fun `generated file contains return type by default`() {
        /* Given */
        val source = SourceFile.kotlin(
            name = "MySealedClass.kt",
            contents = """
                |import fr.smarquis.sealed.SealedObjectInstances
                |
                |@SealedObjectInstances
                |sealed class MySealedClass {
                |    data object Object: MySealedClass()
                |}
            """.trimMargin(),
        )

        /* When */
        val result = compile(source)

        /* Then */
        assertEquals(ExitCode.OK, result.exitCode)
        assertEquals(
            // language=kotlin
            expected = """
                /** @return [kotlin.collections.Set] of sealed object instances of type [MySealedClass]. */
                public fun kotlin.reflect.KClass<MySealedClass>.sealedObjectInstances(): kotlin.collections.Set<MySealedClass> = setOf(MySealedClass.Object)

            """.trimIndent(),
            actual = result.resolve("MySealedClass\$sealedObjectInstances.kt").readText(),
        )
    }

    @Test
    fun `generated file does not contain return type when requested`() {
        /* Given */
        val source = SourceFile.kotlin(
            name = "MySealedClass.kt",
            contents = """
                |import fr.smarquis.sealed.SealedObjectInstances
                |
                |@SealedObjectInstances(returnType = false)
                |sealed class MySealedClass {
                |    data object Object: MySealedClass()
                |}
            """.trimMargin(),
        )

        /* When */
        val result = compile(source)

        /* Then */
        assertEquals(ExitCode.OK, result.exitCode)
        assertEquals(
            // language=kotlin
            expected = """
                /** @return [kotlin.collections.Set] of sealed object instances of type [MySealedClass]. */
                public fun kotlin.reflect.KClass<MySealedClass>.sealedObjectInstances() = setOf(MySealedClass.Object)

            """.trimIndent(),
            actual = result.resolve("MySealedClass\$sealedObjectInstances.kt").readText(),
        )
    }
}
