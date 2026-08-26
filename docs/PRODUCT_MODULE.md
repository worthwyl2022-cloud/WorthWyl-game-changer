# Product Module

## Purpose

The Product Module defines the minimal Creative OS boundary for a studio, writer, or creative team. It turns the substrate’s governance primitives into a project-oriented surface with explicit constitution, canon, quarantine, and provenance controls.

## API Surface

| API surface | Purpose |
|---|---|
| `createProject` | Creates an isolated workspace with its own substrate key. |
| `addPrinciple` / `constitution` | Adds and reads Creative Constitution principles. |
| `addCanonFact` | Adds immutable facts to the canon-first lane. |
| `quarantineInbox` | Lists generated material awaiting human review. |
| `approve` / `reject` | Promotes or discards quarantined material through an explicit human gate. |
| `auditLog` | Exposes governance and provenance events for review. |
| `exportProject` | Produces a diligence-oriented project export. |

## Project Isolation

Each project owns its constitution, canon, quarantine, and provenance. A separate `SubstrateCore` or resonance field should be associated with each `substrateKey` so that memory does not cross project boundaries.

This boundary is an architectural design in the supplied package, not a claim of production-scale multi-tenancy. The default implementation is described as single-process and in-memory.

## Persistence Boundary

`InMemoryProjectRepository` is the default repository. A production implementation should provide a durable `ProjectRepository` backed by a suitable persistence layer such as Room, files, or PostgreSQL, with explicit migration, backup, access-control, and audit-retention policies.

## UI Mapping

| User experience | Supporting operation |
|---|---|
| Constitution editor | `addPrinciple` and `constitution` |
| Corpus ingestion | `addCanonFact` and substrate injection |
| Quarantine inbox | `quarantineInbox`, `approve`, and `reject` |
| “Why was this blocked?” | Filter `auditLog` for protection-block events |
| Diligence export | `exportProject` with provenance and governance history |

## Governance Principle

Generated material should not silently become durable memory. It remains quarantined until a human decision and evaluation gate establish that it can be promoted to episodic memory. Locked identity is not created through ordinary generation approval.

## Implementation Notes

The product surface should remain thin. Its purpose is to expose and preserve the substrate’s behavioral contract rather than to hide governance decisions behind an opaque dashboard. All project boundaries, approvals, rejections, incident resolutions, and write-back events should remain inspectable through the audit surface.

## Source Note

This document is an editorial extraction from the Product Module section of the user-supplied acquisition package. It is intentionally implementation-neutral where the source package describes a future persistence or production-hardening step.

## References

[1]: ../source/EXTRACTED_SOURCE.txt "Extracted source text from the supplied PDF"
[2]: ../source/CognitiveCore_Acquisition_Package.pdf "Original user-supplied acquisition package"

**Prepared by Manus AI**
