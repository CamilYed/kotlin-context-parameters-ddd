package io.github.camilyed.contextparameters.domain

interface OrderRepository {
    fun findById(id: OrderId): Order
    fun save(order: Order)
}
