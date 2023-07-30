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
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.compile
import fr.smarquis.sealed.SealedObjectInstances.Visibility.Internal
import fr.smarquis.sealed.SealedObjectInstances.Visibility.Public
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import kotlin.reflect.KVisibility.INTERNAL
import kotlin.reflect.KVisibility.PUBLIC
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

@OptIn(ExperimentalCompilerApi::class)
class VisibilityModifiersTest {

    @SealedObjectInstances
    sealed class SealedUnspecifiedVisibility {
        data object INSTANCE : SealedUnspecifiedVisibility()
    }

    @Test
    fun `unspecified visibility becomes public`() =
        assertEquals(PUBLIC, SealedUnspecifiedVisibility::class::sealedObjectInstances.visibility)

    @Suppress("RedundantVisibilityModifier")
    @SealedObjectInstances
    public sealed class SealedImplicitPublicVisibility {
        data object INSTANCE : SealedImplicitPublicVisibility()
    }

    @Test
    fun `implicit public visibility becomes public`() =
        assertEquals(PUBLIC, SealedImplicitPublicVisibility::class::sealedObjectInstances.visibility)

    @SealedObjectInstances(visibility = Public)
    sealed class SealedExplicitPublicVisibility {
        data object INSTANCE : SealedExplicitPublicVisibility()
    }

    @Test
    fun `explicit public visibility becomes public`() =
        assertEquals(PUBLIC, SealedExplicitPublicVisibility::class::sealedObjectInstances.visibility)

    @SealedObjectInstances
    internal sealed class SealedImplicitInternalVisibility {
        data object INSTANCE : SealedImplicitInternalVisibility()
    }

    @Test
    fun `implicit internal visibility becomes internal`() =
        assertEquals(INTERNAL, SealedImplicitInternalVisibility::class::sealedObjectInstances.visibility)

    @SealedObjectInstances(visibility = Internal)
    sealed class SealedExplicitInternalVisibility {
        data object INSTANCE : SealedExplicitInternalVisibility()
    }

    @Test
    fun `explicit internal visibility becomes internal`() =
        assertEquals(INTERNAL, SealedExplicitInternalVisibility::class::sealedObjectInstances.visibility)

    sealed class CompanionImplicitInternalVisibility {
        @SealedObjectInstances internal companion object
    }

    @Test
    fun `companion implicit internal visibility becomes internal`() =
        assertEquals(INTERNAL, CompanionImplicitInternalVisibility::sealedObjectInstances.visibility)

    sealed class CompanionExplicitInternalVisibility {
        @SealedObjectInstances(visibility = Internal)
        companion object
    }

    @Test
    fun `companion explicit internal visibility becomes internal`() =
        assertEquals(INTERNAL, CompanionExplicitInternalVisibility::sealedObjectInstances.visibility)

    internal sealed class CompanionInferredVisibilityInternal {
        @SealedObjectInstances companion object
    }

    @Test
    fun `companion inferred visibility becomes internal`() =
        assertEquals(INTERNAL, CompanionInferredVisibilityInternal::sealedObjectInstances.visibility)

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
                |    data object Object: MySealedClass()
                |}
            """.trimMargin(),
        )

        /* When */
        val result = compile(source)

        /* Then */
        assertEquals(KotlinCompilation.ExitCode.COMPILATION_ERROR, result.exitCode)
        assertContains(
            message = "Error message is printed",
            charSequence = result.messages,
            other = "MySealedClass.kt:4: Unsupported [private] visibility.",
        )
    }

    @Test
    fun `compilation fails when visibility of companion object is private`() {
        /* Given */
        val source = SourceFile.kotlin(
            name = "MySealedClass.kt",
            contents = """
                |import fr.smarquis.sealed.SealedObjectInstances
                |
                |sealed class MySealedClass {
                |    @SealedObjectInstances private companion object
                |}
            """.trimMargin(),
        )

        /* When */
        val result = compile(source)

        /* Then */
        assertEquals(KotlinCompilation.ExitCode.COMPILATION_ERROR, result.exitCode)
        assertContains(
            message = "Error message is printed",
            charSequence = result.messages,
            other = "MySealedClass.kt:4: Unsupported [private] visibility.",
        )
    }

    @Test
    fun `compilation fails when requested visibility of sealed class is larger than actual visibility`() {
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
                |    data object Object: MySealedClass()
                |}
            """.trimMargin(),
        )

        /* When */
        val result = compile(source)

        /* Then */
        assertEquals(KotlinCompilation.ExitCode.COMPILATION_ERROR, result.exitCode)
        assertContains(
            message = "Error message is printed for the receiver type",
            charSequence = result.messages,
            other = "MySealedClass\$sealedObjectInstances.kt:2:12 'public' member exposes its 'internal' receiver type argument MySealedClass",
        )
        assertContains(
            message = "Error message is printed for the return type",
            charSequence = result.messages,
            other = "MySealedClass\$sealedObjectInstances.kt:2:49 'public' function exposes its 'internal' return type argument MySealedClass",
        )
    }

    @Test
    fun `compilation fails when requested visibility of companion object is larger than actual visibility`() {
        /* Given */
        val source = SourceFile.kotlin(
            name = "MySealedClass.kt",
            contents = """
                |import fr.smarquis.sealed.SealedObjectInstances
                |import fr.smarquis.sealed.SealedObjectInstances.Visibility.Public
                |import fr.smarquis.sealed.SealedObjectInstances.RawType.*
                |
                |sealed class MySealedClass {
                |    @SealedObjectInstances(visibility = Public) internal companion object
                |}
            """.trimMargin(),
        )

        /* When */
        val result = compile(source)

        /* Then */
        assertEquals(KotlinCompilation.ExitCode.COMPILATION_ERROR, result.exitCode)
        assertContains(
            message = "Error message is printed for the receiver type",
            charSequence = result.messages,
            other = "MySealedClass\$sealedObjectInstances.kt:4:12 'public' member exposes its 'internal' receiver type Companion",
        )
    }
}
