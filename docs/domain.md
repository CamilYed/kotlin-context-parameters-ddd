# Domain notes

The domain example is intentionally small.

## Aggregate

`Order` is the aggregate root.

It owns:

- `OrderId`
- `ownerId`
- `OrderState`

The state is modeled with a sealed interface:

```kotlin
sealed interface OrderState {
    data object Draft : OrderState
    data class Confirmed(val confirmedAt: Instant) : OrderState
}
```

There is no enum and no nullable `confirmedAt` in the snapshot.

## Behavior

The main behavior is:

```kotlin
context(clock: ClockProvider, events: DomainEvents, user: UserContext)
fun confirm()
```

This operation needs an execution context:

- `ClockProvider` gives current time.
- `DomainEvents` publishes `OrderConfirmed`.
- `UserContext` tells who performs the action.

The aggregate does not know how those objects are created.
It only declares that they must exist when the operation is called.

## Application context

`ConfirmOrderUseCase` adds `Transaction` to the context because transaction handling belongs to application flow, not to the aggregate itself.
