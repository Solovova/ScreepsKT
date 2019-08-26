package mainContext

import OrderRecord
import REACTION_TIME
import screeps.api.*
import RESOURCES_ALL
import accounts.initMineralData
import mainRoom.MainRoom
import screeps.utils.toMap
import toSecDigit
import kotlin.math.max
import kotlin.math.min

fun MainContext.marketDeleteEmptyOffers() {
    val orders = Game.market.orders.toMap().filter {
        it.value.remainingAmount == 0
                && !it.value.active
                && it.value.amount == 0
    }
    for (order in orders) Game.market.cancelOrder(order.value.id)
}

fun MainContext.marketGetSellOrdersSorted(sellMineral: ResourceConstant, roomName: String): List<OrderRecord> {

    val buyPriceEnergy = this.constants.globalConstant.marketBuyPriceEnergy
    val result: MutableList<OrderRecord> = mutableListOf()

    val orders = Game.market.getAllOrders().filter {
        it.resourceType == sellMineral
                && it.type == ORDER_SELL
                && it.amount != 0
    }
    for (order in orders) {
        val transactionCost: Double = Game.market.calcTransactionCost(1000, order.roomName, roomName).toDouble() * buyPriceEnergy / 1000.0
        result.add(result.size, OrderRecord(order, order.price + transactionCost))
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
                && it.amount != 0
    }

    for (order in orders) {
        val transactionCost: Double = Game.market.calcTransactionCost(1000, order.roomName, roomName).toDouble() * buyPriceEnergy / 1000.0
        result.add(result.size, OrderRecord(order, order.price - transactionCost))
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
        console.log("id: ${record.order.id} price: $strPrice real price: $strRealPrice  quantity:${record.order.remainingAmount}")
    }
}

fun MainContext.mineralDataFill() {
    initMineralData(this.mineralData)
    for (res in RESOURCES_ALL) {
        val quantity: Int = this.mainRoomCollector.rooms.values.sumBy { it.getResource(res) }
        val need: Int = this.mainRoomCollector.rooms.values.sumBy { it.needMineral[res] ?: 0 }
        val mineralDataRecord: MineralDataRecord? = this.mineralData[res]
        if (mineralDataRecord == null) {
            if (quantity != 0 || need != 0) mineralData[res] = MineralDataRecord(quantity = quantity, need = need)
        } else {
            mineralDataRecord.quantity = quantity
            mineralDataRecord.need = need
            mineralDataRecord.quantityDown = 0
            mineralDataRecord.quantityUp = 0
        }
    }
}

fun MainContext.mineralInfoShow() {
    val mineralInfo = MineralInfo(numRows = 5)
    mineralInfo.addColumn(arrayOf(
            "Res:",
            "Quantity:",
            "Balance:",
            "Need:"))
    for (res in RESOURCES_ALL) {
        val mineralDataRecord: MineralDataRecord? = mineralData[res]
        if (mineralDataRecord != null) {
            val strQuantity = if (mineralDataRecord.quantity == 0) "" else mineralDataRecord.quantity.toString().toSecDigit()
            //val strMinPrice = if (mineralDataRecord.priceMin == 0.0) "" else mineralDataRecord.priceMin.toString()
            //val strMaxPrice = if (mineralDataRecord.priceMax == 0.0) "" else mineralDataRecord.priceMax.toString()
            //val strProduceUp = if (mineralDataRecord.quantityUp == 0) "" else mineralDataRecord.quantityUp.toString()
            //val strProduceDown = if (mineralDataRecord.quantityDown == 0) "" else mineralDataRecord.quantityDown.toString()
            val strBalance = if (mineralDataRecord.quantityUp - mineralDataRecord.quantityDown == 0) ""
            else (mineralDataRecord.quantityUp - mineralDataRecord.quantityDown).toString()

            val strNeed = if (mineralDataRecord.need > mineralDataRecord.quantity) (-mineralDataRecord.need + mineralDataRecord.quantity).toString()
            else ""


            mineralInfo.addColumn(arrayOf(
                    "$res",
                    strQuantity,
                    strBalance,
                    strNeed))
        }
    }

    mineralInfo.show(this)
}

fun MainContext.mineralProductionFill() {
    for (room in this.mainRoomCollector.rooms.values) {
        if (room.constant.reactionActive != "") {
            val reaction = room.constant.reactionActive.unsafeCast<ResourceConstant>()
            if (room.structureLabSort.size !in arrayOf(3, 6, 10)) continue
            val reactionComponent = this.constants.globalConstant.labReactionComponent[reaction]
                    ?: return
            if (reactionComponent.size != 2) return
            val reactionTime = REACTION_TIME[reaction] ?: 100
            val produce = 1000 * (room.structureLabSort.size - 2) * 5 / reactionTime

            val mineralDataRecord: MineralDataRecord? = this.mineralData[reaction]
            if (mineralDataRecord != null) mineralDataRecord.quantityUp += produce
            else this.mineralData[reaction] = MineralDataRecord(quantityUp = produce)
            for (ind in 0..1) {
                val reactionComponentRc = reactionComponent[ind].unsafeCast<ResourceConstant>()
                val mineralDataRecordComp: MineralDataRecord? = this.mineralData[reactionComponentRc]
                if (mineralDataRecordComp != null) mineralDataRecordComp.quantityDown += produce
                else this.mineralData[reactionComponentRc] = MineralDataRecord(quantityDown = produce)
            }
        }
    }
}

fun MainContext.mineralSellExcess() {
    for (resource in RESOURCES_ALL) {
        val mineralDataRecord = mineralData[resource] ?: continue
        if (mineralDataRecord.marketSellExcess == 0 || mineralDataRecord.quantity < mineralDataRecord.marketSellExcess) continue
        val mainRoomForSale: MainRoom = this.mainRoomCollector.rooms.values.firstOrNull { it.getResourceInTerminal(resource) > 5000 }
                ?: continue
        val orders = this.marketGetBuyOrdersSorted(resource, mainRoomForSale.name)
        if (orders.isEmpty()) continue
        val order = orders[0]
        if (order.realPrice > mineralDataRecord.priceMin) Game.market.deal(order.order.id, min(order.order.amount, 5000), mainRoomForSale.name)
    }
}

fun MainContext.mineralSellOrderCreate(resource: ResourceConstant, mineralDataRecord: MineralDataRecord): Double? {
    val quantitySkip = 30000
    val quantityOrderAmount = 10000
    if (mineralDataRecord.quantity < mineralDataRecord.marketSellExcess) return null

    val orders = Game.market.getAllOrders().filter {
        it.resourceType == resource
                && it.type == ORDER_SELL
                && it.remainingAmount != 0
                && it.amount != 0
    }.sortedBy { it.price }
    var sumQuantity = 0
    var sellOrder: Market.Order? = null
    for (order in orders) {
        sumQuantity += order.remainingAmount
        sellOrder = order
        if (sumQuantity > quantitySkip) break
    }

    if (sellOrder == null) return null

    val myOrderPrice = max(mineralDataRecord.priceMin, sellOrder.price - 0.001)

    //console.log("Create SELL order for $resource price: $myOrderPrice")

    val myOrders = Game.market.orders.values.toList().firstOrNull {
        it.resourceType == resource
                && it.type == ORDER_SELL
    }

    if (myOrders == null) {
        if (mineralDataRecord.sellFromRoom == "") return null
        Game.market.createOrder(ORDER_SELL, resource, myOrderPrice, quantityOrderAmount, mineralDataRecord.sellFromRoom)
    } else {
        if (myOrders.remainingAmount < quantityOrderAmount) Game.market.extendOrder(myOrders.id, quantityOrderAmount - myOrders.remainingAmount)
        if (myOrderPrice != myOrders.price) Game.market.changeOrderPrice(myOrders.id, myOrderPrice)
    }

    return myOrderPrice
}

fun MainContext.mineralSellDirect(resource: ResourceConstant, mineralDataRecord: MineralDataRecord, minPrice: Double) {
    if (mineralDataRecord.quantity < mineralDataRecord.marketSellExcess) return
    val mainRoomForSale: MainRoom = this.mainRoomCollector.rooms.values.firstOrNull { it.getResourceInTerminal(resource) > 5000 }
            ?: return
    val orders = this.marketGetBuyOrdersSorted(resource, mainRoomForSale.name)
    if (orders.isEmpty()) return
    val order = orders[0]
    //console.log("$resource ${order.realPrice}  $minPrice")
    if (order.realPrice > minPrice) Game.market.deal(order.order.id, min(order.order.amount, 5000), mainRoomForSale.name)
}

fun MainContext.mineralBuyDirect(resource: ResourceConstant, mineralDataRecord: MineralDataRecord, minPrice: Double) {
    if (mineralDataRecord.quantity > mineralDataRecord.marketSellExcess) return
    if (mineralDataRecord.quantity > mineralDataRecord.storeMax) return
    val mainRoomForBuyName: String = mineralDataRecord.buyToRoom
    if (mainRoomForBuyName == "") return

    val orders = this.marketGetSellOrdersSorted(resource, mainRoomForBuyName)
    if (orders.isEmpty()) return
    val order = orders[0]
    //console.log("$resource ${order.realPrice}  $minPrice")
    if (order.realPrice < minPrice) Game.market.deal(order.order.id, min(order.order.amount, 5000), mainRoomForBuyName)
}

fun MainContext.mineralSellBuy() {
    for (resource in RESOURCES_ALL) {
        val mineralDataRecord = mineralData[resource] ?: continue
        val minPrice = this.mineralSellOrderCreate(resource, mineralDataRecord)
        if (minPrice != null) this.mineralSellDirect(resource, mineralDataRecord, minPrice)

        if (Game.market.credits > 200000.0) {
            if (mineralDataRecord.onlyDirectBuy) this.mineralBuyDirect(resource,mineralDataRecord,mineralDataRecord.priceMin)
        }

    }
}