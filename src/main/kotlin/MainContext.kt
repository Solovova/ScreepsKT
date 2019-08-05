import Tasks
import constants.Constants
import creep.doTask
import creep.newTask
import dataCache.DataCache
import mainRoom.MainRoomCollector
import screeps.api.*
import screeps.utils.isEmpty
import screeps.utils.unsafe.delete

class MainContext {
    var mainRoomCollector: MainRoomCollector
    val tasks: Tasks
    val dataCache : DataCache
    val constants: Constants = Constants()

    init {
        this.mainRoomCollector = MainRoomCollector(this, arrayOf())
        this.tasks = Tasks()
        this.dataCache = DataCache(this)
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