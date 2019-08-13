package mainRoom

import screeps.api.Game
import screeps.api.Market
import screeps.api.ORDER_BUY
import screeps.api.RESOURCE_ENERGY
import screeps.utils.toMap

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
    val priceEnergy: Double = 0.010
    if ((this.getResourceInTerminal() + this.getResourceInStorage()) < 300000) {
        //val orders: Array<Market.Order> = Game.market.getAllOrders { it.roomName == this.name && it.resourceType == RESOURCE_ENERGY }
        val orders = Game.market.orders.toMap().filter { it.value.roomName == this.name && it.value.resourceType == RESOURCE_ENERGY }
        if (orders.isNotEmpty()) {
            val remainingAmount = orders.values.sumBy { it.remainingAmount }
            if (remainingAmount < 50000) {
                val order = orders.values.first()
                if (order.price != priceEnergy)Game.market.changeOrderPrice(order.id,priceEnergy)
                Game.market.extendOrder(order.id, 50000)
            }
        } else
            Game.market.createOrder(ORDER_BUY, RESOURCE_ENERGY, priceEnergy, 100000, this.name)
    }

//    val orders: Array<Market.Order> = Game.market.getAllOrders { it.roomName == this.name && it.resourceType == RESOURCE_ENERGY }
//    for (order in orders) console.log("Order id: ${order.id} remainingAmount: ${order.remainingAmount}")
}