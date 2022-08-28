package fr.smarquis.sealed

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSNode
import com.google.devtools.ksp.symbol.Modifier.SEALED
import com.google.devtools.ksp.validate
import java.io.OutputStreamWriter
import kotlin.reflect.KClass


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
            .filter(KSNode::validate)
            .filter { SEALED in it.modifiers }
            .forEach(::process)
        return emptyList()
    }

    private fun process(declaration: KSClassDeclaration) {
        declaration.createNewFile().writer().use {
            it.appendHeader(declaration)
            declaration.annotations().forEach { annotation ->
                it.appendMethod(declaration, annotation)
            }
        }
    }

    @OptIn(KspExperimental::class)
    private fun KSClassDeclaration.annotations() = getAnnotationsByType(SealedObjectInstances::class).toList()

    private fun KSClassDeclaration.createNewFile() = environment.codeGenerator.createNewFile(
        dependencies = Dependencies(
            aggregating = true,
            sources = sealedObjectInstances().map { it.containingFile!! }.plus(containingFile!!).toTypedArray()
        ),
        packageName = containingFile!!.packageName.asString(),
        fileName = "${simpleName.asString()}\$sealedObjectInstances"
    )

    private fun OutputStreamWriter.appendHeader(klass: KSClassDeclaration) = apply {
        val packageName = klass.containingFile!!.packageName.asString()
        if (packageName.isNotBlank()) appendLine("package $packageName\n")
    }

    @Suppress("UnusedReceiverParameter")
    private fun OutputStreamWriter.appendMethod(sealed: KSClassDeclaration, annotation: SealedObjectInstances) {
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
        fun ${KClass::class.qualifiedName}<$sealedClassName>.$methodName(): $rawClassName<$sealedClassName> = $collectionBuilder
        """.trimIndent().let(::appendLine)
    }

}
