# Kotlin Context Parameters DDD Example

Mały projekt Kotlin/JVM pokazujący `context parameters` w kodzie w stylu DDD.

Przykład jest celowo mały. Nie ma tutaj Springa, bo celem nie jest integracja z frameworkiem.
Celem jest pokazanie, gdzie `context parameters` mogą poprawić czytelność domeny i warstwy aplikacji, bez udawania, że zastępują dependency injection.

## Domena

Domena ma jeden agregat: `Order`.

Agregat ma bardzo prosty cykl życia:

```text
Draft -> Confirmed
```

Nie ma enuma dla statusu zamówienia. Stan jest zamodelowany jako sealed type:

```kotlin
sealed interface OrderState {
    data object Draft : OrderState

    data class Confirmed(
        val confirmedAt: Instant,
    ) : OrderState
}
```

Dzięki temu unikamy klasycznego układu `status + nullable confirmedAt`.

Czyli zamiast takiego modelu:

```kotlin
status = Confirmed
confirmedAt = null // stan, który nie powinien istnieć
```

mamy:

```kotlin
OrderState.Draft
OrderState.Confirmed(confirmedAt = now)
```

Potwierdzone zamówienie zawsze ma czas potwierdzenia, bo ten czas jest częścią stanu `Confirmed`.

## Reguła agregatu

Agregat pilnuje tej reguły:

```text
Zamówienie można potwierdzić tylko wtedy, gdy:
- nadal jest szkicem,
- aktualny użytkownik jest właścicielem albo może potwierdzać zamówienia,
- potwierdzenie zapisuje aktualny czas,
- potwierdzenie publikuje event domenowy OrderConfirmed.
```

Najważniejsza jest sygnatura operacji:

```kotlin
context(clock: ClockProvider, events: DomainEvents, user: UserContext)
fun confirm()
```

`Order.confirm()` nie przyjmuje `clock`, `events` i `user` jako zwykłych parametrów.
Nie chowa ich też za globalnym service locatorem.

Ta metoda mówi:

> ta operacja wymaga takiego kontekstu wykonania.

Jeśli kontekstu nie ma w miejscu wywołania, kod się nie skompiluje.

## Warstwa aplikacji

Warstwa aplikacji dokłada transakcję:

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

Constructor injection nadal zostaje tam, gdzie ma sens:

```kotlin
class ConfirmOrderUseCase(
    private val orderRepository: OrderRepository,
)
```

Repozytorium jest stałą zależnością use case'a.
Zegar, aktualny użytkownik, eventy domenowe i transakcja są kontekstem wykonania.

## Struktura pakietów

```text
src/main/kotlin/io/github/camilyed/contextparameters
├── domain
├── application
└── adapter
```

Interfejsy takie jak `OrderRepository`, `ClockProvider`, `DomainEvents` i `Transaction` są w `domain`.
Nie ma pakietu `ports`.

## Testy

Testy są pisane prosto, w stylu Given-When-Then.

Wsparcie testów jest podzielone na:

```text
testing/
├── ability
├── assertions
├── builders
└── fakes
```

Nie ma dużego obiektu typu scenario DSL.
Są małe abilities, buildery, fake'i i custom assertions w Strikt.

## Wymagania

- JDK 25
- Gradle 9.5.1
- Kotlin 2.4.0
- JUnit 6.1.0

## Pierwsze uruchomienie

Wygeneruj lokalnie oficjalny Gradle Wrapper:

```bash
sdk install gradle 9.5.1
sdk use gradle 9.5.1

gradle wrapper --gradle-version=9.5.1 --distribution-type=bin
```

Potem używaj wrappera:

```bash
./gradlew clean test
./gradlew run
```

## Artykuł

Link do artykułu dodamy później.

```text
https://camilyed.github.io/pl/kotlin-context-parameters-nie-di-tylko-kontekst-wykonania/
```
