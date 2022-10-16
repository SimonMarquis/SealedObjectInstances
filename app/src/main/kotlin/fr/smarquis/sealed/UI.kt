package fr.smarquis.sealed

@SealedObjectInstances
sealed class UI(override val isEnabled: Boolean = false): FeatureFlag() {
    object Animations: UI()
    object Framerate: UI()
}
