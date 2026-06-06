package io.github.camilyed.contextparameters.domain

import io.github.camilyed.contextparameters.testing.BaseUnitTest
import io.github.camilyed.contextparameters.testing.assertions.hasId
import io.github.camilyed.contextparameters.testing.assertions.hasNoPublishedEvents
import io.github.camilyed.contextparameters.testing.assertions.hasOwner
import io.github.camilyed.contextparameters.testing.assertions.hasPublishedOrderConfirmed
import io.github.camilyed.contextparameters.testing.assertions.isConfirmed
import io.github.camilyed.contextparameters.testing.assertions.isDraft
import io.github.camilyed.contextparameters.testing.assertions.wasConfirmedAt
import io.github.camilyed.contextparameters.testing.builders.OrderSnapshotBuilder.Companion.anOrder
import org.junit.jupiter.api.Test
import strikt.api.expectCatching
import strikt.api.expectThat
import strikt.assertions.isA
import strikt.assertions.isEqualTo
import strikt.assertions.isFailure
import strikt.assertions.message

class OrderTest : BaseUnitTest() {

    @Test
    fun `should create draft order`() {
        // when
        val order =
            Order.draft(
                id = OrderId("ORD-123"),
                ownerId = UserId("user-123"),
            )

        // then
        expectThat(order.toSnapshot())
            .hasId("ORD-123")
            .hasOwner("user-123")
            .isDraft()
    }

    @Test
    fun `should restore draft order from snapshot`() {
        // given
        val snapshot =
            anOrder()
                .withId("ORD-123")
                .ownedBy("user-123")
                .build()

        // when
        val order = Order.fromSnapshot(snapshot)

        // then
        expectThat(order.toSnapshot())
            .hasId("ORD-123")
            .hasOwner("user-123")
            .isDraft()
    }

    @Test
    fun `should restore confirmed order from snapshot`() {
        // given
        val snapshot =
            anOrder()
                .withId("ORD-123")
                .ownedBy("user-123")
                .confirmedAt("2026-06-05T10:15:30Z")
                .build()

        // when
        val order = Order.fromSnapshot(snapshot)

        // then
        expectThat(order.toSnapshot())
            .hasId("ORD-123")
            .hasOwner("user-123")
            .isConfirmed()
            .wasConfirmedAt("2026-06-05T10:15:30Z")
    }

    @Test
    fun `should expose current order state`() {
        // given
        val order =
            Order.draft(
                id = OrderId("ORD-123"),
                ownerId = UserId("user-123"),
            )

        // then
        expectThat(order.state())
            .isEqualTo(OrderState.Draft)
    }

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

    @Test
    fun `should confirm draft order when current user can confirm orders`() {
        // given
        val order =
            Order.fromSnapshot(
                anOrder()
                    .withId("ORD-123")
                    .ownedBy("owner-123")
                    .build(),
            )

        // and
        currentUserCanConfirmOrders("admin-123")

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

    @Test
    fun `should not confirm order when current user is not owner and cannot confirm orders`() {
        // given
        val order =
            Order.fromSnapshot(
                anOrder()
                    .withId("ORD-123")
                    .ownedBy("owner-123")
                    .build(),
            )

        // and
        currentUserIs("user-456")

        // when - then
        expectCatching {
            context(clock, domainEvents, currentUser) {
                order.confirm()
            }
        }.isFailure()
            .isA<IllegalArgumentException>()
            .message.isEqualTo("User cannot confirm this order")

        // and
        expectThat(domainEvents)
            .hasNoPublishedEvents()
    }

    @Test
    fun `should not confirm already confirmed order`() {
        // given
        val order =
            Order.fromSnapshot(
                anOrder()
                    .withId("ORD-123")
                    .ownedBy("user-123")
                    .confirmedAt("2026-06-05T10:15:30Z")
                    .build(),
            )

        // and
        currentUserIs("user-123")

        // when - then
        expectCatching {
            context(clock, domainEvents, currentUser) {
                order.confirm()
            }
        }.isFailure()
            .isA<IllegalArgumentException>()
            .message.isEqualTo("Only draft order can be confirmed")

        // and
        expectThat(domainEvents)
            .hasNoPublishedEvents()
    }
}
