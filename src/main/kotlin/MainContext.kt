import constants.Constants
import creep.doTask
import creep.newTask
import dataCache.DataCache
import mainRoom.MainRoomCollector
import screeps.api.*
import screeps.utils.isEmpty
import screeps.utils.unsafe.delete

//MainContext initial only then died
//in start of tick initial mainRoomCollector
//in start of tick initial Constant and assign it need place

class MainContext {
    var mainRoomCollector: MainRoomCollector = MainRoomCollector(this, arrayOf())
    val tasks: Tasks
    val dataCache : DataCache
    val constants: Constants = Constants()
    var initOnThisTick: Boolean = true

    init {
        this.tasks = Tasks()
        this.dataCache = DataCache(this)

        //Only this  //ToDo run once, if we run in init don't run runInStartOfTick in future
        this.runInStartOfTick()
        this.constants.fromMemory()

    }

    fun runInStartOfTick() {
        this.mainRoomCollector = MainRoomCollector(this, this.constants.mainRooms)
        this.tasks.deleteTaskDiedCreep()
        this.houseKeeping()
        this.mainRoomCollector.runInStartOfTick()
        for (creep in Game.creeps.values) creep.newTask(this)
    }

    fun runInEndOfTick() {
        for (creep in Game.creeps.values) creep.doTask(this)
        this.tasks.toMemory()
        this.dataCache.toMemory()

        //only this
        this.initOnThisTick = false
        this.constants.toMemory()
    }

    private fun houseKeeping() {
        if (Game.creeps.isEmpty()) return
        for ((creepName, _) in Memory.creeps) {
            if (Game.creeps[creepName] == null) {
                delete(Memory.creeps[creepName])
            }
        }
    }
}