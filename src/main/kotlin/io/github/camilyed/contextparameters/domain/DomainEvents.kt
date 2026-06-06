package io.github.camilyed.contextparameters.domain

interface DomainEvent

interface DomainEvents {
    fun publish(event: DomainEvent)
}
