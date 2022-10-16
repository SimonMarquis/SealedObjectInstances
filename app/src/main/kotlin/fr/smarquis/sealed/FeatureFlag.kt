package fr.smarquis.sealed

@SealedObjectInstances
sealed class FeatureFlag {
    abstract val isEnabled: Boolean
}
