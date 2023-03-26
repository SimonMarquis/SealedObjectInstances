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
import fr.smarquis.sealed.SealedObjectInstances.RawType.Array
import fr.smarquis.sealed.SealedObjectInstances.RawType.List
import org.junit.jupiter.api.Test
import java.io.File.separator
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CustomFileNameTest {

    @SealedObjectInstances
    @SealedObjectInstances(name = "array", rawType = Array, fileName = "custom_file_name")
    @SealedObjectInstances(name = "list", rawType = List, fileName = "custom_file_name")
    sealed class CustomFileNameSealedClass

    @Test
    fun `all extensions are created`() {
        CustomFileNameSealedClass::class.sealedObjectInstances()
        CustomFileNameSealedClass::class.array()
        CustomFileNameSealedClass::class.list()
    }

    @Test
    fun `custom source fileName is created`() {
        /* Given */
        val source = SourceFile.kotlin(
            name = "CustomFileNameSealedClass.kt",
            contents = """
                |import fr.smarquis.sealed.SealedObjectInstances
                |import fr.smarquis.sealed.SealedObjectInstances.RawType.*
                |
                |
                |@SealedObjectInstances
                |@SealedObjectInstances(name = "array", rawType = Array, fileName = "custom_file_name")
                |@SealedObjectInstances(name = "list", rawType = List, fileName = "custom_file_name")
                |sealed class CustomFileNameSealedClass
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
        assertEquals(ExitCode.OK, result.exitCode)
        /* $sealedObjectInstances.kt */
        assertTrue("${'$'}sealedObjectInstances.kt source file is generated") {
            """ksp${separator}sources${separator}kotlin${separator}CustomFileNameSealedClass${'$'}sealedObjectInstances.kt""" in result.messages
        }
        result.generatedFiles.single { it.name == "CustomFileNameSealedClass_sealedObjectInstancesKt.class" }
        /* custom_file_name.kt */
        assertTrue("custom_file_name.kt source file is generated") {
            """ksp${separator}sources${separator}kotlin${separator}custom_file_name.kt""" in result.messages
        }
        result.generatedFiles.single { it.name == "Custom_file_nameKt.class" }
    }
}
