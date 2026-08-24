package com.example.core.substrate

import kotlin.math.sqrt

class SemanticEngine {
    val dim = 10
    
    val themePrototypes = mapOf(
        "isolation" to listOf("alone", "solitude", "isolation", "loneliness", "silence", "separation"),
        "meaning" to listOf("purpose", "meaning", "significance", "value", "why", "matter", "worth"),
        "conflict" to listOf("struggle", "conflict", "fight", "tension", "opposition", "battle"),
        "technology" to listOf("machine", "system", "code", "artificial", "intelligence", "robot", "circuit"),
        "space" to listOf("cosmos", "void", "orbit", "stars", "spaceship", "planet", "universe"),
        "memory" to listOf("remember", "memory", "past", "echo", "forgotten", "recollection"),
        "transformation" to listOf("change", "become", "evolve", "transform", "metamorphosis", "shift"),
        "connection" to listOf("together", "bond", "relationship", "intimacy", "contact", "presence"),
        "loss" to listOf("grief", "absence", "disappearance", "ending", "vanishing", "gone"),
        "creation" to listOf("create", "make", "build", "invent", "generate", "birth", "origin")
    )
    
    val themeKeys = themePrototypes.keys.toList()

    fun embed(text: String): DoubleArray {
        val t = text.lowercase()
        val vec = DoubleArray(dim)
        var totalScore = 0.0
        
        for (i in 0 until dim) {
            val theme = themeKeys[i]
            val words = themePrototypes[theme]!!
            var score = 0.0
            for (w in words) {
                if (t.contains(w)) {
                    score += 1.0
                }
            }
            vec[i] = score
            totalScore += score * score
        }
        
        val norm = sqrt(totalScore)
        if (norm > 0.0) {
            for (i in 0 until dim) {
                vec[i] /= norm
            }
        }
        return vec
    }

    fun tagsFor(text: String, threshold: Double = 0.30): Set<String> {
        val vec = embed(text)
        val tags = mutableSetOf<String>()
        for (i in 0 until dim) {
            if (vec[i] > threshold) {
                tags.add(themeKeys[i])
            }
        }
        if (tags.isEmpty()) {
            tags.add("neutral")
        }
        return tags
    }
}
