package io.github.camilyed.contextparameters.domain

import java.time.Instant

interface DomainEvent

data class OrderConfirmed(
    val orderId: OrderId,
    val confirmedAt: Instant,
) : DomainEvent
