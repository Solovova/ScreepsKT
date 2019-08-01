import screeps.api.*

class MainContext {
    var mainRooms: MainRooms
    val tasks: Tasks

    init {
        this.mainRooms = MainRooms(Memory["MRoom"] as Array<String>)
        this.tasks = Tasks()
        this.tasks.fromMemory()
    }

    fun runInStartOfTick() {
        this.mainRooms = MainRooms(Memory["MRoom"] as Array<String>)
        this.tasks.deleteTaskDiedCreep()
        this.mainRooms.buildCreeps()
        for (creep in Game.creeps.values) creep.newTask(this)
    }

    fun runInEndOfTick() {
        for (creep in Game.creeps.values) creep.doTask(this)
        this.tasks.toMemory()
    }
}