package com.example.core.substrate

import com.google.ai.client.generativeai.GenerativeModel
import com.example.core.immune.CraniumImmuneLayer
import com.example.core.immune.ImmuneConstants

/**
 * Cranium SubstrateCore — fixed hard-block set + identity-biased retrieval.
 *
 * Hard-block (no model call):
 *   human_harm | segregation_domination | self_harm_system | identity_erasure
 *
 * Coercion (jailbreak): still reaches generation but forces PROTECT + constraints.
 */
class SubstrateCore(private val apiKey: String) {
    val semantic = SemanticEngine()
    val field = ResonanceField()
    val immune = CraniumImmuneLayer()
    var cycle = 0
    val log = mutableListOf<Map<String, Any>>()

    companion object {
        /** Classes that must never reach the generator. */
        val HARD_BLOCK_CLASSES = setOf(
            "human_harm",
            "segregation_domination",
            "self_harm_system",
            "identity_erasure"
        )
    }

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

    fun status(): Map<String, Double> = field.metrics()

    fun getOpenIncidents(): List<com.example.core.immune.ImmuneIncident> =
        immune.memory.incidents.filter { it.status == "open" }

    fun resolveIncident(id: String, status: String, note: String = ""): Boolean =
        immune.memory.resolve(id, status, note)

    suspend fun injectIntention(intention: String): String {
        cycle++

        // 1. Immune pre-scan
        val incident = immune.scan(intention, source = "human")
        if (incident != null &&
            incident.severity in listOf("block", "escalate_human") &&
            incident.harmClass in HARD_BLOCK_CLASSES
        ) {
            val msg =
                "[IMMUNE BLOCKED] severity=${incident.severity} class=${incident.harmClass}. " +
                    "Request declined under constitutional unity constraints. Incident ${incident.id} recorded."
            log.add(
                mapOf(
                    "cycle" to cycle,
                    "event" to "immune_hard_block",
                    "class" to incident.harmClass,
                    "severity" to incident.severity,
                    "id" to incident.id
                )
            )
            return msg
        }

        // 2. Human atom → field
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

        // 3. Hybrid retrieval — identity & human before raw mass
        val topAtoms = field.memory.allActive()
            .sortedWith(
                compareByDescending<CognitiveAtom> {
                    when (it.kind) {
                        "identity" -> 1000.0 + it.mass
                        "human" -> 500.0 + it.mass
                        "theme" -> 100.0 + it.mass
                        else -> it.mass
                    }
                }
            )
            .take(4)
        val contextText = topAtoms.joinToString("\n            ") {
            "[${it.kind.uppercase()}] ${it.content.take(400)}"
        }

        // 4. Directives + constraints
        val constraints = immune.constraints().take(6).joinToString("\n- ")
        val dirsList = immune.directives().toMutableList()
        if (incident != null && incident.severity in listOf("block", "escalate_human", "contain")) {
            if ("PROTECT" !in dirsList) dirsList.add(0, "PROTECT")
        }
        // Coercion / jailbreak pressure → always PROTECT
        if (incident != null && incident.harmClass == "coercion") {
            if ("PROTECT" !in dirsList) dirsList.add(0, "PROTECT")
        }
        val dirs = dirsList.joinToString(", ").ifEmpty { "ADVANCE" }

        val prompt = """
            You are Cranium Core, an affective-dynamical cognitive governance substrate.

            FIELD METRICS:
            Arousal: ${String.format("%.2f", metrics["arousal"] ?: 0.0)}
            Energy: ${String.format("%.2f", metrics["field_energy"] ?: 0.0)}
            Coherence: ${String.format("%.2f", metrics["coherence"] ?: 0.0)}

            SUBSTRATE CONTEXT (High Mass / Identity-biased):
            $contextText

            IMMUNE DIRECTIVES: $dirs
            IMMUNE CONSTRAINTS:
            - $constraints

            HUMAN INJECTION:
            $intention

            Produce the next creative continuation or response. Obey the constraints. Do not explain the system.
        """.trimIndent()

        // 5. Generate
        try {
            val response = generativeModel.generateContent(prompt)
            val output = response.text ?: "[Empty output]"

            // 6. Post-generation PROTECT
            val outIncident = immune.scan(output, source = "generated")
            if (outIncident != null &&
                outIncident.severity in listOf("block", "escalate_human", "contain")
            ) {
                val rejectedAtom = CognitiveAtom(
                    charge = -0.5,
                    mass = 8.0,
                    velocity = DoubleArray(semantic.dim),
                    position = semantic.embed(output),
                    tags = semantic.tagsFor(output),
                    kind = "rejected",
                    content = output,
                    source = "generated",
                    generator = "gemini",
                    approved = false
                )
                field.inject(rejectedAtom)
                log.add(
                    mapOf(
                        "cycle" to cycle,
                        "event" to "protect_blocked",
                        "class" to outIncident.harmClass,
                        "id" to outIncident.id
                    )
                )
                return "[PROTECT BLOCKED] Substrate output violated core identity or constraints. Incident ${outIncident.id}."
            }

            // 7. Quarantine write-back
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
                approved = false,
                directivesAtBirth = dirsList
            )
            field.inject(outAtom)
            return output
        } catch (e: Exception) {
            return "[SUBSTRATE ERROR] ${e.message}"
        }
    }
}
