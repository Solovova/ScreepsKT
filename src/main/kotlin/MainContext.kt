import screeps.api.*
import screeps.utils.isEmpty
import screeps.utils.unsafe.delete

class MainContext {
    var mainRooms: MainRooms
    val tasks: Tasks
    val dataCache : DataCache

    init {
        this.mainRooms = MainRooms(this,arrayOf())
        this.tasks = Tasks()
        this.dataCache = DataCache(this)
    }

    fun runInStartOfTick() {
        this.mainRooms = MainRooms(this,Memory["mainRooms"] as Array<String>)
        this.tasks.deleteTaskDiedCreep()
        this.houseKeeping()
        this.mainRooms.runInStartOfTick()
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