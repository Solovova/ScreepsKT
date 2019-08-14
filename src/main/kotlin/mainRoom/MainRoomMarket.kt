package mainRoom

import screeps.api.Game
import screeps.api.Market
import screeps.api.ORDER_BUY
import screeps.api.RESOURCE_ENERGY
import screeps.utils.toMap

fun MainRoom.marketCreateBuyOrders() {
    if (!this.constant.marketBuyEnergy) return

    val priceEnergy = 0.010
    if ((this.getResourceInTerminal() + this.getResourceInStorage()) < 300000) {
        val orders = Game.market.orders.toMap().filter { it.value.roomName == this.name && it.value.resourceType == RESOURCE_ENERGY }
        if (orders.isNotEmpty()) {
            val order = orders.values.first()
            if (order.price != priceEnergy)Game.market.changeOrderPrice(order.id,priceEnergy)
            if (order.remainingAmount < 50000) Game.market.extendOrder(order.id, 50000)
        } else
            Game.market.createOrder(ORDER_BUY, RESOURCE_ENERGY, priceEnergy, 100000, this.name)
    }
}