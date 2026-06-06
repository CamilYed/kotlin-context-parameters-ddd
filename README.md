# Kotlin Context Parameters DDD Example

Small Kotlin project used as a companion repository for an article about `context parameters`.

The goal is not to build a web application. The goal is to show where `context parameters` fit in a small DDD / hexagonal architecture example.

Article link: _to be added_

## Why no Spring?

Spring would be useful if the example needed HTTP endpoints, dependency injection, configuration, profiles, or infrastructure integration.

Here it would mostly add noise.

This repository focuses on one question:

> When is a dependency a constructor dependency, when is it a regular function argument, and when is it a context of execution?

That question is easier to see in a plain Kotlin project.

## Tech stack

- Kotlin `2.4.0`
- JDK `25`
- Gradle `9.5.1`
- JUnit `6.1.0`
- No Spring
- No persistence framework
- In-memory adapters only

## Architecture

The project uses a small hexagonal architecture shape:

```text
src/main/kotlin/io/github/camilyed/contextparameters
├── domain
│   ├── Order.kt
│   ├── OrderRepository.kt
│   ├── ClockProvider.kt
│   ├── DomainEvents.kt
│   ├── Transaction.kt
│   └── UserContext.kt
├── application
│   └── ConfirmOrderUseCase.kt
└── adapter
    ├── inmemory
    │   ├── InMemoryOrderRepository.kt
    │   └── InMemoryTransaction.kt
    ├── recording
    │   └── RecordingDomainEvents.kt
    └── system
        └── SystemClockProvider.kt
```

There is intentionally no `ports` package.

The interfaces are part of the domain package, for example:

```kotlin
interface OrderRepository {
    context(tx: Transaction)
    fun get(id: OrderId): Order

    context(tx: Transaction)
    fun save(order: Order)
}
```

This keeps the language close to the domain instead of turning the package structure into an architecture diagram.

## Main idea

The domain operation needs a few things from the execution environment:

- current time,
- current user,
- event publisher.

With regular parameters, the method would look like this:

```kotlin
fun confirm(
    clock: ClockProvider,
    events: DomainEvents,
    user: UserContext,
) {
    // ...
}
```

With `context parameters`, the operation says something more precise:

```kotlin
context(clock: ClockProvider, events: DomainEvents, user: UserContext)
fun confirm() {
    // ...
}
```

This reads as:

> I can confirm an order, but only inside a context that provides time, events, and current user.

If that context is missing at the call site, the code does not compile.

## Example use case

```kotlin
class ConfirmOrderUseCase(
    private val orders: OrderRepository,
) {
    context(
        clock: ClockProvider,
        events: DomainEvents,
        user: UserContext,
        tx: Transaction,
    )
    fun confirm(command: ConfirmOrderCommand): OrderSnapshot {
        val order = orders.get(command.orderId)

        order.confirm()
        orders.save(order)

        return order.toSnapshot()
    }
}
```

The repository is injected through the constructor because it defines the use case.

The clock, events, current user, and transaction are context parameters because they describe the environment of this operation.

## Running locally

Generate the Gradle wrapper first:

```bash
gradle wrapper --gradle-version 9.5.1
```

Then run tests:

```bash
./gradlew test
```

Run the demo:

```bash
./gradlew run
```

## Tests

The tests are intentionally written without Spring and without mocks.

```kotlin
val snapshot = context(clock, events, user, tx) {
    useCase.confirm(ConfirmOrderCommand(orderId))
}
```

The context is explicit in the test setup, but it does not pollute the business operation itself.

## Rule of thumb

Use regular parameters for business input:

```kotlin
fun confirmOrder(orderId: OrderId)
```

Use constructor injection for stable object dependencies:

```kotlin
class ConfirmOrderUseCase(
    private val orders: OrderRepository,
)
```

Use context parameters for execution context:

```kotlin
context(user: UserContext, tx: Transaction)
fun confirmOrder(orderId: OrderId)
```

## Repository setup

Recommended repository name:

```text
kotlin-context-parameters-ddd
```

Create and push:

```bash
gh repo create CamilYed/kotlin-context-parameters-ddd   --public   --description "Kotlin context parameters in a small DDD / hexagonal architecture example"   --source=.   --remote=origin   --push
```

## License

MIT
