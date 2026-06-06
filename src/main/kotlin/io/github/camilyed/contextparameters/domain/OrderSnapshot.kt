package io.github.camilyed.contextparameters.domain

data class OrderSnapshot(
    val id: OrderId,
    val ownerId: UserId,
    val state: OrderState,
)
