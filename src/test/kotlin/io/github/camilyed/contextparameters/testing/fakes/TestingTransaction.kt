package io.github.camilyed.contextparameters.testing.fakes

import io.github.camilyed.contextparameters.domain.Transaction

class TestingTransaction : Transaction {

    private var committed = false

    override fun <T> within(block: () -> T): T {
        val result = block()

        committed = true

        return result
    }

    fun wasCommitted(): Boolean =
        committed

    fun clear() {
        committed = false
    }
}
