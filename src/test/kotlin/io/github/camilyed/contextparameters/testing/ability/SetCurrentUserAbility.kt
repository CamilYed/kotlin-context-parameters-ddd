package io.github.camilyed.contextparameters.testing.ability

import io.github.camilyed.contextparameters.domain.UserContext
import io.github.camilyed.contextparameters.domain.UserId

interface SetCurrentUserAbility {

    var currentUser: UserContext

    fun currentUserIs(userId: String) {
        currentUser = UserContext(
            userId = UserId(userId),
            canConfirmOrders = false,
        )
    }

    fun currentUserCanConfirmOrders(userId: String) {
        currentUser = UserContext(
            userId = UserId(userId),
            canConfirmOrders = true,
        )
    }
}
