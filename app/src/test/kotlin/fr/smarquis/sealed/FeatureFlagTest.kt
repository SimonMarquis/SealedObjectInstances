package fr.smarquis.sealed

import kotlin.test.Test
import kotlin.test.assertEquals


class FeatureFlagTest {

    @Test
    fun `FeatureFlag sealedObjectInstances`() = assertEquals(
        expected = Debug.sealedObjectInstances() + UI.sealedObjectInstances(),
        actual = FeatureFlag.sealedObjectInstances(),
    )

    @Test
    fun `Debug sealedObjectInstances`() = assertEquals(
        expected = setOf(Debug.Logs, Debug.StrictMode, Debug.Traces),
        actual = Debug.sealedObjectInstances(),
    )

    @Test
    fun `UI sealedObjectInstances`() = assertEquals(
        expected = setOf(UI.Animations, UI.Framerate),
        actual = UI.sealedObjectInstances(),
    )

}
