package fr.smarquis.sealed

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.KotlinCompilation.ExitCode
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.kspWithCompilation
import com.tschuchort.compiletesting.symbolProcessorProviders
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

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
                |    object Object: MySealedClass()
                |}
            """.trimMargin()
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
        assertTrue("Error message is printed") {
            "e: [ksp] Duplicated names: {sealedObjectInstances=1, values=2}" in result.messages
        }
    }

}