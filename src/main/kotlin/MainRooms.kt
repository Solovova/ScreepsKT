import screeps.api.*
import screeps.utils.toMap

class MainRooms(names: Array<String>) {
    val rooms: MutableMap<String, MainRoom> = mutableMapOf()

    init {
        names.forEachIndexed { index, name ->
            if (Game.rooms[name] == null) messenger("ERROR", name, "Not room M$index", COLOR_RED)
            else {
                var slaveRoomsName: Array<String> = arrayOf()
                if (Memory["mainRoomsData"] != null && Memory["mainRoomsData"][name] != null && Memory["mainRoomsData"][name]["slaveRooms"] != null)
                    slaveRoomsName = Memory["mainRoomsData"][name]["slaveRooms"] as Array<String>
                rooms[name] = MainRoom(name, "M$index", slaveRoomsName)
            }
        }
    }

    private fun creepsCalculate() {
        for (creep in Game.creeps.values) {
            if (creep.carry.toMap().map { it.value }.sum() == 0 && creep.ticksToLive < 100) creep.suicide()

            // Main rooms
            if (creep.memory.role in 0..99) {
                val mainRoom: MainRoom = this.rooms[creep.memory.mainRoom] ?: continue
                mainRoom.have[creep.memory.role]++
            }

            // Slave rooms
            if (creep.memory.role in 100..199) {
                val mainRoom: MainRoom = this.rooms[creep.memory.mainRoom] ?: continue
                val slaveRoom: SlaveRoom = mainRoom.slaveRooms[creep.memory.slaveRoom] ?: continue
                slaveRoom.have[creep.memory.role-100]++
            }
        }
    }

    private fun buildCreeps() {
        this.creepsCalculate()
        for (room in rooms.values) room.buildCreeps()
    }

    private fun build() {
        this.creepsCalculate()
        for (room in rooms.values) room.building()
    }

    fun runInStartOfTick() {
        this.buildCreeps()
        this.build()
        for (room in rooms.values) room.runTower()
    }
}