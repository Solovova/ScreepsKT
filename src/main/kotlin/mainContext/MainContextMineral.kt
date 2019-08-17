package mainContext

import OrderRecord
import screeps.api.*
import MineralData
import RESOURCES_ALL
import toSecDigit

fun MainContext.marketGetSellOrdersSorted(sellMineral: ResourceConstant, roomName: String): List<OrderRecord> {

    val buyPriceEnergy = this.constants.globalConstant.marketBuyPriceEnergy
    val result: MutableList<OrderRecord> = mutableListOf()

    val orders = Game.market.getAllOrders().filter {
        it.resourceType == sellMineral
                && it.type == ORDER_SELL
    }
    for (order in orders) {
        val transactionCost:Double = Game.market.calcTransactionCost(1000,order.roomName, roomName).toDouble()*buyPriceEnergy/1000.0
        result.add(result.size,OrderRecord(order,order.price + transactionCost))
    }

    result.sortBy { it.realPrice }
    return result.toList()
}

fun MainContext.marketGetBuyOrdersSorted(sellMineral: ResourceConstant, roomName: String): List<OrderRecord> {

    val buyPriceEnergy = this.constants.globalConstant.marketBuyPriceEnergy
    val result: MutableList<OrderRecord> = mutableListOf()

    val orders = Game.market.getAllOrders().filter {
        it.resourceType == sellMineral
                && it.type == ORDER_BUY
    }

    for (order in orders) {
        val transactionCost:Double = Game.market.calcTransactionCost(1000,order.roomName, roomName).toDouble()*buyPriceEnergy/1000.0
        result.add(result.size,OrderRecord(order,order.price - transactionCost))
    }

    result.sortByDescending { it.realPrice }
    return result.toList()
}

fun MainContext.marketShowSellOrdersRealPrice(resourceConstant: ResourceConstant = RESOURCE_ENERGY) {
    if (this.constants.mainRooms.isEmpty()) return
    val marketSell = this.marketGetSellOrdersSorted(resourceConstant, this.constants.mainRooms[0])
    console.log("Sell orders $resourceConstant}")
    for (record in marketSell) {
        val strPrice = record.order.price.asDynamic().toFixed(3).toString().padEnd(6)
        val strRealPrice = record.realPrice.asDynamic().toFixed(3).toString().padEnd(6)
        console.log("id: ${record.order.id} mineral: $resourceConstant price: $strPrice real price: $strRealPrice quantity:${record.order.remainingAmount}")
    }
}

fun MainContext.marketShowBuyOrdersRealPrice(resourceConstant: ResourceConstant = RESOURCE_ENERGY) {
    if (this.constants.mainRooms.isEmpty()) return
    val marketSell = this.marketGetBuyOrdersSorted(resourceConstant, this.constants.mainRooms[0])

    console.log("Buy orders $resourceConstant}")
    for (record in marketSell) {
        val strPrice = record.order.price.asDynamic().toFixed(3).toString().padEnd(6)
        val strRealPrice = record.realPrice.asDynamic().toFixed(3).toString().padEnd(6)
        console.log("id: ${record.order.id} price: $strPrice real price: $strRealPrice  quantity:${record.order.remainingAmount}" )
    }
}

fun MainContext.mineralDataFill() {
    for (res in RESOURCES_ALL)
        mineralData[res] = MineralData(quantity = this.mainRoomCollector.rooms.values.sumBy {it.getResource(res)})
}

fun MainContext.mineralInfoShow() {
    val mineralInfo = MineralInfo()
    for (res in RESOURCES_ALL)
        if ((mineralData[res]?.quantity ?: 0) != 0)
            mineralInfo.addColumn(arrayOf("$res", (mineralData[res]?.quantity ?: 0).toString().toSecDigit(),"_______________"))
    mineralInfo.show(this)
}