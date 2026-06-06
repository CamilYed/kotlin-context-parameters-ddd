package io.github.camilyed.contextparameters.domain

import java.time.Instant

data class OrderConfirmed(
    val orderId: OrderId,
    val confirmedAt: Instant,
    val confirmedBy: UserId,
) : DomainEvent
