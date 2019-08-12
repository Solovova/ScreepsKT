package mainRoomCollector

import mainContext.MainContext
import constants.MainRoomConstant
import slaveRoom.SlaveRoom
import mainRoom
import mainRoom.MainRoom
import role
import screeps.api.*
import screeps.utils.toMap
import screeps.utils.unsafe.delete
import slaveRoom

class MainRoomCollector(val parent: MainContext, names: Array<String>) {
    val rooms: MutableMap<String, MainRoom> = mutableMapOf()

    init {
        names.forEachIndexed { index, name ->
            val mainRoomConstant: MainRoomConstant? = this.parent.constants.mainRoomConstantContainer[name]
            if (mainRoomConstant != null)
                rooms[name] = MainRoom(this, name, "M${index.toString().padStart(2, '0')}", mainRoomConstant)
            else parent.messenger("ERROR", name, "initialization don't see mainRoomConstant", COLOR_RED)
        }
    }

    private fun creepsCalculate() {
        for (creep in Game.creeps.values) {
            if (creep.carry.toMap().map { it.value }.sum() == 0
                    && creep.ticksToLive < 100
                    && creep.memory.role < 1000
                    && creep.memory.role != 14) creep.suicide()

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

        //clear
        try {
            for (key in js("Object").keys(Memory["profitCreep"]).unsafeCast<Array<String>>())
                if (Game.getObjectById<Creep>(key) == null)
                    delete(Memory["profitCreep"][key])
        } catch (e: Exception) {
            parent.messenger("ERROR", "Clear in creep profit", "", COLOR_RED)
        }
    }

    fun runInStartOfTick() {
        this.creepsCalculate()
        this.creepsCalculateProfit()
        for (room in rooms.values) {
            try {
                room.runInStartOfTick()
            }catch (e: Exception) {
                parent.messenger("ERROR", "Room in start of tick", room.name, COLOR_RED)
            }
        }
        this.terminalCalculate()
    }

    fun runNotEveryTick() {
        for (record in this.rooms) {
            try {
                record.value.runNotEveryTick()
            }catch (e: Exception) {
                parent.messenger("ERROR", "Room noe every tick", record.value.room.name, COLOR_RED)
            }
        }
    }
}