# Kotlin Context Parameters DDD Example

Small Kotlin/JVM project showing `context parameters` in a DDD-style codebase.

The example is intentionally small. There is no Spring here, because the goal is not to show framework integration.
The goal is to show where `context parameters` can make domain and application code more explicit without turning them into a replacement for dependency injection.

## Domain

The domain contains one aggregate: `Order`.

The aggregate has a very small lifecycle:

```text
Draft -> Confirmed
```

There is no enum for the order state. The state is modeled as a sealed type:

```kotlin
sealed interface OrderState {
    data object Draft : OrderState

    data class Confirmed(
        val confirmedAt: Instant,
    ) : OrderState
}
```

This avoids the common `status + nullable confirmedAt` shape.

Instead of this:

```kotlin
status = Confirmed
confirmedAt = null // impossible state
```

the model says:

```kotlin
OrderState.Draft
OrderState.Confirmed(confirmedAt = now)
```

A confirmed order always has confirmation time, because the timestamp is part of the `Confirmed` state.

## Aggregate rule

The aggregate protects this rule:

```text
An order can be confirmed only when:
- it is still a draft,
- the current user is the owner or can confirm orders,
- confirmation stores the current time,
- confirmation publishes an OrderConfirmed domain event.
```

The important part is the operation signature:

```kotlin
context(clock: ClockProvider, events: DomainEvents, user: UserContext)
fun confirm()
```

`Order.confirm()` does not take `clock`, `events` and `user` as normal parameters.
It also does not hide them behind a global service locator.

It says:

> this operation requires this execution context.

If the context is not available at the call site, the code does not compile.

## Application layer

The application layer adds transaction handling:

```kotlin
context(
    clock: ClockProvider,
    events: DomainEvents,
    user: UserContext,
    transaction: Transaction,
)
fun confirm(command: ConfirmOrderCommand): OrderSnapshot =
    transaction.within {
        val order = orderRepository.findById(command.orderId)

        order.confirm()

        orderRepository.save(order)

        order.toSnapshot()
    }
```

Constructor injection is still used where it makes sense:

```kotlin
class ConfirmOrderUseCase(
    private val orderRepository: OrderRepository,
)
```

The repository is a stable dependency of the use case.
The clock, current user, domain events and transaction are execution context.

## Package structure

```text
src/main/kotlin/io/github/camilyed/contextparameters
├── domain
├── application
└── adapter
```

Interfaces such as `OrderRepository`, `ClockProvider`, `DomainEvents` and `Transaction` are in `domain`.
There is no `ports` package.

## Tests

Tests follow a simple Given-When-Then style.

Test support is split into:

```text
testing/
├── ability
├── assertions
├── builders
└── fakes
```

There is no large scenario DSL object.
The tests use small abilities, builders, fakes and custom Strikt assertions.

## Requirements

- JDK 25
- Gradle 9.5.1
- Kotlin 2.4.0
- JUnit 6.1.0

## First run

Generate the official Gradle Wrapper locally:

```bash
sdk install gradle 9.5.1
sdk use gradle 9.5.1

gradle wrapper --gradle-version=9.5.1 --distribution-type=bin
```

Then use the wrapper:

```bash
./gradlew clean test
./gradlew run
```

## Article

Article link will be added later.

```text
https://camilyed.github.io/pl/kotlin-context-parameters-nie-di-tylko-kontekst-wykonania/
```
