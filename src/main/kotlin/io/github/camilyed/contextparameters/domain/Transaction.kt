package io.github.camilyed.contextparameters.domain

interface Transaction {
    fun <T> within(block: () -> T): T
}
