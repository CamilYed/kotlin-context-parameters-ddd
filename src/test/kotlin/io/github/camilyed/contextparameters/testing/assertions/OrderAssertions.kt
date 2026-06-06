package io.github.camilyed.contextparameters.testing.assertions

import io.github.camilyed.contextparameters.domain.OrderSnapshot
import io.github.camilyed.contextparameters.domain.OrderState
import strikt.api.Assertion
import java.time.Instant

fun Assertion.Builder<OrderSnapshot>.hasId(expectedId: String): Assertion.Builder<OrderSnapshot> =
    assertThat("has id $expectedId") {
        it.id.value == expectedId
    }

fun Assertion.Builder<OrderSnapshot>.hasOwner(expectedOwnerId: String): Assertion.Builder<OrderSnapshot> =
    assertThat("has owner $expectedOwnerId") {
        it.ownerId.value == expectedOwnerId
    }

fun Assertion.Builder<OrderSnapshot>.isDraft(): Assertion.Builder<OrderSnapshot> =
    assertThat("is draft") {
        it.state == OrderState.Draft
    }

fun Assertion.Builder<OrderSnapshot>.isConfirmed(): Assertion.Builder<OrderSnapshot> =
    assertThat("is confirmed") {
        it.state is OrderState.Confirmed
    }

fun Assertion.Builder<OrderSnapshot>.wasConfirmedAt(
    expectedConfirmedAt: String,
): Assertion.Builder<OrderSnapshot> {
    val expected = Instant.parse(expectedConfirmedAt)

    return assertThat("was confirmed at $expectedConfirmedAt") {
        val state = it.state

        state is OrderState.Confirmed && state.confirmedAt == expected
    }
}
