package io.github.camilyed.contextparameters.domain

import java.time.Instant

class Order private constructor(
    val id: OrderId,
    val ownerId: UserId,
    private var status: OrderStatus,
    private var confirmedAtValue: Instant? = null,
) {
    val confirmedAt: Instant?
        get() = confirmedAtValue

    fun status(): OrderStatus = status

    context(clock: ClockProvider, events: DomainEvents, user: UserContext)
    fun confirm() {
        require(status == OrderStatus.Draft) {
            "Only draft orders can be confirmed"
        }
        require(user.canConfirmOrders || user.userId == ownerId) {
            "User cannot confirm this order"
        }

        val now = clock.now()

        status = OrderStatus.Confirmed
        confirmedAtValue = now
        events.publish(
            OrderConfirmed(
                orderId = id,
                confirmedAt = now,
                confirmedBy = user.userId,
            ),
        )
    }

    fun toSnapshot(): OrderSnapshot =
        OrderSnapshot(
            id = id,
            ownerId = ownerId,
            status = status,
            confirmedAt = confirmedAtValue,
        )

    companion object {
        fun draft(id: OrderId, ownerId: UserId): Order =
            Order(
                id = id,
                ownerId = ownerId,
                status = OrderStatus.Draft,
            )

        fun fromSnapshot(snapshot: OrderSnapshot): Order =
            Order(
                id = snapshot.id,
                ownerId = snapshot.ownerId,
                status = snapshot.status,
                confirmedAtValue = snapshot.confirmedAt,
            )
    }
}

enum class OrderStatus {
    Draft,
    Confirmed,
}

data class OrderSnapshot(
    val id: OrderId,
    val ownerId: UserId,
    val status: OrderStatus,
    val confirmedAt: Instant?,
)
