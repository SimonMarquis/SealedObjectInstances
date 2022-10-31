package fr.smarquis.sealed

import fr.smarquis.sealed.SealedObjectInstances.RawType.Set
import fr.smarquis.sealed.SealedObjectInstances.Visibility.Unspecified
import kotlin.reflect.KClass
import kotlin.reflect.KFunction1

/**
 * This repeatable annotation is the main entry point to configure the generated sealed class extensions.
 */
@Repeatable
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class SealedObjectInstances(
    /**
     * Method name to access sealed object instances.
     */
    val name: String = "sealedObjectInstances",
    /**
     * The collection type the generated method.
     */
    val rawType: RawType = Set,
    /**
     * The visibility of the generated method.
     */
    val visibility: Visibility = Unspecified,
    /**
     * The file name for the generated method. The default filename (when blank) will be the sealed class name with `$sealedObjectInstances` postfix.
     */
    val fileName: String = "",
) {

    enum class RawType(
        internal val kClass: KClass<*>,
        internal val builder: KFunction1<kotlin.Array<out Any>, Any>,
    ) {
        Array(kotlin.Array::class, ::arrayOf),
        List(kotlin.collections.List::class, ::listOf),
        Set(kotlin.collections.Set::class, ::setOf),
    }

    enum class Visibility { Unspecified, Public, Internal, Private }

}
