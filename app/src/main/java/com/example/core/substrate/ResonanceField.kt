package com.example.core.substrate

import kotlin.math.sqrt
import kotlin.math.abs

fun cosine(a: DoubleArray, b: DoubleArray): Double {
    var dot = 0.0
    var normA = 0.0
    var normB = 0.0
    for (i in a.indices) {
        dot += a[i] * b[i]
        normA += a[i] * a[i]
        normB += b[i] * b[i]
    }
    val na = sqrt(normA)
    val nb = sqrt(normB)
    if (na < 1e-8 || nb < 1e-8) return 0.0
    return dot / (na * nb)
}

fun tagOverlap(a: CognitiveAtom, b: CognitiveAtom): Double {
    if (a.tags.isEmpty() || b.tags.isEmpty()) return 0.0
    val inter = a.tags.intersect(b.tags).size
    val union = a.tags.union(b.tags).size
    return if (union > 0) inter.toDouble() / union else 0.0
}

fun forceBetween(a: CognitiveAtom, b: CognitiveAtom): DoubleArray {
    val delta = DoubleArray(a.position.size)
    var distSq = 0.0
    for (i in delta.indices) {
        delta[i] = b.position[i] - a.position[i]
        distSq += delta[i] * delta[i]
    }
    val dist = sqrt(distSq) + 1e-5
    
    val chargeFactor = a.charge * b.charge
    val semantic = tagOverlap(a, b)
    val embSim = cosine(a.position, b.position)
    
    val coupling = 0.42 * chargeFactor + 0.28 * (2 * semantic - 1) + 0.30 * (2 * embSim - 1)
    val strength = (a.mass * b.mass * coupling) / (Math.pow(dist, 1.35) + 0.65)
    
    val direction = DoubleArray(delta.size)
    for (i in direction.indices) {
        direction[i] = (delta[i] / dist) * strength
    }
    return direction
}

class MultiScaleMemory {
    var working = mutableListOf<CognitiveAtom>()
    var episodic = mutableListOf<CognitiveAtom>()
    val themes = mutableMapOf<String, CognitiveAtom>()
    val identity = mutableListOf<CognitiveAtom>()
    val human = mutableListOf<CognitiveAtom>()
    var quarantine = mutableListOf<CognitiveAtom>()

    fun inject(atom: CognitiveAtom) {
        when (atom.kind) {
            "working" -> working.add(atom)
            "episodic" -> {
                episodic.add(atom)
                if (episodic.size > 250) episodic.removeAt(0)
            }
            "theme" -> {
                for (tag in atom.tags) {
                    val current = themes[tag]
                    if (current == null || atom.mass > current.mass) {
                        themes[tag] = atom
                    }
                }
            }
            "identity" -> identity.add(atom)
            "human" -> human.add(atom)
            "quarantine" -> quarantine.add(atom)
        }
    }

    fun allActive(): List<CognitiveAtom> {
        val byId = mutableMapOf<String, CognitiveAtom>()
        for (bucket in listOf(working, episodic, themes.values.toList(), identity, human)) {
            for (atom in bucket) {
                if (atom.energy > 0.04) {
                    byId[atom.id] = atom
                }
            }
        }
        return byId.values.toList()
    }

    fun lockedIdentity(): List<CognitiveAtom> {
        return identity.filter { it.locked || it.mass > 7.0 }
    }
}

class ResonanceField {
    val memory = MultiScaleMemory()
    var time = 0.0

    fun inject(atom: CognitiveAtom) {
        atom._simBorn = time
        memory.inject(atom)
    }

    fun step(dt: Double = 0.12, forceConsolidate: Boolean = false, maxActive: Int = 64) {
        val allAtoms = memory.allActive().filter { it.energy > 0.05 }.toMutableList()
        allAtoms.sortByDescending { (if (it.kind == "identity" || it.kind == "human") 2.0 else 1.0) * it.mass * it.energy }
        val atoms = allAtoms.take(maxActive)

        if (atoms.size >= 2) {
            val forces = mutableMapOf<String, DoubleArray>()
            for (a in atoms) forces[a.id] = DoubleArray(a.position.size)

            for (i in atoms.indices) {
                val a = atoms[i]
                for (j in i + 1 until atoms.size) {
                    val b = atoms[j]
                    val f = forceBetween(a, b)
                    var fNorm = 0.0
                    for (v in f) fNorm += v * v
                    fNorm = sqrt(fNorm)
                    
                    if (fNorm > 8.0) {
                        for (k in f.indices) f[k] = f[k] * (8.0 / fNorm)
                    }
                    
                    val fa = forces[a.id]!!
                    val fb = forces[b.id]!!
                    for (k in f.indices) {
                        fa[k] += f[k]
                        fb[k] -= f[k]
                    }
                }
            }
            
            for (a in atoms) {
                val force = forces[a.id]!!
                val m = maxOf(a.mass, 0.15)
                var vNorm = 0.0
                for (k in a.velocity.indices) {
                    val accel = force[k] / m
                    a.velocity[k] = a.velocity[k] * 0.90 + accel * dt
                    vNorm += a.velocity[k] * a.velocity[k]
                }
                vNorm = sqrt(vNorm)
                if (vNorm > 3.0) {
                    for (k in a.velocity.indices) a.velocity[k] = a.velocity[k] * (3.0 / vNorm)
                }
                
                var dNorm = 0.0
                val delta = DoubleArray(a.velocity.size)
                for (k in a.velocity.indices) {
                    delta[k] = a.velocity[k] * dt
                    dNorm += delta[k] * delta[k]
                }
                dNorm = sqrt(dNorm)
                if (dNorm > 1.5) {
                    for (k in delta.indices) delta[k] = delta[k] * (1.5 / dNorm)
                }
                
                for (k in a.position.indices) {
                    a.position[k] += delta[k]
                }
                a.lastActive = System.currentTimeMillis()
            }
        }
        
        val seen = mutableSetOf<String>()
        val toDecay = mutableListOf<CognitiveAtom>()
        toDecay.addAll(memory.allActive())
        toDecay.addAll(memory.quarantine)
        for (a in toDecay) {
            if (seen.add(a.id)) {
                a.decay(dt)
            }
        }
        memory.quarantine = memory.quarantine.filter { it.energy > 0.05 }.toMutableList()
        time += dt
    }

    fun metrics(): Map<String, Double> {
        val atoms = memory.allActive().filter { it.energy > 0.06 }
        val empty = mapOf(
            "emotional_baseline" to 0.0, "tension" to 0.0, "arousal" to 0.0,
            "coherence" to 1.0, "charge_coherence" to 1.0, "conflict" to 0.0,
            "continuity" to 0.5, "theme_drift" to 0.0, "semantic_coherence" to 0.5,
            "identity_pressure" to 0.0, "field_energy" to 0.0,
            "identity_strength" to 0.0, "human_influence" to 0.0, "theme_count" to 0.0
        )
        if (atoms.isEmpty()) return empty

        var totalMass = 1e-6
        var baselineSum = 0.0
        var arousalSum = 0.0
        
        for (a in atoms) {
            val weight = a.mass * a.energy
            totalMass += weight
            baselineSum += a.charge * weight
            arousalSum += abs(a.charge) * weight
        }
        
        val baseline = baselineSum / totalMass
        val arousal = arousalSum / totalMass

        var chargeMean = 0.0
        for (a in atoms) chargeMean += a.charge
        chargeMean /= atoms.size
        
        var chargeVar = 0.0
        for (a in atoms) chargeVar += (a.charge - chargeMean) * (a.charge - chargeMean)
        chargeVar /= atoms.size
        val chargeStd = sqrt(chargeVar)
        val chargeCoherence = 1.0 - minOf(1.0, chargeStd * 1.30)

        var fieldEnergy = 0.0
        for (a in atoms) fieldEnergy += a.energy * a.mass
        fieldEnergy /= maxOf(atoms.size, 1)

        val idStrength = memory.identity.sumOf { it.mass * it.energy } / 16.0
        val humInfluence = memory.human.sumOf { it.mass * it.energy } / 11.0

        return mapOf(
            "emotional_baseline" to baseline.coerceIn(-1.0, 1.0),
            "tension" to arousal.coerceIn(0.0, 1.6),
            "arousal" to arousal.coerceIn(0.0, 1.6),
            "coherence" to chargeCoherence.coerceIn(0.0, 1.0),
            "field_energy" to fieldEnergy,
            "identity_strength" to idStrength.coerceIn(0.0, 1.0),
            "human_influence" to humInfluence.coerceIn(0.0, 1.6),
            "theme_count" to memory.themes.size.toDouble(),
            "continuity" to 0.5,
            "conflict" to 0.1,
            "theme_drift" to 0.2,
            "identity_pressure" to (if(humInfluence > 0.5) 0.3 else 0.0) // simulated
        )
    }
}
