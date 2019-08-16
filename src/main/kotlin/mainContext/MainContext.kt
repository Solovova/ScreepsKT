package mainContext

import Tasks
import battleGroup.BattleGroupContainer
import constants.Constants
import mainRoomCollector.MainRoomCollector
import productionController.ProductionController
import screeps.api.*

class MainContext {
    val messengerMap : MutableMap<String,String> = mutableMapOf()
    val constants: Constants = Constants(this)
    val tasks: Tasks = Tasks(this)
    var mainRoomCollector: MainRoomCollector = MainRoomCollector(this, arrayOf())
    private val battleGroupContainer: BattleGroupContainer = BattleGroupContainer(this)
    private val productionController: ProductionController = ProductionController(this)

    init {
//        RESOURCE_ENERGY: "energy",
//        RESOURCE_POWER: "power",
//
//        RESOURCE_HYDROGEN: "H",
//        RESOURCE_OXYGEN: "O",
//        RESOURCE_UTRIUM: "U",
//        RESOURCE_LEMERGIUM: "L",
//        RESOURCE_KEANIUM: "K",
//        RESOURCE_ZYNTHIUM: "Z",
//        RESOURCE_CATALYST: "X",
//        RESOURCE_GHODIUM: "G",
//
//        RESOURCE_HYDROXIDE: "OH",
//        RESOURCE_ZYNTHIUM_KEANITE: "ZK",
//        RESOURCE_UTRIUM_LEMERGITE: "UL",

//        this.marketShowBuyOrdersRealPrice(RESOURCE_KEANIUM)
//        this.marketShowSellOrdersRealPrice(RESOURCE_KEANIUM)
//        val result = Game.market.deal("5d53042649ede365f785b86d",10000,"E54N37")
//        console.log("Trade: $result")
    }

    fun runInStartOfTick() {
        this.mainRoomCollector = MainRoomCollector(this,this.constants.mainRoomsInit)
        this.mainRoomCollector.runInStartOfTick()
        this.productionController.runInStartOfTick()
    }

    fun runNotEveryTick() {
        this.mainRoomCollector.runNotEveryTick()
        this.tasks.deleteTaskDiedCreep()
        this.productionController.runNotEveryTick()
    }

    fun runInEndOfTick() {
        this.mainRoomCollector.runInEndOfTick()
        this.productionController.runInEndOfTick()

        this.tasks.toMemory()
        this.constants.toMemory()
        this.messengerShow()
    }
}