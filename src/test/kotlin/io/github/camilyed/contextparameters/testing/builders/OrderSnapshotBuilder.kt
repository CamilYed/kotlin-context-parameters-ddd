package io.github.camilyed.contextparameters.testing.builders

import io.github.camilyed.contextparameters.domain.OrderId
import io.github.camilyed.contextparameters.domain.OrderSnapshot
import io.github.camilyed.contextparameters.domain.OrderState
import io.github.camilyed.contextparameters.domain.UserId
import java.time.Instant

class OrderSnapshotBuilder private constructor() {

    private var id = OrderId("ORD-123")
    private var ownerId = UserId("user-123")
    private var state: OrderState = OrderState.Draft

    fun withId(value: String): OrderSnapshotBuilder =
        apply {
            id = OrderId(value)
        }

    fun ownedBy(value: String): OrderSnapshotBuilder =
        apply {
            ownerId = UserId(value)
        }

    fun confirmedAt(value: String): OrderSnapshotBuilder =
        apply {
            state = OrderState.Confirmed(
                confirmedAt = Instant.parse(value),
            )
        }

    fun build(): OrderSnapshot =
        OrderSnapshot(
            id = id,
            ownerId = ownerId,
            state = state,
        )

    companion object {
        fun anOrder(): OrderSnapshotBuilder =
            OrderSnapshotBuilder()
    }
}
