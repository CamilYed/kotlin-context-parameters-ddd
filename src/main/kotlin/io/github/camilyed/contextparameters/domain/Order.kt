package io.github.camilyed.contextparameters.domain

class Order private constructor(
    val id: OrderId,
    val ownerId: UserId,
    private var state: OrderState,
) {
    fun state(): OrderState =
        state

    context(clock: ClockProvider, events: DomainEvents, user: UserContext)
    fun confirm() {
        require(state == OrderState.Draft) {
            "Only draft order can be confirmed"
        }

        require(user.userId == ownerId || user.canConfirmOrders) {
            "User cannot confirm this order"
        }

        val now = clock.now()

        state = OrderState.Confirmed(
            confirmedAt = now,
        )

        events.publish(
            OrderConfirmed(
                orderId = id,
                confirmedAt = now,
            ),
        )
    }

    fun toSnapshot(): OrderSnapshot =
        OrderSnapshot(
            id = id,
            ownerId = ownerId,
            state = state,
        )

    companion object {
        fun draft(
            id: OrderId,
            ownerId: UserId,
        ): Order =
            Order(
                id = id,
                ownerId = ownerId,
                state = OrderState.Draft,
            )

        fun fromSnapshot(snapshot: OrderSnapshot): Order =
            Order(
                id = snapshot.id,
                ownerId = snapshot.ownerId,
                state = snapshot.state,
            )
    }
}
