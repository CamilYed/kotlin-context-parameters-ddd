# Kotlin Context Parameters w aplikacji DDD

Polski | [English](README.md)

Praktyczny przykład wykorzystania **context parameters** w Kotlinie 2.4 na bazie małej aplikacji napisanej w stylu DDD i hexagonal architecture.

Repozytorium jest dodatkiem do artykułu:

👉 https://camilyed.github.io/pl/kotlin-context-parameters-nie-di-tylko-kontekst-wykonania/

## Po co powstało to repo?

Większość przykładów pokazuje samą składnię.

Tutaj interesowało mnie inne pytanie:

> Gdzie context parameters pasują w prawdziwej aplikacji?

Przykład pokazuje połączenie:

- context parameters
- DDD
- hexagonal architecture
- warstwy aplikacji
- testy Given-When-Then

## Model mentalny

```text
constructor injection -> stałe zależności obiektu
zwykłe parametry      -> dane wejściowe operacji
context parameters    -> kontekst wykonania
```

Przykłady kontekstu wykonania:

- aktualny użytkownik
- zegar
- eventy domenowe
- transakcja
- tenant context

## Architektura

```text
adapter
   |
   v
application
   |
   v
domain
```

Struktura:

```text
src/main/kotlin/io/github/camilyed/contextparameters
├── domain
├── application
└── adapter
```

## Przykład

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

## Domena

Przykład zawiera jeden agregat: Order.

Reguła biznesowa:

- szkic można potwierdzić
- użytkownik musi być właścicielem lub mieć uprawnienia
- zapisujemy czas potwierdzenia
- publikujemy event domenowy

## Testy

- JUnit 6
- Strikt
- Given-When-Then
- buildery
- fake'i
- własne asercje

Bez Springa. Bez kontenera DI.

## Wymagania

- JDK 25
- Kotlin 2.4.0
- Gradle 9.5.1

## Uruchomienie

```bash
./gradlew clean test
./gradlew run
```
