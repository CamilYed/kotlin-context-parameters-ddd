package io.github.camilyed.contextparameters

import io.github.camilyed.contextparameters.adapter.clock.SystemClockProvider
import io.github.camilyed.contextparameters.adapter.events.InMemoryDomainEvents
import io.github.camilyed.contextparameters.adapter.persistence.InMemoryOrderRepository
import io.github.camilyed.contextparameters.adapter.transaction.InMemoryTransaction
import io.github.camilyed.contextparameters.application.ConfirmOrderCommand
import io.github.camilyed.contextparameters.application.ConfirmOrderUseCase
import io.github.camilyed.contextparameters.domain.Order
import io.github.camilyed.contextparameters.domain.OrderId
import io.github.camilyed.contextparameters.domain.UserContext
import io.github.camilyed.contextparameters.domain.UserId

fun main() {
    val orderRepository = InMemoryOrderRepository()
    val clock = SystemClockProvider()
    val events = InMemoryDomainEvents()
    val transaction = InMemoryTransaction()

    val orderId = OrderId("ORD-123")
    val ownerId = UserId("user-123")

    orderRepository.save(
        Order.draft(
            id = orderId,
            ownerId = ownerId,
        ),
    )

    val useCase = ConfirmOrderUseCase(orderRepository)

    val currentUser = UserContext(
        userId = ownerId,
    )

    val confirmedOrder =
        context(clock, events, currentUser, transaction) {
            useCase.confirm(
                ConfirmOrderCommand(orderId),
            )
        }

    println(confirmedOrder)
    println(events.publishedEvents())
}
