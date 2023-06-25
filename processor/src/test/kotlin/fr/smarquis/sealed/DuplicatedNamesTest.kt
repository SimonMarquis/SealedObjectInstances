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

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.KotlinCompilation.ExitCode
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.kspWithCompilation
import com.tschuchort.compiletesting.symbolProcessorProviders
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.junit.jupiter.api.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

@OptIn(ExperimentalCompilerApi::class)
class DuplicatedNamesTest {

    @Test
    fun `compilation fails when duplicated names are requested`() {
        /* Given */
        val source = SourceFile.kotlin(
            name = "MySealedClass.kt",
            contents = """
                |import fr.smarquis.sealed.SealedObjectInstances
                |import fr.smarquis.sealed.SealedObjectInstances.RawType.*
                |
                |@SealedObjectInstances
                |@SealedObjectInstances(name = "values", rawType = Array)
                |@SealedObjectInstances(name = "values", rawType = List)
                |sealed class MySealedClass {
                |    data object Object: MySealedClass()
                |}
            """.trimMargin(),
        )

        /* When */
        val result = KotlinCompilation().apply {
            sources = listOf(source)
            symbolProcessorProviders = listOf(SealedObjectInstancesProcessorProvider())
            kspWithCompilation = true
            inheritClassPath = true
        }.compile()

        /* Then */
        assertEquals(ExitCode.COMPILATION_ERROR, result.exitCode)
        assertContains(
            message = "Error message is printed",
            charSequence = result.messages,
            other = "MySealedClass.kt:7: Duplicated names: {sealedObjectInstances=1, values=2}",
        )
    }
}
