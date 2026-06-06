package io.github.camilyed.contextparameters.domain

data class UserContext(
    val userId: UserId,
    val roles: Set<Role>,
) {
    val canConfirmOrders: Boolean
        get() = Role.OrderManager in roles || Role.Admin in roles
}

enum class Role {
    Customer,
    OrderManager,
    Admin,
}
