package io.github.camilyed.contextparameters.adapter.events

import io.github.camilyed.contextparameters.domain.DomainEvent
import io.github.camilyed.contextparameters.domain.DomainEvents

class InMemoryDomainEvents : DomainEvents {

    private val events = mutableListOf<DomainEvent>()

    override fun publish(event: DomainEvent) {
        events += event
    }

    fun publishedEvents(): List<DomainEvent> =
        events.toList()
}
