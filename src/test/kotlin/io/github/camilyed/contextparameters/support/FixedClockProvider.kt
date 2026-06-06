package io.github.camilyed.contextparameters.support

import io.github.camilyed.contextparameters.domain.ClockProvider
import java.time.Instant

class FixedClockProvider(
    private val fixedNow: Instant,
) : ClockProvider {
    override fun now(): Instant = fixedNow
}
