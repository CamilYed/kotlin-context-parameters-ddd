package io.github.camilyed.contextparameters.domain

import java.time.Instant

interface ClockProvider {
    fun now(): Instant
}
