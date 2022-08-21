package fr.smarquis.sealed

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
import org.intellij.lang.annotations.Language

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
            .forEach(::createSealedObjectInstancesExtension)
        return emptyList()
    }

    private fun createSealedObjectInstancesExtension(parent: KSClassDeclaration) {
        val file = parent.containingFile!!
        val simpleName = parent.simpleName.asString()
        val qualifiedName = parent.qualifiedName!!.asString()
        val sealedObjectInstances = parent.sealedObjectInstances()
        val packageName = file.packageName.asString()

        @Language("kotlin")
        val kotlin = """
            ${if (packageName.isNotBlank()) "package $packageName" else ""}
            
            import kotlin.reflect.KClass
            import $qualifiedName
            
            /** @return The list of sealed object instances of type [$simpleName]. */
            @Suppress("UnusedReceiverParameter")
            fun KClass<$simpleName>.sealedObjectInstances(): Set<$simpleName> = ${sealedObjectInstances.buildSet()}
            
        """.trimIndent()

        environment.codeGenerator.createNewFile(
            dependencies = Dependencies(
                aggregating = true,
                sources = sealedObjectInstances.mapNotNull { it.containingFile }.plus(file).distinct().toTypedArray()
            ),
            packageName = packageName,
            fileName = "$" + parent.simpleName.asString()
        ).write(kotlin.toByteArray())
    }

    private fun Set<KSClassDeclaration>.buildSet() = joinToString(prefix = "setOf(", separator = ", ", postfix = ")") {
        it.qualifiedName!!.asString()
    }

}
