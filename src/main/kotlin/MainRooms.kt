import screeps.api.COLOR_RED
import screeps.api.Game
import screeps.api.get
import screeps.api.values
import screeps.utils.toMap

class MainRooms(names: Array<String>) {
    val rooms: MutableMap<String, MainRoom> = mutableMapOf()

    init {
        names.forEachIndexed { index, name ->
            if (Game.rooms[name] == null) messenger("ERROR", name, "Not room M$index", COLOR_RED)
            else rooms[name] = MainRoom(name, "M$index")
        }
    }

    private fun creepsCalculate() {
        for (creep in Game.creeps.values) {
            if (creep.carry.toMap().map { it.value }.sum() == 0 && creep.ticksToLive < 100) creep.suicide()

            // Main rooms
            if (creep.memory.role in 0..99) {
                val room: MainRoom = this.rooms[creep.memory.dstRoom] ?: continue
                room.have[creep.memory.role]++
            }
        }
    }

    fun buildCreeps() {
        this.creepsCalculate()
        for (room in rooms.values) room.buildCreeps()
    }
}