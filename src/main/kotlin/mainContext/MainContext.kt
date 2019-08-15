package mainContext

import Tasks
import battleGroup.BattleGroupContainer
import constants.Constants
import mainRoomCollector.MainRoomCollector
import productionController.ProductionController
import screeps.api.ResourceConstant

//mainContext.MainContext initial only then died
//in start of tick initial mainRoomCollector
//in start of tick initial Constant and assign it need place

class MainContext {
    var mainRoomCollector: MainRoomCollector = MainRoomCollector(this, arrayOf())
    val tasks: Tasks = Tasks(this)
    val constants: Constants = Constants(this)
    var initOnThisTick: Boolean = true
    val messengerMap : MutableMap<String,String> = mutableMapOf()
    val battleGroupContainer: BattleGroupContainer
    val productionController: ProductionController = ProductionController(this)


    init {
        this.constants.fromMemory()
        this.battleGroupContainer = BattleGroupContainer(this)
        this.runInStartOfTick()
    }

    fun runInStartOfTick() {
        this.mainRoomCollector = MainRoomCollector(this, this.constants.mainRooms)
        this.mainRoomCollector.runInStartOfTick()
        this.productionController.runInStartOfTick()
    }

    fun runNotEveryTick() {
        this.mainRoomCollector.runNotEveryTick()
        this.tasks.deleteTaskDiedCreep()
        this.productionController.runNotEveryTick()
    }

    fun runInEndOfTick() {
        this.initOnThisTick = false
        this.mainRoomCollector.runInEndOfTick()
        this.productionController.runInEndOfTick()

        this.tasks.toMemory()
        this.constants.toMemory()
        this.messengerShow()
    }
}