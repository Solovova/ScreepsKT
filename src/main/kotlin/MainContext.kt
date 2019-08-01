import screeps.api.Memory
import screeps.api.get

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
        this.mainRooms.buildCreeps()
    }

    fun runInEndOfTick() {
        this.tasks.toMemory()
    }
}