package io.github.camilyed.contextparameters.testing.fakes

import io.github.camilyed.contextparameters.domain.DomainEvent
import io.github.camilyed.contextparameters.domain.DomainEvents

class TestingDomainEvents : DomainEvents {

    private val events = mutableListOf<DomainEvent>()

    override fun publish(event: DomainEvent) {
        events += event
    }

    fun publishedEvents(): List<DomainEvent> =
        events.toList()

    fun clear() {
        events.clear()
    }
}
