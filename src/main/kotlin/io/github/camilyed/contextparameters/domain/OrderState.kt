package io.github.camilyed.contextparameters.domain

import java.time.Instant

sealed interface OrderState {

    data object Draft : OrderState

    data class Confirmed(
        val confirmedAt: Instant,
    ) : OrderState
}
