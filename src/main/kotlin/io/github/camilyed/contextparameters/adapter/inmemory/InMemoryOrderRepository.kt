package io.github.camilyed.contextparameters.adapter.inmemory

import io.github.camilyed.contextparameters.domain.Order
import io.github.camilyed.contextparameters.domain.OrderId
import io.github.camilyed.contextparameters.domain.OrderRepository
import io.github.camilyed.contextparameters.domain.Transaction

class InMemoryOrderRepository : OrderRepository {
    private val orders = mutableMapOf<OrderId, Order>()

    context(tx: Transaction)
    override fun get(id: OrderId): Order =
        orders[id]?.let { Order.fromSnapshot(it.toSnapshot()) }
            ?: error("Order $id was not found in transaction ${tx.id}")

    context(tx: Transaction)
    override fun save(order: Order) {
        orders[order.id] = Order.fromSnapshot(order.toSnapshot())
    }

    fun seed(order: Order) {
        orders[order.id] = Order.fromSnapshot(order.toSnapshot())
    }
}
