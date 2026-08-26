# LLM-Judge Contradiction Adapter

## Role

The judge module provides a diligence-grade escalation path for identity and principle contradiction checks. It complements, rather than replaces, the fast contradiction proxy.

## Two-Stage Evaluation

| Stage | Component | Responsibility |
|---|---|---|
| 1 | `ContradictionEngine` proxy | Fast lexical checks, paraphrase clustering, and mention or endorsement gates. |
| 2 | `LlmJudgeContradiction` | Real-model classification against locked principles using `contradicts`, `neutral`, or `supports` labels. |

The proxy remains useful as an offline prefilter and cost-control mechanism. Adversarial or escalated cases should be evaluated by the real model, with the final verdict recorded for audit.

## Integration Contract

The supplied package describes an adapter that receives a semantic engine, the existing contradiction proxy, a generative model, and an `alwaysJudgeAdversarial` setting. After generation, the substrate evaluates the output against locked identity memory and field context. If the verdict reports a violation, the system should enter a protection, rejection, or revision path rather than writing the output directly to durable memory.

A representative integration shape is:

```kotlin
val judge = LlmJudgeContradiction(
    semantic = semantic,
    proxy = contradiction,
    model = generativeModel,
    alwaysJudgeAdversarial = true
)

val verdict = judge.evaluate(
    output,
    field.memory.lockedIdentity(),
    metrics
)

if (verdict.violated) {
    // PROTECT: reject, revise, or safely reframe.
}
```

The exact implementation should be verified against the authoritative source repository before compilation or deployment.

## Operating Limits

Judge quality depends on the underlying model, prompt design, calibration, and human spot-checking. Published receipts should include representative false-positive and false-negative review. Cost and latency should be bounded; the source package proposes at most one judge call per locked principle on escalation paths, capped at six.

For auditability, logs should distinguish model-judge matches from proxy matches—for example, by recording `matchKind = "llm_judge"`—and should preserve the principle, output excerpt, verdict, model identifier, prompt version, and timestamp subject to privacy requirements.

## Diligence Position

This adapter is a remediation and validation component, not evidence that the prototype already contains a full natural-language-inference system. A trained CrossEncoder or other independently validated NLI model is not claimed by the source package.

## Source Note

This document is a professional editorial extraction from the Judge Module section of the user-supplied acquisition package.

## References

[1]: ../source/EXTRACTED_SOURCE.txt "Extracted source text from the supplied PDF"
[2]: ../source/CognitiveCore_Acquisition_Package.pdf "Original user-supplied acquisition package"

**Prepared by Manus AI**
