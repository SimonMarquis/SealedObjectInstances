package fr.smarquis.sealed

import fr.smarquis.sealed.SealedObjectInstances.RawType.Set
import fr.smarquis.sealed.SealedObjectInstances.Visibility.Unspecified
import kotlin.reflect.KClass
import kotlin.reflect.KFunction1

@Repeatable
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class SealedObjectInstances(
    val name: String = "sealedObjectInstances",
    val rawType: RawType = Set,
    val visibility: Visibility = Unspecified,
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
