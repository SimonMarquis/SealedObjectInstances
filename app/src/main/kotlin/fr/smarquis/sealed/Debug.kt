package fr.smarquis.sealed

@SealedObjectInstances
sealed class Debug(override val isEnabled: Boolean = false): FeatureFlag() {
    object Logs: Debug(true)
    object Traces: Debug()
    object StrictMode: Debug()
}
