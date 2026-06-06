# Kotlin Context Parameters — przykład DDD

Mały projekt w Kotlinie jako repozytorium do artykułu o `context parameters`.

Celem nie jest zbudowanie aplikacji webowej. Celem jest pokazanie, gdzie `context parameters` pasują w małym przykładzie DDD / architektury heksagonalnej.

Link do artykułu: _do uzupełnienia_

## Dlaczego bez Springa?

Spring miałby sens, gdyby przykład pokazywał HTTP, dependency injection, konfigurację, profile albo integrację z infrastrukturą.

Tutaj Spring głównie zasłoniłby temat.

Ten projekt odpowiada na jedno pytanie:

> Kiedy zależność powinna być w konstruktorze, kiedy powinna być zwykłym argumentem funkcji, a kiedy jest kontekstem wykonania?

To pytanie dużo łatwiej zobaczyć w czystym projekcie Kotlinowym.

## Stack

- Kotlin `2.4.0`
- JDK `25`
- Gradle `9.5.1`
- JUnit `6.1.0`
- bez Springa
- bez frameworka persistence
- tylko adaptery in-memory

## Architektura

Projekt ma mały kształt architektury heksagonalnej:

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

Celowo nie ma pakietu `ports`.

Interfejsy są w pakiecie `domain`, na przykład:

```kotlin
interface OrderRepository {
    context(tx: Transaction)
    fun get(id: OrderId): Order

    context(tx: Transaction)
    fun save(order: Order)
}
```

Dzięki temu język kodu zostaje blisko domeny, a pakiety nie udają prezentacji o architekturze.

## Główna myśl

Operacja domenowa potrzebuje kilku rzeczy z otoczenia:

- aktualnego czasu,
- aktualnego użytkownika,
- publishera eventów.

Jako zwykłe parametry wyglądałoby to tak:

```kotlin
fun confirm(
    clock: ClockProvider,
    events: DomainEvents,
    user: UserContext,
) {
    // ...
}
```

Z `context parameters` metoda mówi coś bardziej precyzyjnego:

```kotlin
context(clock: ClockProvider, events: DomainEvents, user: UserContext)
fun confirm() {
    // ...
}
```

Czyli:

> Potrafię potwierdzić zamówienie, ale tylko w kontekście, który dostarcza czas, eventy i aktualnego użytkownika.

Jeżeli tego kontekstu nie ma w miejscu wywołania, kod się nie skompiluje.

## Przykład use case’a

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

Repozytorium jest w konstruktorze, bo definiuje use case.

Zegar, eventy, aktualny użytkownik i transakcja są context parameters, bo opisują środowisko wykonania tej operacji.

## Uruchomienie lokalnie

Najpierw wygeneruj Gradle Wrapper:

```bash
gradle wrapper --gradle-version 9.5.1
```

Potem uruchom testy:

```bash
./gradlew test
```

Uruchom demo:

```bash
./gradlew run
```

## Testy

Testy są celowo bez Springa i bez mocków.

```kotlin
val snapshot = context(clock, events, user, tx) {
    useCase.confirm(ConfirmOrderCommand(orderId))
}
```

Kontekst jest jawny w przygotowaniu testu, ale nie zaśmieca samej operacji biznesowej.

## Prosta zasada

Zwykłe parametry dla danych wejściowych:

```kotlin
fun confirmOrder(orderId: OrderId)
```

Constructor injection dla stałych zależności obiektu:

```kotlin
class ConfirmOrderUseCase(
    private val orders: OrderRepository,
)
```

Context parameters dla kontekstu wykonania:

```kotlin
context(user: UserContext, tx: Transaction)
fun confirmOrder(orderId: OrderId)
```

## Założenie repozytorium

Proponowana nazwa repo:

```text
kotlin-context-parameters-ddd
```

Utworzenie i push:

```bash
gh repo create CamilYed/kotlin-context-parameters-ddd   --public   --description "Kotlin context parameters in a small DDD / hexagonal architecture example"   --source=.   --remote=origin   --push
```

## Licencja

MIT
