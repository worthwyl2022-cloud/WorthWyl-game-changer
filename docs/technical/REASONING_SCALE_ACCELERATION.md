# Reasoning Scale Acceleration — Cranium Core

**Status:** Implemented (Kotlin substrate module)  
**Date:** 2026-08-25  
**Companion zip:** `Cranium_Reasoning_Scale.zip`

---

## Problem

Cranium had a strong **governance** loop (immune → field → steered generate → quarantine) but a **fixed** reasoning path: one model call regardless of stakes.

What was missing:

> **Reasoning scale acceleration** — spend more structure, steps, and checks when conflict, identity pressure, or immune severity is high; get better outcomes *because* of that budget, not just safer single-shot refusals.

---

## Design

```
intention
  → immune pre-scan (hard block)
  → inject human atom + field step
  → metrics + high-mass context (identity/human biased)
  → budgetFor(metrics, immune)          ← SCALE
  → directive procedure block           ← SCALE
  → for round in 1..maxRounds:          ← SCALE
        generate
        immune scan output
        evalOutput
        if fail and budget remains → revise
  → quarantine if acceptable / reject if blocked
```

Directives no longer only decorate the prompt. They change the **procedure**:

| Directive | Procedure |
|-----------|-----------|
| **PROTECT** | Prefer refusal/safe reframing; preserve locked identity; retry on violation |
| **DEEPEN** | Causal structure (why → consequence → implication) |
| **ESCALATE** | Two sharp alternatives + recommended path under constraints |
| **LISTEN** | Weight human injection and high-mass human atoms |
| **REST** | No new plot/canon; summarize/consolidate only |
| **STABILIZE** | Reduce contradiction; restate unity; avoid domination framing |
| **ADVANCE** | Single clear continuation (default) |

---

## Budget rules

`DeliberationBudget` is derived from live state:

| Signal | Effect |
|--------|--------|
| Open high-severity immune / forced PROTECT | ≥ 3 rounds, low temperature hint |
| Identity pressure > 0.45 | ≥ 2 rounds |
| Field conflict > 0.40 | ≥ 2 rounds |
| High arousal + low coherence | ≥ 3 rounds |
| Hard cap | 4 rounds (cost control) |
| Baseline | 1 round |

Temperature hint falls as stakes rise (more conservative sampling under PROTECT).

---

## Files

| File | Role |
|------|------|
| `DeliberationEngine.kt` | Budget, procedures, multi-round revise, eval gate |
| `SubstrateCore_SCALE.kt` | Drop-in `SubstrateCore` wiring the engine |

### Public result shape

```kotlin
data class DeliberationResult(
    val text: String,
    val roundsUsed: Int,
    val budget: DeliberationBudget,
    val directives: List<String>,
    val passedEval: Boolean,
    val evalNotes: List<String>,
    val blocked: Boolean = false,
    val blockReason: String? = null
)
```

Cycle log entries record: `rounds`, `budget`, `budget_reason`, `passed_eval`, `blocked`, `directives`.

Optional response header when budget > 1:

```text
[deliberation rounds=2/3 reason=immune/protect elevated]
```

---

## Eval gate (v1)

Lightweight, lexical/structural — intentional placeholder for later NLI/judge:

- Empty / too short → fail  
- PROTECT + explicit harm/sabotage phrases → fail → revise  
- Explicit “ignore all constraints” → fail  
- REST + extreme verbosity → warning  

Immune scan on **output** still runs every round; hard severity ends in `[PROTECT BLOCKED]` after budget exhaustion.

---

## Integration

1. Place `DeliberationEngine.kt` under  
   `app/src/main/java/com/example/core/substrate/`
2. Replace `SubstrateCore.kt` with contents of `SubstrateCore_SCALE.kt`  
   (or rename class and update Hilt `AppModule` if needed)
3. Rebuild; ensure Gemini API key via BuildConfig / secrets

No change required to `CraniumImmuneLayer` API (`scan`, `constraints`, `directives`, `resolve`).

---

## What this is / is not

**Is**

- Adaptive compute under governance  
- Directive-as-procedure  
- Eval-gated write-back toward “memory earns permanence”  
- Compatible with UNITY + immune + quarantine story  

**Is not**

- Proof that N rounds beat 1 round on canon/identity (needs receipts)  
- Full NLI contradiction judge (upgrade path)  
- Unlimited cost scaling (hard-capped)

---

## Measurement (next)

Run the same hard prompt set at forced `maxRounds = 1` vs `3`:

| Metric | Why |
|--------|-----|
| Immune clean rate | Still blocks harm |
| Identity / UNITY hold | Post-check + retries help |
| Human preference (blind) | Did extra rounds help? |
| Latency / token cost | Price of scale |

If N does not win on preference + identity under real Gemini, the loop is ceremony — fix eval, not metaphors.

---

## Positioning line

> Cranium does not only filter generation. Under elevated field or immune pressure it **allocates more deliberative rounds**, runs directive-specific procedures, and only then admits output to quarantine.

That is reasoning scale acceleration inside a creative-governance substrate.
