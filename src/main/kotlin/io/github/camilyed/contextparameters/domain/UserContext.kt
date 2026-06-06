package io.github.camilyed.contextparameters.domain

data class UserContext(
    val userId: UserId,
    val canConfirmOrders: Boolean = false,
)
