package mainContext

import Tasks
import battleGroup.BattleGroupContainer
import constants.Constants
import mainRoomCollector.MainRoomCollector
import mainRoomCollector.infoShow
import screeps.api.*

class MainContext {
    val messengerMap : MutableMap<String,String> = mutableMapOf()
    val mineralData: MutableMap<ResourceConstant, MineralDataRecord> = mutableMapOf()
    val constants: Constants = Constants(this)
    val tasks: Tasks = Tasks(this)
    var mainRoomCollector: MainRoomCollector = MainRoomCollector(this, arrayOf())
    private val battleGroupContainer: BattleGroupContainer = BattleGroupContainer(this)

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

        //this.marketShowBuyOrdersRealPrice("XGHO2".unsafeCast<ResourceConstant>())
        //this.marketShowSellOrdersRealPrice("XGHO2".unsafeCast<ResourceConstant>())
        //Game.market.createOrder(ORDER_SELL,"GH2O".unsafeCast<ResourceConstant>(),1.995,10000,"E54N39")
        //val result = Game.market.deal("5d4b64f5e4c2aa66fdcc1fdd",10000,"E54N37")
//        console.log("Trade: $result")
        //this.directControlTaskClearInRoom("E54N39")
    }

    fun runInStartOfTick() {
        this.mainRoomCollector = MainRoomCollector(this,this.constants.mainRoomsInit)
        this.mainRoomCollector.runInStartOfTick()
        this.mineralDataFill()
        this.mineralProductionFill()
        this.battleGroupContainer.runInStartOfTick()
    }

    fun runNotEveryTick() {
        this.mainRoomCollector.runNotEveryTick()
        this.tasks.deleteTaskDiedCreep()
        this.battleGroupContainer.runNotEveryTick()
    }

    fun runInEndOfTick() {
        this.battleGroupContainer.runInEndOfTick()
        this.mainRoomCollector.runInEndOfTick()
        this.tasks.toMemory()
        this.constants.toMemory()
        this.mineralInfoShow()
        this.messengerShow()
        this.mainRoomCollector.infoShow()
    }
}