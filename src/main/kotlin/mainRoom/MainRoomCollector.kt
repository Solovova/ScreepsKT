package mainRoom

import mainContext.MainContext
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
            if (creep.carry.toMap().map { it.value }.sum() == 0 && creep.ticksToLive < 100 &&
                    creep.memory.role < 1000) creep.suicide()

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

    private fun creepsCalculateProfit() {
        if (Memory["profitCreep"] == null) Memory["profitCreep"] = object {}

        for (creep in Game.creeps.values) {
            if (creep.memory.role == 106 || creep.memory.role == 108 || creep.memory.role == 1106 || creep.memory.role == 1108) {
                val mainRoom: MainRoom = this.rooms[creep.memory.mainRoom] ?: continue
                val slaveRoom: SlaveRoom = mainRoom.slaveRooms[creep.memory.slaveRoom] ?: continue

                val carry: Int = creep.carry.energy
                var oldCarry:Int = 0
                if (Memory["profitCreep"][creep.id] != null)
                    oldCarry = Memory["profitCreep"][creep.id] as Int

                if ((carry - oldCarry) > 2) slaveRoom.profitPlus(carry - oldCarry)
                Memory["profitCreep"][creep.id] = carry
            }
        }
    }

    private fun buildCreeps() {

        for (room in rooms.values) room.buildCreeps()
    }


    fun runInStartOfTick() {
        this.creepsCalculate()
        this.creepsCalculateProfit()
        for (room in rooms.values) room.runInStartOfTick()
    }

    fun runNotEveryTick() {
        for (record in this.rooms) record.value.runNotEveryTick()
    }
}