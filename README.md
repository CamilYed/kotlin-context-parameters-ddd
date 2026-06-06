# Kotlin Context Parameters in a DDD Application

[Polski](README.pl.md) | English

A practical Kotlin 2.4 example showing how **context parameters** can model execution context in a DDD / hexagonal architecture application.

This repository accompanies the article:

👉 https://camilyed.github.io/en/kotlin-context-parameters-not-di-but-execution-context/

## Why this repository?

Most examples of context parameters focus on syntax.

This repository focuses on a design question:

> Where do context parameters fit in a real application?

The example uses a small Order aggregate and shows how to combine:

- context parameters,
- DDD,
- hexagonal architecture,
- application use cases,
- Given-When-Then tests.

## Mental model

```text
constructor injection -> stable object dependencies
normal parameters     -> operation input
context parameters    -> execution context
```

Examples of execution context:

- current user
- clock
- domain events
- transaction
- tenant context

## Architecture

```text
adapter
   |
   v
application
   |
   v
domain
```

Package structure:

```text
src/main/kotlin/io/github/camilyed/contextparameters
├── domain
├── application
└── adapter
```

## Example

```kotlin
context(
    clock: ClockProvider,
    events: DomainEvents,
    user: UserContext,
)
fun confirm()
```

```kotlin
context(
    clock: ClockProvider,
    events: DomainEvents,
    user: UserContext,
    transaction: Transaction,
)
fun confirm(command: ConfirmOrderCommand): OrderSnapshot
```

## Domain

The domain contains a single aggregate: Order.

Business rule:

- draft orders can be confirmed
- current user must be owner or have permission
- confirmation stores current time
- confirmation publishes a domain event

## Testing

- JUnit 6
- Strikt
- Given-When-Then
- builders
- fakes
- custom assertions

No Spring. No DI container.

## Requirements

- JDK 25
- Kotlin 2.4.0
- Gradle 9.5.1

## Running

```bash
./gradlew clean test
./gradlew run
```
