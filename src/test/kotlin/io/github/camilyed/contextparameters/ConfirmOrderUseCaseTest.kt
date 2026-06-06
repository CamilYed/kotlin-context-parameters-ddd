package io.github.camilyed.contextparameters

import io.github.camilyed.contextparameters.adapter.inmemory.InMemoryOrderRepository
import io.github.camilyed.contextparameters.adapter.inmemory.InMemoryTransaction
import io.github.camilyed.contextparameters.adapter.recording.RecordingDomainEvents
import io.github.camilyed.contextparameters.application.ConfirmOrderCommand
import io.github.camilyed.contextparameters.application.ConfirmOrderUseCase
import io.github.camilyed.contextparameters.domain.Order
import io.github.camilyed.contextparameters.domain.OrderConfirmed
import io.github.camilyed.contextparameters.domain.OrderId
import io.github.camilyed.contextparameters.domain.OrderStatus
import io.github.camilyed.contextparameters.domain.Role
import io.github.camilyed.contextparameters.domain.UserContext
import io.github.camilyed.contextparameters.domain.UserId
import io.github.camilyed.contextparameters.support.FixedClockProvider
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.time.Instant

class ConfirmOrderUseCaseTest {
    @Test
    fun `should confirm order and publish domain event`() {
        // given
        val now = Instant.parse("2026-06-05T10:15:30Z")
        val orderId = OrderId("ORD-123")
        val ownerId = UserId("user-123")

        val repository = InMemoryOrderRepository().also {
            it.seed(Order.draft(orderId, ownerId))
        }
        val useCase = ConfirmOrderUseCase(repository)

        val clock = FixedClockProvider(now)
        val events = RecordingDomainEvents()
        val user = UserContext(
            userId = ownerId,
            roles = setOf(Role.Customer),
        )
        val tx = InMemoryTransaction("test-tx")

        // when
        val snapshot = context(clock, events, user, tx) {
            useCase.confirm(ConfirmOrderCommand(orderId))
        }

        // then
        assertEquals(OrderStatus.Confirmed, snapshot.status)
        assertEquals(now, snapshot.confirmedAt)
        assertEquals(
            listOf(
                OrderConfirmed(
                    orderId = orderId,
                    confirmedAt = now,
                    confirmedBy = ownerId,
                ),
            ),
            events.published,
        )
    }

    @Test
    fun `should reject user who is not owner and cannot confirm orders`() {
        // given
        val orderId = OrderId("ORD-456")
        val ownerId = UserId("owner-123")

        val repository = InMemoryOrderRepository().also {
            it.seed(Order.draft(orderId, ownerId))
        }
        val useCase = ConfirmOrderUseCase(repository)

        val clock = FixedClockProvider(Instant.parse("2026-06-05T10:15:30Z"))
        val events = RecordingDomainEvents()
        val user = UserContext(
            userId = UserId("somebody-else"),
            roles = setOf(Role.Customer),
        )
        val tx = InMemoryTransaction("test-tx")

        // when / then
        val exception = assertThrows(IllegalArgumentException::class.java) {
            context(clock, events, user, tx) {
                useCase.confirm(ConfirmOrderCommand(orderId))
            }
        }

        assertEquals("User cannot confirm this order", exception.message)
        assertEquals(emptyList<Any>(), events.published)
    }

    @Test
    fun `should allow order manager to confirm somebody else order`() {
        // given
        val now = Instant.parse("2026-06-05T10:15:30Z")
        val orderId = OrderId("ORD-789")
        val ownerId = UserId("owner-123")

        val repository = InMemoryOrderRepository().also {
            it.seed(Order.draft(orderId, ownerId))
        }
        val useCase = ConfirmOrderUseCase(repository)

        val clock = FixedClockProvider(now)
        val events = RecordingDomainEvents()
        val user = UserContext(
            userId = UserId("manager-123"),
            roles = setOf(Role.OrderManager),
        )
        val tx = InMemoryTransaction("test-tx")

        // when
        val snapshot = context(clock, events, user, tx) {
            useCase.confirm(ConfirmOrderCommand(orderId))
        }

        // then
        assertEquals(OrderStatus.Confirmed, snapshot.status)
        assertEquals(
            listOf(
                OrderConfirmed(
                    orderId = orderId,
                    confirmedAt = now,
                    confirmedBy = UserId("manager-123"),
                ),
            ),
            events.published,
        )
    }
}
