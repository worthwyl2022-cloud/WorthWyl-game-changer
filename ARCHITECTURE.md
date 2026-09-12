# Architecture and Design Record

## Repository role

**Product, integration, or supporting component repository.**

This document records engineering evidence for technical diligence. It does not establish legal ownership, inventorship, assignment, trademark rights, or third-party license compliance.

## System boundary

Describe what this repository owns and what it consumes from other repositories, services, runtimes, registries, or external providers. Identify whether this repository is canonical, supporting, demonstrative, or historical.

## Core concepts and invariants

Record the concepts that must remain stable for the architecture to retain its intended behavior. State the authority, identity, state-transition, governance, safety, and compatibility rules that are enforced by code or tests. Do not describe an invariant as enforced unless a code path or test demonstrates it.

## Data and control flow

Document the primary inputs, transformations, state transitions, outputs, and failure paths. Link to the implementation and tests that demonstrate each critical path.

## Extension points

Record supported interfaces, adapters, providers, schemas, contracts, and configuration points. Distinguish stable interfaces from experimental or internal ones.

## Canonicality decision

Canonical status: **Supporting surface; cranium-kernel is the sole canonical authority source**. This repository must not define a competing authority implementation.
