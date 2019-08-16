package mainContext

import Tasks
import battleGroup.BattleGroupContainer
import constants.Constants
import mainRoomCollector.MainRoomCollector
import productionController.ProductionController
import screeps.api.ResourceConstant

class MainContext {
    val messengerMap : MutableMap<String,String> = mutableMapOf()
    val constants: Constants = Constants(this)
    val tasks: Tasks = Tasks(this)
    var mainRoomCollector: MainRoomCollector = MainRoomCollector(this, arrayOf())
    private val battleGroupContainer: BattleGroupContainer = BattleGroupContainer(this)
    private val productionController: ProductionController = ProductionController(this)

    fun runInStartOfTick() {
        this.mainRoomCollector = MainRoomCollector(this,this.constants.mainRoomsInit)
        this.productionController.runInStartOfTick()
        this.mainRoomCollector.runInStartOfTick()
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