package io.github.camilyed.contextparameters.adapter.clock

import io.github.camilyed.contextparameters.domain.ClockProvider
import java.time.Instant

class SystemClockProvider : ClockProvider {
    override fun now(): Instant =
        Instant.now()
}
