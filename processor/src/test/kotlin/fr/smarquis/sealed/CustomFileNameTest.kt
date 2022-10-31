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