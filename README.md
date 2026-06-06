# Kotlin Context Parameters DDD Example

[Polski](README.pl.md) | English

Small Kotlin/JVM repository showing `context parameters` in a DDD / hexagonal architecture example.

The project is intentionally small. There is no Spring and no DI container here. The point is to show where `context parameters` fit when the code already has clear domain, application and adapter boundaries.

## What this repository shows

The example answers one practical question:

> how can a use case and an aggregate express execution context without turning it into constructor dependency or hiding it behind a service locator?

The code uses:

- Kotlin 2.4.0,
- JDK 25,
- Gradle 9.5.1,
- JUnit 6,
- Strikt for assertions,
- a small DDD-style `Order` aggregate,
- in-memory adapters for tests and demo.

## Package structure

```text
src/main/kotlin/io/github/camilyed/contextparameters
├── domain
├── application
└── adapter
```

The direction is simple:

```text
application -> domain
adapter     -> domain
```

Interfaces such as `OrderRepository`, `ClockProvider`, `DomainEvents` and `Transaction` live in `domain`. There is no separate `ports` package.

## Domain

The domain contains one aggregate: `Order`.

The aggregate protects this rule:

```text
An order can be confirmed only when:
- it is still a draft,
- the current user is the owner or can confirm orders,
- confirmation stores the current time,
- confirmation publishes an OrderConfirmed domain event.
```

State is represented by a sealed type:

```kotlin
sealed interface OrderState {

    data object Draft : OrderState

    data class Confirmed(
        val confirmedAt: Instant,
    ) : OrderState
}
```

The aggregate operation declares its execution context explicitly:

```kotlin
context(clock: ClockProvider, events: DomainEvents, user: UserContext)
fun confirm()
```

The aggregate does not create a clock, does not know where the current user comes from and does not know how events are delivered.

It only says:

> to confirm an order, this context must be available.

## Application layer

The use case has one constructor dependency:

```kotlin
class ConfirmOrderUseCase(
    private val orderRepository: OrderRepository,
)
```

The repository is a stable dependency of the use case.

The operation itself needs execution context:

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

This gives a clear split:

```text
constructor injection -> stable dependency of the object
normal parameter      -> operation input
context parameter     -> execution context
```

## Adapters

Adapters provide concrete implementations for domain abstractions.

Example:

```kotlin
class InMemoryOrderRepository : OrderRepository {

    private val orders = mutableMapOf<OrderId, Order>()

    override fun findById(id: OrderId): Order =
        orders[id]
            ?: throw OrderNotFoundException("Order with id ${id.value} not found")

    override fun save(order: Order) {
        orders[order.id] = order
    }
}
```

The adapter depends on the domain interface. The domain does not depend on the adapter.

## Tests

Tests are written in a Given-When-Then style.

Test support is split into small pieces:

```text
src/test/kotlin/io/github/camilyed/contextparameters/testing
├── ability
├── assertions
├── builders
└── fakes
```

Example:

```kotlin
@Test
fun `should confirm draft order by owner`() {
    // given
    val order =
        Order.fromSnapshot(
            anOrder()
                .withId("ORD-123")
                .ownedBy("user-123")
                .build(),
        )

    // and
    currentUserIs("user-123")

    // and
    currentTimeIs("2026-06-05T10:15:30Z")

    // when
    context(clock, domainEvents, currentUser) {
        order.confirm()
    }

    // then
    expectThat(order.toSnapshot())
        .isConfirmed()
        .wasConfirmedAt("2026-06-05T10:15:30Z")

    // and
    expectThat(domainEvents)
        .hasPublishedOrderConfirmed(
            orderId = "ORD-123",
            confirmedAt = "2026-06-05T10:15:30Z",
        )
}
```

The test does not start a framework. It creates a small context and verifies domain behavior.

## Requirements

- JDK 25
- Gradle 9.5.1
- Kotlin 2.4.0

## Running locally

```bash
./gradlew clean test
./gradlew run
```

If the wrapper is missing, generate it once:

```bash
sdk install gradle 9.5.1
sdk use gradle 9.5.1
gradle wrapper --gradle-version=9.5.1 --distribution-type=bin
```

Then use only:

```bash
./gradlew clean test
```

## Article

Related article:

```text
https://camilyed.github.io/en/kotlin-context-parameters-not-di-but-execution-context/
```
