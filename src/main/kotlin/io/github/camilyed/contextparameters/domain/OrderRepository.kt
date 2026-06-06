package io.github.camilyed.contextparameters.domain

interface OrderRepository {
    context(tx: Transaction)
    fun get(id: OrderId): Order

    context(tx: Transaction)
    fun save(order: Order)
}
