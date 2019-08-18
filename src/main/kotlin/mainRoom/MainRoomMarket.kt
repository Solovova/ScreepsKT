package mainRoom

import mainContext.marketGetSellOrdersSorted
import screeps.api.*
import screeps.utils.toMap

fun MainRoom.marketCreateBuyOrders() {
    if (!this.constant.marketBuyEnergy) return
    if (Game.market.credits < this.parent.parent.constants.globalConstant.marketMinCreditForOpenBuyOrder) return

    val priceEnergy = parent.parent.constants.globalConstant.marketBuyPriceEnergy
    if ((this.getResourceInTerminal() + this.getResourceInStorage()) < 300000) {
        val orders = Game.market.orders.toMap().filter {
            it.value.roomName == this.name
                    && it.value.resourceType == RESOURCE_ENERGY
                    && it.value.type == ORDER_BUY
        }
        if (orders.isNotEmpty()) {
            val order = orders.values.first()
            if (order.price != priceEnergy) Game.market.changeOrderPrice(order.id, priceEnergy)
            if (order.remainingAmount < 50000) Game.market.extendOrder(order.id, 50000)
        } else
            Game.market.createOrder(ORDER_BUY, RESOURCE_ENERGY, priceEnergy, 100000, this.name)
    }
}

fun MainRoom.marketCreateBuyOrdersMineral() {
    return
    if (this.name != "E54N39") return
    if (Game.market.credits < this.parent.parent.constants.globalConstant.marketMinCreditForOpenBuyOrder) return

    val buyPrice = 0.036
    val buyMineral: ResourceConstant = RESOURCE_KEANIUM
    val buyNeedInRoom = 50000
    val buyStepRemaining = 20000

    if ((this.getResourceInTerminal(buyMineral) + this.getResourceInStorage(buyMineral)) < buyNeedInRoom) {
        val orders = Game.market.orders.toMap().filter {
            it.value.roomName == this.name
                    && it.value.resourceType == buyMineral
                    && it.value.type == ORDER_BUY
        }
        if (orders.isNotEmpty()) {
            val order = orders.values.first()
            if (order.price != buyPrice) Game.market.changeOrderPrice(order.id, buyPrice)
            if (order.remainingAmount < buyStepRemaining) Game.market.extendOrder(order.id, buyStepRemaining)
        } else
            Game.market.createOrder(ORDER_BUY, buyMineral, buyPrice, buyStepRemaining, this.name)
    }
}

