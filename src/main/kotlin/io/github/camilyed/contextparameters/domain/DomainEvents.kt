package io.github.camilyed.contextparameters.domain

interface DomainEvents {
    fun publish(event: DomainEvent)
}
