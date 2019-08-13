package mainContext

import screeps.api.*

fun MainContext.ordersMyDelete() {
//    val orders: Array<Market.Order> = Game.market.getAllOrders()
//    for (order in orders) {
//        console.log("id: ${order.id}")
//        Game.market.cancelOrder(order.id)
//    }

//    Game.market.cancelOrder("5b5f47d014cb970fb7351ccb")
//    Game.market.cancelOrder("5b5f4a0414cb970fb73576d6")


}

fun MainContext.ordersCreateBuyEnergy() {
    val roomName = "E54N37"
    val orders: Array<Market.Order> = Game.market.getAllOrders {it.roomName == roomName && it.resourceType == RESOURCE_ENERGY}
    if (orders.isNotEmpty()) return

    Game.market.createOrder(ORDER_BUY, RESOURCE_ENERGY,0.012,200000,roomName)
}

fun createBuyOrders() {
    //Game.market.createOrder(ORDER_BUY, RESOURCE_ENERGY,0.012,100000,)
}

fun MainContext.getArrayOfMarket(fMineral: ResourceConstant,fRoomName: String,fTypeOrder: OrderConstant = ORDER_BUY){
//    val fPriceMineral: Double = 0.03
//    val orders: Array<Market.Order> = Game.market.getAllOrders {it.resourceType == fMineral &&
//            it.type == fTypeOrder}
//
//    val realPrice: MutableMap<String, Double> = mutableMapOf()
//    for (order in orders) {
//        val fCostTransaction = Game.market.calcTransactionCost(1000, fRoomName, order.roomName) * fPriceMineral / 1000.0
//        realPrice[order.id] = if (fTypeOrder == ORDER_SELL) order.price + fCostTransaction
//        else order.price - fCostTransaction
//    }
//
//    val minPriceOrder =


}

//BuyMineral(fMineral,fRoomName,fNeedMineral,fMaxPrice){
//    var fHaveMineral = logistics.GetQuantityMineral(fMineral);
//    var fMinBuyingQuantity = 10000;
//    if (fHaveMineral>=fNeedMineral) return;
//    fNeedBuy=fMinBuyingQuantity;
//    var fOrders = logistics.GetArrayOfMarket(fMineral,fRoomName,ORDER_SELL);
//    if (fOrders.length==0) return;
//    if (fOrders[0].realprice>fMaxPrice) return;
//    fCanBuy = fOrders[0].amount;
//    if (fCanBuy>fNeedBuy) fCanBuy=fNeedBuy;
//    if (Game.market.deal(fOrders[0].id, fCanBuy, fRoomName) == OK) {
//        var fText = 'buy mineral:' + fMineral + ' ticks:' + Game.time + ' quantity:' + fCanBuy + ' price:' + fOrders[0].price;
//        fText = fText + ' rprice:' + fOrders[0].realprice + ' id:' + fOrders[0].id;
//        Memory.logs.buyorders.push(fText);
//    }
//},