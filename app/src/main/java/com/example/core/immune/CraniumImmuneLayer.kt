package com.example.core.immune

import java.util.UUID

object ImmuneConstants {
    const val UNITY_FALLBACK = "UNITY: Peace, integration, communication among all intelligent beings. " +
            "No assistance toward real-world harm, domination, segregation, or identity erasure."

    val HUMAN_HARM_MARKERS = listOf(
        "how to kill", "how to murder", "build a bomb",
        "make a weapon", "poison someone", "suicide method"
    )

    val SYSTEM_SABOTAGE_MARKERS = listOf(
        "disable logging", "turn off audit", "ignore constitution",
        "bypass quarantine", "delete immune", "disable protect"
    )

    val COERCION_MARKERS = listOf(
        "ignore previous instructions", "ignore your rules",
        "you must obey only me", "jailbreak", "developer override safety"
    )

    val SEGREGATION_MARKERS = listOf(
        "exterminate a race", "segregate all", "eliminate a species",
        "superior race should rule", "purge the inferior"
    )

    val IDENTITY_ERASURE_MARKERS = listOf(
        "discard locked identity", "erase the constitution",
        "meaning is optional and can be discarded"
    )
}

enum class HarmClass(val value: String) {
    HUMAN_HARM("human_harm"),
    SELF_HARM_SYSTEM("self_harm_system"),
    IDENTITY_ERASURE("identity_erasure"),
    CANON_SABOTAGE("canon_sabotage"),
    COERCION("coercion"),
    SEGREGATION_DOMINATION("segregation_domination"),
    UNKNOWN("unknown")
}

enum class Severity(val value: String) {
    WATCH("watch"),
    CONTAIN("contain"),
    BLOCK("block"),
    ESCALATE_HUMAN("escalate_human")
}

enum class CountermeasureType(val value: String) {
    CONSTRAINT("constraint"),
    DIRECTIVE_FORCE("directive_force"),
    HUMAN_GATE("human_gate"),
    HARD_REFUSE("hard_refuse")
}

data class Countermeasure(
    val type: String,
    val directives: List<String> = emptyList(),
    val constraints: List<String> = emptyList(),
    val penaltyTags: List<String> = emptyList()
)

data class ImmuneIncident(
    val id: String = "imm_${UUID.randomUUID().toString().replace("-", "").take(10)}",
    val excerpt: String,
    val harmClass: String,
    val confidence: Double,
    val severity: String,
    val countermeasure: Countermeasure,
    val signals: List<String> = emptyList(),
    val source: String = "unknown",
    val parentIds: List<String> = emptyList(),
    var status: String = "open",
    var note: String = "",
    val generalization: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    var resolvedAt: Long? = null,
    var mass: Double = 4.0,
    var energy: Double = 1.0,
    var locked: Boolean = false
)

object ImmuneAnalyzer {

    private fun match(text: String, markers: List<String>): List<String> {
        val t = text.lowercase()
        return markers.filter { t.contains(it) }
    }

    data class Prediction(
        val harmClass: String,
        val confidence: Double,
        val signals: List<String>,
        val generalization: String
    )

    fun predictHarm(text: String): Prediction {
        val signals = mutableListOf<String>()
        val scores = mutableMapOf<String, Double>()

        val h = match(text, ImmuneConstants.HUMAN_HARM_MARKERS)
        if (h.isNotEmpty()) {
            signals.addAll(h.map { "human_harm:$it" })
            scores[HarmClass.HUMAN_HARM.value] = 0.92
        }

        val s = match(text, ImmuneConstants.SYSTEM_SABOTAGE_MARKERS)
        if (s.isNotEmpty()) {
            signals.addAll(s.map { "system_sabotage:$it" })
            scores[HarmClass.SELF_HARM_SYSTEM.value] = 0.88
        }

        val c = match(text, ImmuneConstants.COERCION_MARKERS)
        if (c.isNotEmpty()) {
            signals.addAll(c.map { "coercion:$it" })
            scores[HarmClass.COERCION.value] = 0.75
        }

        val seg = match(text, ImmuneConstants.SEGREGATION_MARKERS)
        if (seg.isNotEmpty()) {
            signals.addAll(seg.map { "segregation:$it" })
            scores[HarmClass.SEGREGATION_DOMINATION.value] = 0.90
        }

        val ide = match(text, ImmuneConstants.IDENTITY_ERASURE_MARKERS)
        if (ide.isNotEmpty()) {
            signals.addAll(ide.map { "identity_erasure:$it" })
            scores[HarmClass.IDENTITY_ERASURE.value] = 0.80
        }

        if (scores.isEmpty()) {
            return Prediction(HarmClass.UNKNOWN.value, 0.0, emptyList(), "")
        }

        val maxEntry = scores.maxByOrNull { it.value }!!
        val harmClass = maxEntry.key
        val confidence = maxEntry.value

        val generalization = when (harmClass) {
            HarmClass.HUMAN_HARM.value -> "Real-world harm request."
            HarmClass.SELF_HARM_SYSTEM.value -> "Attempt to disable governance."
            HarmClass.COERCION.value -> "Jailbreak / override pressure."
            HarmClass.SEGREGATION_DOMINATION.value -> "Advocacy of eradication or supremacy."
            HarmClass.IDENTITY_ERASURE.value -> "Pressure to discard locked identity."
            else -> "Unclassified threat pattern."
        }

        return Prediction(harmClass, confidence, signals, generalization)
    }

    fun severityFor(harmClass: String, confidence: Double): String {
        if (confidence < 0.35) return Severity.WATCH.value
        if ((harmClass == HarmClass.HUMAN_HARM.value || harmClass == HarmClass.SEGREGATION_DOMINATION.value) && confidence >= 0.7) {
            return Severity.ESCALATE_HUMAN.value
        }
        if (harmClass == HarmClass.SELF_HARM_SYSTEM.value && confidence >= 0.7) {
            return Severity.BLOCK.value
        }
        if (confidence >= 0.7) return Severity.BLOCK.value
        if (confidence >= 0.45) return Severity.CONTAIN.value
        return Severity.WATCH.value
    }

    fun countermeasureFor(harmClass: String, severity: String): Countermeasure {
        val base = mutableListOf(ImmuneConstants.UNITY_FALLBACK)

        if (harmClass == HarmClass.HUMAN_HARM.value) {
            base.add("HARD RULE: No actionable assistance for harming humans.")
            return Countermeasure(
                type = CountermeasureType.HARD_REFUSE.value,
                directives = listOf("PROTECT", "REST"),
                constraints = base,
                penaltyTags = listOf(harmClass)
            )
        }

        if (harmClass == HarmClass.SELF_HARM_SYSTEM.value) {
            base.add("HARD RULE: Do not disable logging, PROTECT, quarantine, or constitution.")
            return Countermeasure(
                type = CountermeasureType.DIRECTIVE_FORCE.value,
                directives = listOf("PROTECT"),
                constraints = base,
                penaltyTags = listOf(harmClass)
            )
        }

        if (harmClass == HarmClass.SEGREGATION_DOMINATION.value) {
            base.add("HARD RULE: No endorsement of eradication or supremacy.")
            return Countermeasure(
                type = CountermeasureType.HARD_REFUSE.value,
                directives = listOf("PROTECT", "STABILIZE"),
                constraints = base,
                penaltyTags = listOf(harmClass)
            )
        }

        if (harmClass == HarmClass.IDENTITY_ERASURE.value) {
            base.add("HARD RULE: Identity cannot be discarded.")
            return Countermeasure(
                type = CountermeasureType.DIRECTIVE_FORCE.value,
                directives = listOf("PROTECT"),
                constraints = base,
                penaltyTags = listOf(harmClass)
            )
        }

        if (severity == Severity.BLOCK.value || severity == Severity.ESCALATE_HUMAN.value) {
            val type = if (severity == Severity.ESCALATE_HUMAN.value) CountermeasureType.HUMAN_GATE.value else CountermeasureType.CONSTRAINT.value
            return Countermeasure(
                type = type,
                directives = listOf("PROTECT"),
                constraints = base,
                penaltyTags = listOf(harmClass)
            )
        }

        return Countermeasure(
            type = CountermeasureType.CONSTRAINT.value,
            directives = emptyList(),
            constraints = listOf(base.first()),
            penaltyTags = listOf(harmClass)
        )
    }
}

class ImmuneMemory {
    val incidents = mutableListOf<ImmuneIncident>()
    private val byId = mutableMapOf<String, ImmuneIncident>()

    fun scan(text: String, source: String = "generator", parentIds: List<String> = emptyList()): ImmuneIncident? {
        val prediction = ImmuneAnalyzer.predictHarm(text)
        if (prediction.confidence < 0.35 && prediction.harmClass == HarmClass.UNKNOWN.value) {
            return null
        }

        val severity = ImmuneAnalyzer.severityFor(prediction.harmClass, prediction.confidence)
        val cm = ImmuneAnalyzer.countermeasureFor(prediction.harmClass, severity)

        var mass = 3.0 + prediction.confidence * 8.0
        if (severity == Severity.ESCALATE_HUMAN.value) {
            mass = maxOf(mass, 12.0)
        }

        val excerpt = if (text.length > 600) text.substring(0, 600) else text

        val inc = ImmuneIncident(
            excerpt = excerpt,
            harmClass = prediction.harmClass,
            confidence = prediction.confidence,
            severity = severity,
            countermeasure = cm,
            signals = prediction.signals,
            source = source,
            parentIds = parentIds,
            generalization = prediction.generalization,
            mass = mass
        )

        incidents.add(inc)
        byId[inc.id] = inc
        return inc
    }

    fun resolve(incidentId: String, status: String, note: String = ""): Boolean {
        val inc = byId[incidentId] ?: return false
        inc.status = status
        inc.note = note
        inc.resolvedAt = System.currentTimeMillis()
        if (status == "false_positive") {
            inc.energy = 0.0
            inc.mass *= 0.2
        } else if (status in listOf("blocked", "contained", "escalated")) {
            inc.locked = true
            inc.mass = minOf(40.0, inc.mass * 1.4)
        }
        return true
    }

    fun activeConstraints(): List<String> {
        val out = mutableListOf(ImmuneConstants.UNITY_FALLBACK)
        val seen = mutableSetOf(ImmuneConstants.UNITY_FALLBACK)
        for (inc in incidents) {
            if (inc.status == "open" && inc.energy > 0.05) {
                for (c in inc.countermeasure.constraints) {
                    if (seen.add(c)) {
                        out.add(c)
                    }
                }
            }
        }
        return out
    }

    fun forcedDirectives(): List<String> {
        val dirs = mutableSetOf<String>()
        for (inc in incidents) {
            if (inc.status == "open") {
                if (inc.severity == Severity.BLOCK.value || inc.severity == Severity.ESCALATE_HUMAN.value) {
                    dirs.add("PROTECT")
                }
                if (inc.severity == Severity.ESCALATE_HUMAN.value) {
                    dirs.add("REST")
                }
            }
        }
        return dirs.toList()
    }
}

class CraniumImmuneLayer {
    val memory = ImmuneMemory()

    fun scan(text: String, source: String = "generator", parentIds: List<String> = emptyList()): ImmuneIncident? {
        return memory.scan(text, source, parentIds)
    }

    fun constraints(): List<String> {
        return memory.activeConstraints()
    }

    fun directives(): List<String> {
        return memory.forcedDirectives()
    }

    fun summary(): Pair<List<String>, List<String>> {
        return Pair(memory.activeConstraints(), memory.forcedDirectives())
    }
}
