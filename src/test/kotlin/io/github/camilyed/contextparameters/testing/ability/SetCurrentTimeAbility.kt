package io.github.camilyed.contextparameters.testing.ability

import io.github.camilyed.contextparameters.testing.fakes.TestingClockProvider
import java.time.Instant

interface SetCurrentTimeAbility {

    val clock: TestingClockProvider

    fun currentTimeIs(value: String) {
        clock.setNow(Instant.parse(value))
    }
}
