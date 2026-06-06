package io.github.camilyed.contextparameters.application

import io.github.camilyed.contextparameters.domain.OrderId

data class ConfirmOrderCommand(
    val orderId: OrderId,
)
