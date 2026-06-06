package io.github.camilyed.contextparameters.domain

@JvmInline
value class OrderId(val value: String) {
    init {
        require(value.isNotBlank()) { "Order id cannot be blank" }
    }
}

@JvmInline
value class UserId(val value: String) {
    init {
        require(value.isNotBlank()) { "User id cannot be blank" }
    }
}
