package io.github.camilyed.contextparameters.application

import io.github.camilyed.contextparameters.domain.ClockProvider
import io.github.camilyed.contextparameters.domain.DomainEvents
import io.github.camilyed.contextparameters.domain.OrderRepository
import io.github.camilyed.contextparameters.domain.OrderSnapshot
import io.github.camilyed.contextparameters.domain.Transaction
import io.github.camilyed.contextparameters.domain.UserContext

class ConfirmOrderUseCase(
    private val orderRepository: OrderRepository,
) {
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
}
