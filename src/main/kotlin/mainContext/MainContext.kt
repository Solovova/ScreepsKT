package mainContext

import Tasks
import constants.Constants
import creep.doTask
import creep.newTask
import mainRoom
import mainRoomCollector.MainRoomCollector
import role
import screeps.api.*
import screeps.utils.isEmpty
import screeps.utils.unsafe.delete
import slaveRoom

//mainContext.MainContext initial only then died
//in start of tick initial mainRoomCollector
//in start of tick initial Constant and assign it need place

class MainContext {
    var mainRoomCollector: MainRoomCollector = MainRoomCollector(this, arrayOf())
    val tasks: Tasks = Tasks(this)
    val constants: Constants = Constants(this)
    var initOnThisTick: Boolean = true
    val messengerMap : MutableMap<String,String> = mutableMapOf()

    init {
        this.constants.fromMemory()
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