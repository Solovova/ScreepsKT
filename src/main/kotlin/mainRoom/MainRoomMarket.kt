package mainRoom

import screeps.api.Game
import screeps.api.Market
import screeps.api.ORDER_BUY
import screeps.api.RESOURCE_ENERGY

fun MainRoom.marketClearNonActive() {
    val orders: Array<Market.Order> = Game.market.getAllOrders {
        (it.roomName == this.name
                && it.resourceType == RESOURCE_ENERGY)
                && !it.active
    }
    for (order in orders) Game.market.cancelOrder(order.id)

}

fun MainRoom.marketCreateBuyOrders() {
    if (!this.constant.marketBuyEnergy) return
    //this.marketClearNonActive()
    if ((this.getResourceInTerminal() + this.getResourceInStorage()) < 300000) {
        val orders: Array<Market.Order> = Game.market.getAllOrders { it.roomName == this.name && it.resourceType == RESOURCE_ENERGY }
        if (orders.isNotEmpty()) {
            val remainingAmount = orders.sumBy { it.remainingAmount }
            if (remainingAmount < 50000) Game.market.extendOrder(orders[0].id, 50000)
        } else
            Game.market.createOrder(ORDER_BUY, RESOURCE_ENERGY, 0.012, 100000, this.name)
    }

//    val orders: Array<Market.Order> = Game.market.getAllOrders { it.roomName == this.name && it.resourceType == RESOURCE_ENERGY }
//    for (order in orders) console.log("Order id: ${order.id} remainingAmount: ${order.remainingAmount}")
}