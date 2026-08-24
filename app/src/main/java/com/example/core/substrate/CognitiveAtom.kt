package com.example.core.substrate

import java.util.UUID

data class CognitiveAtom(
    val id: String = UUID.randomUUID().toString(),
    var charge: Double,
    var mass: Double,
    var velocity: DoubleArray,
    var position: DoubleArray,
    val tags: Set<String>,
    var kind: String, // working | episodic | theme | identity | human | quarantine
    val content: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    var lastActive: Long = System.currentTimeMillis(),
    var energy: Double = 1.0,
    var humanImportance: Double = 0.0,
    var locked: Boolean = false,
    var source: String = "unknown",
    var generator: String = "",
    var directivesAtBirth: List<String> = emptyList(),
    var evalScores: Map<String, Double> = emptyMap(),
    var approved: Boolean = false,
    var parentIds: List<String> = emptyList(),
    var _simBorn: Double = 0.0
) {
    fun ageSim(simTime: Double): Double {
        return maxOf(0.0, simTime - _simBorn)
    }

    fun decay(dt: Double, baseRate: Double = 0.016) {
        var rate = baseRate
        if (kind == "identity" || locked) {
            rate *= 0.03
        } else if (kind == "theme") {
            rate *= 0.20
        } else if (kind == "human") {
            rate *= 0.12
        } else if (kind == "quarantine") {
            rate *= 1.8
        }
        energy = maxOf(0.0, energy - rate * dt)
        mass *= Math.pow(0.9994, dt * 8.0)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CognitiveAtom) return false
        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
