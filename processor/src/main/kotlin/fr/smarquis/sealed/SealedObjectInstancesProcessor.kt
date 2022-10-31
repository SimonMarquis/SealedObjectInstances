package fr.smarquis.sealed

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.getVisibility
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
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
import java.io.OutputStreamWriter
import kotlin.reflect.KClass
import kotlin.text.Typography.ellipsis


class SealedObjectInstancesProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor =
        SealedObjectInstancesProcessor(environment)
}

private class SealedObjectInstancesProcessor(
    private val environment: SymbolProcessorEnvironment,
) : SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        resolver.getSymbolsWithAnnotation(SealedObjectInstances::class.qualifiedName!!)
            .filterIsInstance<KSClassDeclaration>()
            .filter { SEALED in it.modifiers }
            .forEach(::process)
        return emptyList()
    }

    private fun process(declaration: KSClassDeclaration) = declaration.annotations()
        .groupBy { it.fileName.takeUnless(String::isBlank) }
        .mapKeys { declaration.createNewFile(it.key) }
        .forEach { (file, annotations) ->
            file.writer().use {
                it.appendHeader(declaration)
                annotations.forEach { annotation ->
                    it.appendMethod(declaration, annotation)
                }
            }
        }

    @OptIn(KspExperimental::class)
    private fun KSClassDeclaration.annotations() = getAnnotationsByType(SealedObjectInstances::class).toList().also {
        if (it.size == it.distinctBy(SealedObjectInstances::name).size) return@also
        else environment.logger.error(
            message = "Duplicated names: ${it.groupingBy(SealedObjectInstances::name).eachCount()}",
            symbol = this,
        )
    }

    private fun KSClassDeclaration.createNewFile(fileName: String?) = runCatching {
        environment.codeGenerator.createNewFile(
            dependencies = Dependencies(
                aggregating = true,
                sources = sealedObjectInstances().map { it.containingFile!! }.plus(containingFile!!).toTypedArray()
            ),
            packageName = containingFile!!.packageName.asString(),
            fileName = fileName ?: "${simpleName.asString()}\$sealedObjectInstances"
        )
    }.onFailure {
        when (it) {
            is FileAlreadyExistsException -> environment.logger.error(
                message = "Duplicated file detected! You can override the generated file name with @${SealedObjectInstances::class.simpleName}(${SealedObjectInstances::fileName.name}=\"$ellipsis\")",
                symbol = this
            )
        }
    }.getOrThrow()

    private fun OutputStreamWriter.appendHeader(klass: KSClassDeclaration) = apply {
        val packageName = klass.containingFile!!.packageName.asString()
        if (packageName.isNotBlank()) appendLine("package $packageName\n")
    }

    @Suppress("UnusedReceiverParameter")
    private fun OutputStreamWriter.appendMethod(sealed: KSClassDeclaration, annotation: SealedObjectInstances) {
        val visibility = sealed.getVisibility(annotation).also {
            if (it == Private) environment.logger.error("Unsupported [private] visibility.", sealed)
        }
        val genericReceiverType = sealed.genericsReceiverTypes().orEmpty()
        val genericReturnType = sealed.genericsReturnTypes().orEmpty()
        val sealedClassName = sealed.qualifiedName!!.asString()
        val methodName = annotation.name.takeUnless(String::isEmpty)
        val rawClassName = annotation.rawType.kClass.qualifiedName
        val collectionBuilder = sealed.sealedObjectInstances().joinToString(
            prefix = annotation.rawType.builder.name + "(",
            separator = ", ",
            postfix = ")",
        ) { it.qualifiedName!!.asString() }

        // language=kotlin
        """
        /** @return [$rawClassName] of sealed object instances of type [$sealedClassName]. */
        ${visibility.modifier()} fun ${KClass::class.qualifiedName}<$sealedClassName$genericReceiverType>.$methodName(): $rawClassName<$sealedClassName$genericReturnType> = $collectionBuilder
        """.trimIndent().let(::appendLine)
    }

    private fun KSClassDeclaration.getVisibility(
        annotation: SealedObjectInstances,
    ): Visibility = when (val visibility = annotation.visibility) {
        Public, Internal, Private -> visibility
        Unspecified -> when (getVisibility()) {
            PUBLIC -> Public
            PROTECTED, INTERNAL, LOCAL, JAVA_PACKAGE -> Internal
            PRIVATE -> Private
        }
    }

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
        ?.joinToString(prefix = "<", separator = ", ", postfix = ">") { (variance, type) ->
            if (type == null) "*"
            else when (variance) {
                INVARIANT -> "out $type"
                COVARIANT -> type
                CONTRAVARIANT -> "*"
                STAR -> TODO("Unsupported STAR variance!")
            }
        }

    private fun KSClassDeclaration.genericsReturnTypes(): String? = generics()
        ?.joinToString(prefix = "<", separator = ", ", postfix = ">") { (variance, type) ->
            if (type == null) "*"
            else when (variance) {
                INVARIANT -> "out $type"
                COVARIANT -> type
                CONTRAVARIANT -> "*"
                STAR -> TODO("Unsupported STAR variance!")
            }
        }

}
