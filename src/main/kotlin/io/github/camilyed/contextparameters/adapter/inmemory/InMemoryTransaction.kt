package io.github.camilyed.contextparameters.adapter.inmemory

import io.github.camilyed.contextparameters.domain.Transaction

@JvmInline
value class InMemoryTransaction(
    override val id: String,
) : Transaction
