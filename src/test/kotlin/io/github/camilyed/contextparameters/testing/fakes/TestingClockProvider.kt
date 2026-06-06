package io.github.camilyed.contextparameters.testing.fakes

import io.github.camilyed.contextparameters.domain.ClockProvider
import java.time.Instant

class TestingClockProvider(
    private var currentInstant: Instant = Instant.parse("2026-06-05T10:15:30Z"),
) : ClockProvider {

    override fun now(): Instant =
        currentInstant

    fun setNow(value: Instant) {
        currentInstant = value
    }
}
