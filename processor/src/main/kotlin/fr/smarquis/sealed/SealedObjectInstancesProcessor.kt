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

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.getVisibility
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.Modifier.SEALED
import com.google.devtools.ksp.symbol.Variance
import com.google.devtools.ksp.symbol.Variance.CONTRAVARIANT
import com.google.devtools.ksp.symbol.Variance.COVARIANT
import com.google.devtools.ksp.symbol.Variance.INVARIANT
import com.google.devtools.ksp.symbol.Variance.STAR
import com.google.devtools.ksp.symbol.Visibility.INTERNAL
import com.google.devtools.ksp.symbol.Visibility.JAVA_PACKAGE
import com.google.devtools.ksp.symbol.Visibility.LOCAL
import com.google.devtools.ksp.symbol.Visibility.PRIVATE
import com.google.devtools.ksp.symbol.Visibility.PROTECTED
import com.google.devtools.ksp.symbol.Visibility.PUBLIC
import fr.smarquis.sealed.SealedObjectInstances.Visibility
import fr.smarquis.sealed.SealedObjectInstances.Visibility.Internal
import fr.smarquis.sealed.SealedObjectInstances.Visibility.Private
import fr.smarquis.sealed.SealedObjectInstances.Visibility.Public
import fr.smarquis.sealed.SealedObjectInstances.Visibility.Unspecified
import fr.smarquis.sealed.SealedObjectInstancesDataClass.Companion.toDataClass
import java.io.OutputStreamWriter
import kotlin.reflect.KClass
import kotlin.text.Typography.ellipsis

internal class SealedObjectInstancesProcessor(
    private val environment: SymbolProcessorEnvironment,
) : SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        resolver.getSymbolsWithAnnotation(SealedObjectInstances::class.qualifiedName!!)
            .filterIsInstance<KSClassDeclaration>()
            .groupBy { it.sealedClass() }
            .filterNotNullValues()
            .mapValues { it.annotations() }
            .forEach { it.process() }
        return emptyList()
    }

    /**
     * @return the corresponding sealed class declaration for the [this] declaration.
     */
    private fun KSClassDeclaration.sealedClass() = when {
        SEALED in modifiers -> this
        isCompanionObject -> parentDeclaration as KSClassDeclaration
        else -> environment.logger.error(
            message = "Failed to find a corresponding sealed class!",
            symbol = this,
        ).let { null }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <K : Any, V> Map<K?, V>.filterNotNullValues(): Map<K, V> = filterKeys { it != null } as Map<K, V>

    /**
     * @return the [List] of [SealedObjectInstances] annotations for [this] entry.
     */
    @OptIn(KspExperimental::class)
    private fun Map.Entry<KSClassDeclaration, List<KSClassDeclaration>>.annotations() = value
        .flatMap { it.getAnnotationsByType(SealedObjectInstances::class) }
        .distinctBy { it.toDataClass() }
        .also {
            if (it.size == it.distinctBy(SealedObjectInstances::name).size) return@also
            environment.logger.error(
                message = "Duplicated names: ${it.groupingBy(SealedObjectInstances::name).eachCount()}",
                symbol = key,
            )
        }

    private fun Map.Entry<KSClassDeclaration, List<SealedObjectInstances>>.process() = value
        .groupBy { it.fileName.takeUnless(String::isBlank) }
        .mapKeys { key.createNewFile(it.key) }
        .forEach { (file, annotations) ->
            file.writer().use {
                it.appendHeader(key)
                annotations.forEach { annotation ->
                    it.appendMethod(key, annotation)
                }
            }
        }

    private fun KSClassDeclaration.createNewFile(fileName: String?) = runCatching {
        environment.codeGenerator.createNewFile(
            dependencies = Dependencies(
                aggregating = true,
                sources = sealedObjectInstances().map { it.containingFile!! }.plus(containingFile!!).toTypedArray(),
            ),
            packageName = containingFile!!.packageName.asString(),
            fileName = fileName ?: "${simpleName.asString()}\$sealedObjectInstances",
        )
    }.onFailure {
        when (it) {
            is FileAlreadyExistsException -> environment.logger.error(
                message = "Duplicated file detected! You can override the generated file name with @${SealedObjectInstances::class.simpleName}(${SealedObjectInstances::fileName.name}=\"$ellipsis\")",
                symbol = this,
            )
        }
    }.getOrThrow()

    private fun OutputStreamWriter.appendHeader(klass: KSClassDeclaration) = apply {
        val packageName = klass.containingFile!!.packageName.asString()
        if (packageName.isNotBlank()) appendLine("package $packageName\n")
    }

    private fun OutputStreamWriter.appendMethod(sealed: KSClassDeclaration, annotation: SealedObjectInstances) {
        val visibility = sealed.getVisibility(annotation)
        val sealedClassName = sealed.qualifiedName!!.asString()
        val genericReceiverType = sealed.genericsReceiverTypes().orEmpty()
        val receiverType = "${KClass::class.qualifiedName}<$sealedClassName$genericReceiverType>"
        val methodName = annotation.name.takeUnless(String::isEmpty)
        val rawClassName = annotation.rawType.kClass.qualifiedName
        val genericReturnType = sealed.genericsReturnTypes().orEmpty()
        val returnType = if (annotation.returnType) ": $rawClassName<$sealedClassName$genericReturnType>" else ""
        val collectionBuilder = sealed.sealedObjectInstances().joinToString(
            prefix = annotation.rawType.builder.name + "(",
            separator = ", ",
            postfix = ")",
        ) { it.qualifiedName!!.asString() }

        // language=kotlin ~ Extension on the KClass
        """
        /** @return [$rawClassName] of sealed object instances of type [$sealedClassName]. */
        ${visibility.modifier()} fun $receiverType.$methodName()$returnType = $collectionBuilder
        """.trimIndent().let(::appendLine)

        sealed.companionOrNull()?.let {
            val companionVisibility = it.getVisibility(annotation)
            // language=kotlin ~ Extension on the companion object
            """
            /** @return [$rawClassName] of sealed object instances of type [$sealedClassName]. */
            ${companionVisibility.modifier()} fun $sealedClassName.$it.$methodName()$returnType = $sealedClassName::class.$methodName()
            """.trimIndent().let(::appendLine)
        }
    }

    /**
     * @return the companion object of [this] declaration or `null` if it does not exist.
     */
    private fun KSClassDeclaration.companionOrNull() = declarations
        .filterIsInstance<KSClassDeclaration>()
        .singleOrNull { it.isCompanionObject }

    private fun KSClassDeclaration.getVisibility(
        annotation: SealedObjectInstances,
    ): Visibility = when (val visibility = annotation.visibility) {
        Public, Internal, Private -> visibility
        Unspecified -> when (getVisibility()) {
            PUBLIC -> Public
            PROTECTED, INTERNAL, LOCAL, JAVA_PACKAGE -> Internal
            PRIVATE -> Private
        }
    }.also { if (it == Private) environment.logger.error("Unsupported [private] visibility.", this) }

    private fun Visibility.modifier() = when (this) {
        Unspecified, Public -> "public"
        Internal -> "internal"
        Private -> "private"
    }

    private fun KSClassDeclaration.generics(): List<Pair<Variance, String?>>? = typeParameters
        .takeUnless { it.isEmpty() }
        ?.map {
            val bounds = it.bounds.toList()
            when {
                bounds.size > 1 -> TODO("Unsupported multi bounds type parameters!")
                bounds.isEmpty() -> it.variance to null
                else -> it.variance to bounds.single().resolve().declaration.qualifiedName!!.asString()
            }
        }

    private fun KSClassDeclaration.genericsReceiverTypes(): String? = generics()
        ?.joinToString(prefix = "<", separator = ", ", postfix = ">") { "*" }

    private fun KSClassDeclaration.genericsReturnTypes(): String? = generics()
        ?.joinToString(prefix = "<", separator = ", ", postfix = ">") { (variance, type) ->
            if (type == null) return@joinToString "*"
            when (variance) {
                INVARIANT -> "out $type"
                COVARIANT -> type
                CONTRAVARIANT -> "*"
                STAR -> TODO("Unsupported STAR variance!")
            }
        }
}
