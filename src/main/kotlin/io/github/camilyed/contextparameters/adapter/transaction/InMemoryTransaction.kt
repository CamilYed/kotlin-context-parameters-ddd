package io.github.camilyed.contextparameters.adapter.transaction

import io.github.camilyed.contextparameters.domain.Transaction

class InMemoryTransaction : Transaction {

    override fun <T> within(block: () -> T): T =
        block()
}
