package fr.smarquis.sealed

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.KotlinCompilation.ExitCode
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.kspWithCompilation
import com.tschuchort.compiletesting.symbolProcessorProviders
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

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
                |    object Object: MySealedClass()
                |}
            """.trimMargin()
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
                |    object Object: MySealedClass()
                |}
            """.trimMargin()
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

    private fun compile(source: SourceFile) = KotlinCompilation().apply {
        sources = listOf(source)
        symbolProcessorProviders = listOf(SealedObjectInstancesProcessorProvider())
        kspWithCompilation = true
        inheritClassPath = true
    }.compile()

    private fun KotlinCompilation.Result.resolve(relative: String) =
        outputDirectory.parentFile.resolve("ksp/sources/kotlin/$relative")

}