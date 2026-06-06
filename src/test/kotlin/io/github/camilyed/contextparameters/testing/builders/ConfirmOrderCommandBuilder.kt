package io.github.camilyed.contextparameters.testing.builders

import io.github.camilyed.contextparameters.application.ConfirmOrderCommand
import io.github.camilyed.contextparameters.domain.OrderId

class ConfirmOrderCommandBuilder private constructor() {

    private var orderId = OrderId("ORD-123")

    fun withOrderId(value: String): ConfirmOrderCommandBuilder =
        apply {
            orderId = OrderId(value)
        }

    fun build(): ConfirmOrderCommand =
        ConfirmOrderCommand(
            orderId = orderId,
        )

    companion object {
        fun aConfirmOrderCommand(): ConfirmOrderCommandBuilder =
            ConfirmOrderCommandBuilder()
    }
}
