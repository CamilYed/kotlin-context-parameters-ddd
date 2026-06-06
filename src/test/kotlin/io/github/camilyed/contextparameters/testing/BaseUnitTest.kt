package io.github.camilyed.contextparameters.testing

import io.github.camilyed.contextparameters.domain.UserContext
import io.github.camilyed.contextparameters.domain.UserId
import io.github.camilyed.contextparameters.testing.ability.CreateOrderAbility
import io.github.camilyed.contextparameters.testing.ability.SetCurrentTimeAbility
import io.github.camilyed.contextparameters.testing.ability.SetCurrentUserAbility
import io.github.camilyed.contextparameters.testing.fakes.TestingClockProvider
import io.github.camilyed.contextparameters.testing.fakes.TestingDomainEvents
import io.github.camilyed.contextparameters.testing.fakes.TestingOrderRepository
import io.github.camilyed.contextparameters.testing.fakes.TestingTransaction
import org.junit.jupiter.api.BeforeEach

abstract class BaseUnitTest :
    CreateOrderAbility,
    SetCurrentTimeAbility,
    SetCurrentUserAbility {

    override val orderRepository = TestingOrderRepository()
    override val clock = TestingClockProvider()
    val domainEvents = TestingDomainEvents()
    val transaction = TestingTransaction()

    override var currentUser = UserContext(
        userId = UserId("user-123"),
        canConfirmOrders = false,
    )

    @BeforeEach
    fun setupBaseUnitTest() {
        orderRepository.clear()
        domainEvents.clear()
        transaction.clear()

        currentTimeIs("2026-06-05T10:15:30Z")
        currentUserIs("user-123")
    }
}
