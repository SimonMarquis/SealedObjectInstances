package fr.smarquis.sealed

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.kspWithCompilation
import com.tschuchort.compiletesting.symbolProcessorProviders
import fr.smarquis.sealed.SealedObjectInstances.Visibility.Internal
import fr.smarquis.sealed.SealedObjectInstances.Visibility.Public
import kotlin.reflect.KVisibility.INTERNAL
import kotlin.reflect.KVisibility.PUBLIC
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue


class VisibilityModifiersTest {

    @SealedObjectInstances
    sealed class SealedUnspecifiedVisibility {
        object INSTANCE : SealedUnspecifiedVisibility()
    }

    @Test
    fun `unspecified visibility becomes public`() =
        assertEquals(PUBLIC, SealedUnspecifiedVisibility::class::sealedObjectInstances.visibility)

    @Suppress("RedundantVisibilityModifier")
    @SealedObjectInstances
    public sealed class SealedImplicitPublicVisibility {
        object INSTANCE : SealedImplicitPublicVisibility()
    }

    @Test
    fun `implicit public visibility becomes public`() =
        assertEquals(PUBLIC, SealedImplicitPublicVisibility::class::sealedObjectInstances.visibility)

    @SealedObjectInstances(visibility = Public)
    sealed class SealedExplicitPublicVisibility {
        object INSTANCE : SealedExplicitPublicVisibility()
    }

    @Test
    fun `explicit public visibility becomes public`() =
        assertEquals(PUBLIC, SealedExplicitPublicVisibility::class::sealedObjectInstances.visibility)

    @SealedObjectInstances
    internal sealed class SealedImplicitInternalVisibility {
        object INSTANCE : SealedImplicitInternalVisibility()
    }

    @Test
    fun `implicit internal visibility becomes internal`() =
        assertEquals(INTERNAL, SealedImplicitInternalVisibility::class::sealedObjectInstances.visibility)

    @SealedObjectInstances(visibility = Internal)
    sealed class SealedExplicitInternalVisibility {
        object INSTANCE : SealedExplicitInternalVisibility()
    }

    @Test
    fun `explicit internal visibility becomes internal`() =
        assertEquals(INTERNAL, SealedExplicitInternalVisibility::class::sealedObjectInstances.visibility)

    @Test
    fun `compilation fails when visibility of sealed class is private`() {
        /* Given */
        val source = SourceFile.kotlin(
            name = "MySealedClass.kt",
            contents = """
                |import fr.smarquis.sealed.SealedObjectInstances
                |
                |@SealedObjectInstances
                |private sealed class MySealedClass {
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
        assertEquals(KotlinCompilation.ExitCode.COMPILATION_ERROR, result.exitCode)
        assertTrue("Error message is printed") {
            "MySealedClass.kt:4: Unsupported [private] visibility." in result.messages
        }
    }

    @Test
    fun `compilation fails when requested visibility is larger than actual visibility`() {
        /* Given */
        val source = SourceFile.kotlin(
            name = "MySealedClass.kt",
            contents = """
                |import fr.smarquis.sealed.SealedObjectInstances
                |import fr.smarquis.sealed.SealedObjectInstances.Visibility.Public
                |import fr.smarquis.sealed.SealedObjectInstances.RawType.*
                |
                |@SealedObjectInstances(visibility = Public)
                |internal sealed class MySealedClass {
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
        assertEquals(KotlinCompilation.ExitCode.COMPILATION_ERROR, result.exitCode)
        assertTrue("Error message is printed for the receiver type") {
            "MySealedClass\$sealedObjectInstances.kt: (2, 12): 'public' member exposes its 'internal' receiver type argument MySealedClass" in result.messages
        }
        assertTrue("Error message is printed for the return type") {
            "MySealedClass\$sealedObjectInstances.kt: (2, 49): 'public' function exposes its 'internal' return type argument MySealedClass" in result.messages
        }
    }

}
