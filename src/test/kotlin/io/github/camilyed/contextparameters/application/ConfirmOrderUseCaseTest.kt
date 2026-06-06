package io.github.camilyed.contextparameters.application

import io.github.camilyed.contextparameters.domain.OrderNotFoundException
import io.github.camilyed.contextparameters.testing.BaseUnitTest
import io.github.camilyed.contextparameters.testing.assertions.hasOwner
import io.github.camilyed.contextparameters.testing.assertions.hasPublishedOrderConfirmed
import io.github.camilyed.contextparameters.testing.assertions.isConfirmed
import io.github.camilyed.contextparameters.testing.assertions.wasCommitted
import io.github.camilyed.contextparameters.testing.assertions.wasConfirmedAt
import io.github.camilyed.contextparameters.testing.builders.ConfirmOrderCommandBuilder
import io.github.camilyed.contextparameters.testing.builders.ConfirmOrderCommandBuilder.Companion.aConfirmOrderCommand
import io.github.camilyed.contextparameters.testing.builders.OrderSnapshotBuilder.Companion.anOrder
import org.junit.jupiter.api.Test
import strikt.api.expectCatching
import strikt.api.expectThat
import strikt.assertions.isA
import strikt.assertions.isEqualTo
import strikt.assertions.isFailure
import strikt.assertions.message

class ConfirmOrderUseCaseTest : BaseUnitTest() {

    private val confirmOrderUseCase =
        ConfirmOrderUseCase(orderRepository)

    @Test
    fun `should confirm existing draft order`() {
        // given
        thereIsAnOrder(
            anOrder()
                .withId("ORD-123")
                .ownedBy("user-123"),
        )

        // and
        currentUserIs("user-123")

        // and
        currentTimeIs("2026-06-05T10:15:30Z")

        // when
        val order =
            confirm(
                aConfirmOrderCommand()
                    .withOrderId("ORD-123"),
            )

        // then
        expectThat(order)
            .hasOwner("user-123")
            .isConfirmed()
            .wasConfirmedAt("2026-06-05T10:15:30Z")

        // and
        expectThat(domainEvents)
            .hasPublishedOrderConfirmed(
                orderId = "ORD-123",
                confirmedAt = "2026-06-05T10:15:30Z",
            )

        // and
        expectThat(transaction)
            .wasCommitted()
    }

    @Test
    fun `should confirm order when current user can confirm orders`() {
        // given
        thereIsAnOrder(
            anOrder()
                .withId("ORD-123")
                .ownedBy("owner-123"),
        )

        // and
        currentUserCanConfirmOrders("admin-123")

        // when
        val order =
            confirm(
                aConfirmOrderCommand()
                    .withOrderId("ORD-123"),
            )

        // then
        expectThat(order)
            .hasOwner("owner-123")
            .isConfirmed()
            .wasConfirmedAt("2026-06-05T10:15:30Z")

        // and
        expectThat(domainEvents)
            .hasPublishedOrderConfirmed(
                orderId = "ORD-123",
                confirmedAt = "2026-06-05T10:15:30Z",
            )
    }

    @Test
    fun `should throw exception when order does not exist`() {
        // when - then
        expectCatching {
            confirm(
                aConfirmOrderCommand()
                    .withOrderId("ORD-404"),
            )
        }.isFailure()
            .isA<OrderNotFoundException>()
            .message.isEqualTo("Order with id ORD-404 not found")
    }

    @Test
    fun `should not confirm order when current user is not owner and cannot confirm orders`() {
        // given
        thereIsAnOrder(
            anOrder()
                .withId("ORD-123")
                .ownedBy("owner-123"),
        )

        // and
        currentUserIs("user-456")

        // when - then
        expectCatching {
            confirm(
                aConfirmOrderCommand()
                    .withOrderId("ORD-123"),
            )
        }.isFailure()
            .isA<IllegalArgumentException>()
            .message.isEqualTo("User cannot confirm this order")
    }

    private fun confirm(command: ConfirmOrderCommandBuilder) =
        context(clock, domainEvents, currentUser, transaction) {
            confirmOrderUseCase.confirm(command.build())
        }
}
