# Kotlin Context Parameters DDD Example

Polski | [English](README.md)

Mały projekt Kotlin/JVM pokazujący `context parameters` na przykładzie kodu w stylu DDD / hexagonal architecture.

Projekt jest celowo mały. Nie ma tutaj Springa ani kontenera DI. Chodzi o pokazanie, gdzie `context parameters` pasują, gdy kod ma już wyraźną domenę, warstwę aplikacji i adaptery.

## Co pokazuje to repo

Przykład odpowiada na praktyczne pytanie:

> jak use case i agregat mogą opisać kontekst wykonania bez wrzucania wszystkiego w konstruktor i bez chowania zależności w service locatorze?

W repo używam:

- Kotlin 2.4.0,
- JDK 25,
- Gradle 9.5.1,
- JUnit 6,
- Strikt do asercji,
- małego agregatu `Order`,
- adapterów in-memory do testów i demo.

## Struktura pakietów

```text
src/main/kotlin/io/github/camilyed/contextparameters
├── domain
├── application
└── adapter
```

Kierunek zależności jest prosty:

```text
application -> domain
adapter     -> domain
```

Interfejsy takie jak `OrderRepository`, `ClockProvider`, `DomainEvents` i `Transaction` są w `domain`. Nie ma osobnego pakietu `ports`.

## Domena

Domena ma jeden agregat: `Order`.

Agregat pilnuje tej reguły:

```text
Zamówienie można potwierdzić tylko wtedy, gdy:
- nadal jest szkicem,
- aktualny użytkownik jest właścicielem albo może potwierdzać zamówienia,
- potwierdzenie zapisuje aktualny czas,
- potwierdzenie publikuje event domenowy OrderConfirmed.
```

Stan jest reprezentowany przez sealed type:

```kotlin
sealed interface OrderState {

    data object Draft : OrderState

    data class Confirmed(
        val confirmedAt: Instant,
    ) : OrderState
}
```

Operacja agregatu jawnie deklaruje kontekst wykonania:

```kotlin
context(clock: ClockProvider, events: DomainEvents, user: UserContext)
fun confirm()
```

Agregat nie tworzy zegara, nie wie skąd pochodzi aktualny użytkownik i nie wie jak dostarczane są eventy.

Mówi tylko:

> żeby potwierdzić zamówienie, taki kontekst musi być dostępny.

## Warstwa aplikacji

Use case ma jedną zależność w konstruktorze:

```kotlin
class ConfirmOrderUseCase(
    private val orderRepository: OrderRepository,
)
```

Repozytorium jest stałą zależnością use case'a.

Sama operacja potrzebuje kontekstu wykonania:

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

Dzięki temu podział jest czytelny:

```text
constructor injection -> stała zależność obiektu
zwykły parametr       -> dane wejściowe operacji
context parameter     -> kontekst wykonania
```

## Adaptery

Adaptery dostarczają konkretne implementacje abstrakcji z domeny.

Przykład:

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

Adapter zależy od interfejsu domenowego. Domena nie zależy od adaptera.

## Testy

Testy są pisane w stylu Given-When-Then.

Wsparcie testów jest podzielone na małe elementy:

```text
src/test/kotlin/io/github/camilyed/contextparameters/testing
├── ability
├── assertions
├── builders
└── fakes
```

Przykład:

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

Test nie odpala frameworka. Tworzy mały kontekst i sprawdza zachowanie domeny.

## Wymagania

- JDK 25
- Gradle 9.5.1
- Kotlin 2.4.0

## Uruchomienie lokalnie

```bash
./gradlew clean test
./gradlew run
```

Jeżeli brakuje wrappera, wygeneruj go raz:

```bash
sdk install gradle 9.5.1
sdk use gradle 9.5.1
gradle wrapper --gradle-version=9.5.1 --distribution-type=bin
```

Potem używaj już tylko:

```bash
./gradlew clean test
```

## Artykuł

Powiązany artykuł:

```text
https://camilyed.github.io/pl/kotlin-context-parameters-nie-di-tylko-kontekst-wykonania/
```
