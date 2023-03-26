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
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SealedClassesNameCollisionTest {

    @Test
    fun `compilation fails when sealed classes have the same name`() {
        /* Given */
        val source = SourceFile.kotlin(
            name = "MySealedClass.kt",
            contents = """
                |import fr.smarquis.sealed.SealedObjectInstances
                |
                |@SealedObjectInstances
                |sealed class MySealedClass
                |
                |class Wrapper {
                |    @SealedObjectInstances
                |    sealed class MySealedClass
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
        assertEquals(ExitCode.INTERNAL_ERROR, result.exitCode)
        assertTrue("Internal error message is printed") {
            "e: Error occurred in KSP, check log for detail" in result.messages
        }
        assertTrue("FileAlreadyExistsException is printed") {
            "e: [ksp] kotlin.io.FileAlreadyExistsException:" in result.messages
        }
        assertTrue("Custom error message is printed") {
            "Duplicated file detected! You can override the generated file name with @SealedObjectInstances(fileName=\"…\")" in result.messages
        }
    }
}
