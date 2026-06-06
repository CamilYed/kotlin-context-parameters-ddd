package io.github.camilyed.contextparameters

import io.github.camilyed.contextparameters.adapter.inmemory.InMemoryOrderRepository
import io.github.camilyed.contextparameters.adapter.inmemory.InMemoryTransaction
import io.github.camilyed.contextparameters.adapter.recording.RecordingDomainEvents
import io.github.camilyed.contextparameters.adapter.system.SystemClockProvider
import io.github.camilyed.contextparameters.application.ConfirmOrderCommand
import io.github.camilyed.contextparameters.application.ConfirmOrderUseCase
import io.github.camilyed.contextparameters.domain.Order
import io.github.camilyed.contextparameters.domain.OrderId
import io.github.camilyed.contextparameters.domain.Role
import io.github.camilyed.contextparameters.domain.UserContext
import io.github.camilyed.contextparameters.domain.UserId

fun main() {
    val repository = InMemoryOrderRepository()
    val useCase = ConfirmOrderUseCase(repository)

    val orderId = OrderId("ORD-123")
    val owner = UserId("user-123")
    repository.seed(Order.draft(orderId, owner))

    val clock = SystemClockProvider
    val events = RecordingDomainEvents()
    val user = UserContext(
        userId = owner,
        roles = setOf(Role.Customer),
    )
    val tx = InMemoryTransaction("demo-tx")

    context(clock, events, user, tx) {
        val snapshot = useCase.confirm(ConfirmOrderCommand(orderId))

        println("Order status: ${snapshot.status}")
        println("Confirmed at: ${snapshot.confirmedAt}")
        println("Events: ${events.published}")
    }
}
