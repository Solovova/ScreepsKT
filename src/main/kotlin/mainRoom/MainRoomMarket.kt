package mainRoom

import screeps.api.Game
import screeps.api.Market
import screeps.api.ORDER_BUY
import screeps.api.RESOURCE_ENERGY

fun MainRoom.marketCreateBuyOrders() {
    if (!this.constant.marketBuyEnergy) return
    val orders: Array<Market.Order> = Game.market.getAllOrders {it.roomName == this.name && it.resourceType == RESOURCE_ENERGY }
    if (orders.isNotEmpty()) {
        if (this.getResourceInTerminal() + this.getResourceInStorage() < 300000){
            val order = orders.firstOrNull()
            if (order != null) {
                if (order.remainingAmount < 100000) Game.market.extendOrder(order.id,100000)
            }
        }
        return
    }
    Game.market.createOrder(ORDER_BUY, RESOURCE_ENERGY,0.012,200000,this.name)
}