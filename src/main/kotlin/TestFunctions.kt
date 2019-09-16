import mainContext.MainContext
import mainContext.messenger
import mainRoom.MainRoom
import mainRoom.doSnapShot
import screeps.api.*
import slaveRoom.SlaveRoom
import kotlin.math.roundToInt

fun testingFunctions (mainContext : MainContext) {
//    val mCash:MutableMap<ResourceConstant,Int> = mutableMapOf()
//    //mCash[RESOURCE_ENERGY] = 110
//    mCash[RESOURCE_CATALYST] = 200
//    mCash[RESOURCE_ENERGY] = (mCash[RESOURCE_ENERGY] ?: 0) + 100
//    for (rec in mCash) mainContext.messenger("INFO",rec.key.toString(),rec.value.toString(), COLOR_YELLOW)
//    val orders = Game.market.orders.toMap().filter { it.value.roomName == "E53N39" }
//    for (order in orders) mainContext.messenger("INFO",order.value.id,order.value.active.toString(), COLOR_YELLOW)

//    Game.market.cancelOrder("5d52874a49ede365f76ee09d")
//    Game.market.cancelOrder("5d52ac3849ede365f775e31b")
//    Game.market.cancelOrder("5d52b29c49ede365f7771b36")

//    mainContext.battleGroupContainer.addBattleGroup("1")
//    mainContext.battleGroupContainer.addBattleGroup("2")
//    //mainContext.battleGroupContainer.deleteBattleGroup("2")
//    val battleGroup1 = mainContext.battleGroupContainer.getBattleGroup("1")
//    val battleGroup2 = mainContext.battleGroupContainer.getBattleGroup("2")
//    if (battleGroup1!= null) {
//        battleGroup1.constants.testCashedBG += 2
//        battleGroup1.constants.testSimpleBG += 2
//        console.log("BG1 ${battleGroup1.constants.testCashedBG} ${battleGroup1.constants.testSimpleBG}")
//    }
//
//    if (battleGroup2!= null) {
//        battleGroup2.constants.testCashedBG += 3
//        battleGroup2.constants.testSimpleBG += 3
//        console.log("BG2 ${battleGroup2.constants.testCashedBG} ${battleGroup2.constants.testSimpleBG}")
//    }

//    for (mainRoom in mainContext.mainRoomCollector.rooms.values) {
//        //mainRoom.doSnapShot()
//        if (mainRoom.constant.needCleaner) mainContext.messenger("INFO",mainRoom.name,"Main need clean", COLOR_RED)
//        for (slaveRoom in mainRoom.slaveRooms.values){
//            //slaveRoom.doSnapShot()
//            if (slaveRoom.constant.needCleaner) mainContext.messenger("INFO",slaveRoom.name,"Slave need clean", COLOR_RED)
//        }
//
//    }

    //mainContext.directControlTaskClearInRoom("W5N3")
    //console.log("1234".toSecDigit())

//    val mainRoom:MainRoom? = mainContext.mainRoomCollector.rooms["E54N39"]
//    if (mainRoom != null) {
//        for (record in mainRoom.structureLabSort)
//            console.log("${record.key}   : ${record.value}")
//    }

//    val mainRoom: MainRoom? = mainContext.mainRoomCollector.rooms["W4N3"]
//    if (mainRoom != null) {
//        val slaveRoom: SlaveRoom? = mainRoom.slaveRooms["W4N4"]
//        if (slaveRoom != null) {
//            for (record in slaveRoom.source)
//                console.log("${record.key}   : ${record.value}")
//
//            for (record in slaveRoom.rescueFlag)
//                console.log("${record.key}   : ${record.value}")
//        }
//
//    }


    //console.log("${(233 / 100 * 100)}")
    //mainContext.battleGroupContainer.addTestGroup("E54N36")
    //mainContext.battleGroupContainer.deleteBattleGroup("testRoomE54N36")
//    mainContext.battleGroupContainer.deleteBattleGroup("2")
//    val battleGroup = mainContext.battleGroupContainer.battleGroupContainer["testRoomE54N36"]
//    if (battleGroup != null) {
//        mainContext.messenger("INFO",battleGroup.name,"Step: ${battleGroup.constants.step}", COLOR_YELLOW)
//    }
}
