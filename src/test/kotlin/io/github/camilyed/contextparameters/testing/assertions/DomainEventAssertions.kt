package io.github.camilyed.contextparameters.testing.assertions

import io.github.camilyed.contextparameters.domain.OrderConfirmed
import io.github.camilyed.contextparameters.testing.fakes.TestingDomainEvents
import strikt.api.Assertion
import java.time.Instant

fun Assertion.Builder<TestingDomainEvents>.hasPublishedOrderConfirmed(
    orderId: String,
    confirmedAt: String,
): Assertion.Builder<TestingDomainEvents> {
    val expectedConfirmedAt = Instant.parse(confirmedAt)

    return assertThat("has published OrderConfirmed event") {
        it.publishedEvents()
            .filterIsInstance<OrderConfirmed>()
            .singleOrNull()
            ?.let { event ->
                event.orderId.value == orderId &&
                    event.confirmedAt == expectedConfirmedAt
            }
            ?: false
    }
}

fun Assertion.Builder<TestingDomainEvents>.hasNoPublishedEvents(): Assertion.Builder<TestingDomainEvents> =
    assertThat("has no published events") {
        it.publishedEvents().isEmpty()
    }
