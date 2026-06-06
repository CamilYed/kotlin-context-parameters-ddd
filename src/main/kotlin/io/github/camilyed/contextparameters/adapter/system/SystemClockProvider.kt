package io.github.camilyed.contextparameters.adapter.system

import io.github.camilyed.contextparameters.domain.ClockProvider
import java.time.Instant

object SystemClockProvider : ClockProvider {
    override fun now(): Instant = Instant.now()
}
