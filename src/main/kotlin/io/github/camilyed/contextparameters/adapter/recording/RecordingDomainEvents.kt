package io.github.camilyed.contextparameters.adapter.recording

import io.github.camilyed.contextparameters.domain.DomainEvent
import io.github.camilyed.contextparameters.domain.DomainEvents

class RecordingDomainEvents : DomainEvents {
    private val events = mutableListOf<DomainEvent>()

    val published: List<DomainEvent>
        get() = events.toList()

    override fun publish(event: DomainEvent) {
        events += event
    }
}
