package io.github.camilyed.contextparameters.application

import io.github.camilyed.contextparameters.domain.ClockProvider
import io.github.camilyed.contextparameters.domain.DomainEvents
import io.github.camilyed.contextparameters.domain.OrderId
import io.github.camilyed.contextparameters.domain.OrderRepository
import io.github.camilyed.contextparameters.domain.OrderSnapshot
import io.github.camilyed.contextparameters.domain.Transaction
import io.github.camilyed.contextparameters.domain.UserContext

class ConfirmOrderUseCase(
    private val orders: OrderRepository,
) {
    context(
        clock: ClockProvider,
        events: DomainEvents,
        user: UserContext,
        tx: Transaction,
    )
    fun confirm(command: ConfirmOrderCommand): OrderSnapshot {
        val order = orders.get(command.orderId)

        order.confirm()
        orders.save(order)

        return order.toSnapshot()
    }
}

data class ConfirmOrderCommand(
    val orderId: OrderId,
)
