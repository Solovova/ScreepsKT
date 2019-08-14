package mainContext

import Tasks
import battleGroup.BattleGroupContainer
import constants.Constants
import mainRoomCollector.MainRoomCollector
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
    val mineralInfo: MutableMap<ResourceConstant, MineralInfo> = mutableMapOf()

    init {
        this.constants.fromMemory()
        this.battleGroupContainer = BattleGroupContainer(this)
        this.runInStartOfTick()


        //this.directControlTaskClearInRoom("E57N34")
    }

    fun runInStartOfTick() {
        this.mainRoomCollector = MainRoomCollector(this, this.constants.mainRooms)
        this.mainRoomCollector.runInStartOfTick()
    }

    fun runNotEveryTick() {
        this.mainRoomCollector.runNotEveryTick()
        this.tasks.deleteTaskDiedCreep()
    }

    fun runInEndOfTick() {
        this.initOnThisTick = false
        this.mainRoomCollector.runInEndOfTick()

        this.tasks.toMemory()
        this.constants.toMemory()
        this.messengerShow()
    }
}