package io.github.camilyed.contextparameters.testing.ability

import io.github.camilyed.contextparameters.domain.Order
import io.github.camilyed.contextparameters.domain.OrderSnapshot
import io.github.camilyed.contextparameters.testing.builders.OrderSnapshotBuilder
import io.github.camilyed.contextparameters.testing.fakes.TestingOrderRepository

interface CreateOrderAbility {

    val orderRepository: TestingOrderRepository

    fun thereIsAnOrder(builder: OrderSnapshotBuilder): OrderSnapshot {
        val snapshot = builder.build()

        orderRepository.save(Order.fromSnapshot(snapshot))

        return snapshot
    }
}
