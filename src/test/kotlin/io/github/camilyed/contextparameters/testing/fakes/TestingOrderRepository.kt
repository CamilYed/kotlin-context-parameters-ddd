package io.github.camilyed.contextparameters.testing.fakes

import io.github.camilyed.contextparameters.domain.Order
import io.github.camilyed.contextparameters.domain.OrderId
import io.github.camilyed.contextparameters.domain.OrderNotFoundException
import io.github.camilyed.contextparameters.domain.OrderRepository

class TestingOrderRepository : OrderRepository {

    private val orders = mutableMapOf<OrderId, Order>()

    override fun findById(id: OrderId): Order =
        orders[id]
            ?: throw OrderNotFoundException("Order with id ${id.value} not found")

    override fun save(order: Order) {
        orders[order.id] = order
    }

    fun clear() {
        orders.clear()
    }
}
