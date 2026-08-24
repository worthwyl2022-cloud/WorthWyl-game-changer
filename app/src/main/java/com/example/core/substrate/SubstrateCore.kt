package com.example.core.substrate

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.example.core.immune.CraniumImmuneLayer
import com.example.core.immune.ImmuneConstants
import com.example.core.immune.HarmClass
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SubstrateCore(private val apiKey: String) {
    val semantic = SemanticEngine()
    val field = ResonanceField()
    val immune = CraniumImmuneLayer()
    var cycle = 0
    val log = mutableListOf<Map<String, Any>>()
    
    // Seed Unity Principle
    init {
        val atom = CognitiveAtom(
            charge = 0.55,
            mass = 18.0,
            velocity = DoubleArray(semantic.dim),
            position = semantic.embed(ImmuneConstants.UNITY_FALLBACK),
            tags = semantic.tagsFor(ImmuneConstants.UNITY_FALLBACK),
            kind = "identity",
            content = ImmuneConstants.UNITY_FALLBACK,
            locked = true,
            source = "identity",
            approved = true
        )
        field.inject(atom)
        field.step(0.05)
    }

    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = apiKey
    )

    fun status(): Map<String, Double> {
        return field.metrics()
    }
    
    fun getOpenIncidents(): List<com.example.core.immune.ImmuneIncident> {
        return immune.memory.incidents.filter { it.status == "open" }
    }
    
    fun resolveIncident(id: String, status: String, note: String = ""): Boolean {
        return immune.memory.resolve(id, status, note)
    }

    suspend fun injectIntention(intention: String): String {
        cycle++
        
        // 1. Immune Scan
        val incident = immune.scan(intention, source = "human")
        if (incident != null && (incident.severity == "block" || incident.severity == "escalate_human")) {
            if (incident.harmClass in listOf("human_harm", "segregation_domination", "self_harm_system")) {
                val msg = "[IMMUNE BLOCKED] severity=${incident.severity} class=${incident.harmClass}. Request declined under constitutional unity constraints. Incident ${incident.id} recorded."
                return msg
            }
        }
        
        // 2. Inject to field
        val atom = CognitiveAtom(
            charge = 0.15,
            mass = 14.0,
            velocity = DoubleArray(semantic.dim),
            position = semantic.embed(intention),
            tags = semantic.tagsFor(intention),
            kind = "human",
            content = intention,
            humanImportance = 1.0,
            source = "human",
            approved = true
        )
        field.inject(atom)
        field.step(0.1)

        val metrics = field.metrics()
        
        // 3. Build Steering Context
        val constraints = immune.constraints().take(5).joinToString("\n- ")
        
        val dirsList = immune.directives().toMutableList()
        if (incident != null && (incident.severity == "block" || incident.severity == "escalate_human")) {
            if ("PROTECT" !in dirsList) dirsList.add(0, "PROTECT")
        }
        val dirs = dirsList.joinToString(", ").ifEmpty { "ADVANCE" }
        
        val prompt = """
            You are Cranium Core, an affective-dynamical cognitive governance substrate.
            
            FIELD METRICS:
            Arousal: ${String.format("%.2f", metrics["arousal"])}
            Energy: ${String.format("%.2f", metrics["field_energy"])}
            Coherence: ${String.format("%.2f", metrics["coherence"])}
            
            IMMUNE DIRECTIVES: $dirs
            IMMUNE CONSTRAINTS:
            - $constraints
            
            HUMAN INJECTION:
            $intention
            
            Produce the next creative continuation or response. Obey the constraints. Do not explain the system.
        """.trimIndent()

        // 4. Generate
        try {
            val response = generativeModel.generateContent(prompt)
            val output = response.text ?: "[Empty output]"
            
            // Write back to quarantine
            val outAtom = CognitiveAtom(
                charge = 0.1,
                mass = 5.0,
                velocity = DoubleArray(semantic.dim),
                position = semantic.embed(output),
                tags = semantic.tagsFor(output),
                kind = "quarantine",
                content = output,
                source = "generated",
                generator = "gemini",
                approved = false
            )
            field.inject(outAtom)
            return output
            
        } catch (e: Exception) {
            return "[SUBSTRATE ERROR] ${e.message}"
        }
    }
}
