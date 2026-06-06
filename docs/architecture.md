# Architecture notes

This is intentionally a small project.

It borrows the shape of hexagonal architecture, but it does not try to become a framework example.

## Layers

- `domain` — business concepts, rules, domain interfaces.
- `application` — use cases that orchestrate domain objects.
- `adapter` — in-memory and system implementations.

## Why interfaces are in domain

The repository and other domain-facing contracts are in `domain` because the domain defines what it needs.

There is no `ports` package here on purpose.

The package name should not be more important than the language of the domain.

## Main point

`context parameters` are not used to hide dependencies.

They are used to model the execution context:

- current user,
- transaction,
- current time,
- domain event publisher.
