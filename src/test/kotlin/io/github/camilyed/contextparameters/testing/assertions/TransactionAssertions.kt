package io.github.camilyed.contextparameters.testing.assertions

import io.github.camilyed.contextparameters.testing.fakes.TestingTransaction
import strikt.api.Assertion

fun Assertion.Builder<TestingTransaction>.wasCommitted(): Assertion.Builder<TestingTransaction> =
    assertThat("was committed") {
        it.wasCommitted()
    }
