# Security Policy

## Reporting a vulnerability

Do not open a public issue for a suspected vulnerability or exposed credential. Contact the repository owner through a private GitHub channel and include the affected path, impact, reproduction steps, and any proposed mitigation.

## Credential handling

Credentials, private keys, tokens, customer data, and confidential configuration must not be committed. If a credential is exposed, revoke or rotate it immediately and then document the incident separately; removing the file alone does not remove it from Git history.

## Handover note

Security review for acquisition or IP transfer must include repository history, GitHub Actions, deploy keys, environments, package registries, and external services.
