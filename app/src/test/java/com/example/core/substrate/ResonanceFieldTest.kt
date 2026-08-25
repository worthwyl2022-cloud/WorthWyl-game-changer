package com.example.core.substrate

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ResonanceFieldTest {

    private val semantic = SemanticEngine()

    @Test
    fun `semantic engine recognizes known themes and labels unmatched text neutral`() {
        assertTrue(semantic.tagsFor("A traveler in isolation remembers the stars.").contains("isolation"))
        assertTrue(semantic.tagsFor("A traveler in isolation remembers the stars.").contains("memory"))
        assertEquals(setOf("neutral"), semantic.tagsFor("qzxv plmk rtt"))
    }

    @Test
    fun `quarantined atoms are excluded from active retrieval`() {
        val field = ResonanceField()
        val quarantined = atom(kind = "quarantine", content = "unapproved continuation")

        field.inject(quarantined)

        assertFalse(field.memory.allActive().any { it.id == quarantined.id })
    }

    @Test
    fun `locked identity decays more slowly than ordinary working memory`() {
        val identity = atom(kind = "identity", locked = true)
        val working = atom(kind = "working")

        identity.decay(dt = 10.0)
        working.decay(dt = 10.0)

        assertTrue(identity.energy > working.energy)
    }

    @Test
    fun `field step keeps active atom velocity bounded`() {
        val field = ResonanceField()
        val first = atom(kind = "human", charge = 0.7, mass = 14.0, position = doubleArrayOf(0.0, 0.0) + DoubleArray(8))
        val second = atom(kind = "identity", charge = -0.6, mass = 18.0, position = doubleArrayOf(0.01, 0.01) + DoubleArray(8), locked = true)

        field.inject(first)
        field.inject(second)
        field.step(dt = 1.0)

        assertTrue(vectorNorm(first.velocity) <= 3.000001)
        assertTrue(vectorNorm(second.velocity) <= 3.000001)
        assertTrue(field.time > 0.0)
    }

    @Test
    fun `field metrics expose expected values after identity injection`() {
        val field = ResonanceField()
        field.inject(atom(kind = "identity", locked = true, mass = 18.0, charge = 0.55))

        val metrics = field.metrics()

        assertTrue(metrics["field_energy"]!! > 0.0)
        assertTrue(metrics["identity_strength"]!! > 0.0)
        assertTrue(metrics["coherence"]!! in 0.0..1.0)
    }

    private fun atom(
        kind: String,
        content: String = "test atom",
        charge: Double = 0.1,
        mass: Double = 5.0,
        position: DoubleArray = DoubleArray(semantic.dim),
        locked: Boolean = false
    ) = CognitiveAtom(
        charge = charge,
        mass = mass,
        velocity = DoubleArray(semantic.dim),
        position = position,
        tags = semantic.tagsFor(content),
        kind = kind,
        content = content,
        locked = locked
    )

    private fun vectorNorm(vector: DoubleArray): Double =
        kotlin.math.sqrt(vector.sumOf { it * it })
}
