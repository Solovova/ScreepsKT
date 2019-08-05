package mainRoom

import MainContext
import constants.MainRoomConstant
import slaveRoom.SlaveRoom
import mainRoom
import messenger
import role
import screeps.api.*
import screeps.utils.toMap
import slaveRoom

class MainRoomCollector(val parent: MainContext, names: Array<String>) {
    val rooms: MutableMap<String, MainRoom> = mutableMapOf()

    init {
        names.forEachIndexed { index, name ->
            val mainRoomConstant: MainRoomConstant? = this.parent.constants.mainRoomConstantContainer[name]
            if (mainRoomConstant != null)
                rooms[name] = MainRoom(this, name, "M$index", mainRoomConstant)
            else messenger("ERROR", name, "initialization don't see mainRoomConstant", COLOR_RED)
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
        for (room in rooms.values) room.building()
    }

    fun runInStartOfTick() {
        this.buildCreeps()
        this.build()
        for (room in rooms.values) room.runTower()
    }
}