package mainRoomCollector

import mainContext.MainContext
import constants.MainRoomConstant
import creep.doTask
import creep.newTask
import mainContext.messenger
import slaveRoom.SlaveRoom
import mainRoom
import mainRoom.MainRoom
import mainRoom.logisticAddCarry
import role
import screeps.api.*
import screeps.utils.isEmpty
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
                    && (creep.memory.role == 106 || creep.memory.role == 108)) creep.suicide()

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

            // Logist add transfer
            if (creep.memory.role == 14 || creep.memory.role == 1014) {
                val mainRoom: MainRoom = this.rooms[creep.memory.mainRoom] ?: continue
                mainRoom.logisticAddCarry(creep)
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
                var oldCarry = 0
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
            } catch (e: Exception) {
                parent.messenger("ERROR", "Room in start of tick", room.name, COLOR_RED)
            }
        }
        this.terminalCalculate()

        for (creep in Game.creeps.values) {
            try {
                creep.newTask(this.parent)
            } catch (e: Exception) {
                parent.messenger("ERROR", "CREEP New task", "${creep.memory.mainRoom} ${creep.memory.slaveRoom} ${creep.memory.role} ${creep.id}", COLOR_RED)
            }
        }
    }

    fun runNotEveryTick() {
        for (record in this.rooms) {
            try {
                record.value.runNotEveryTick()
            } catch (e: Exception) {
                parent.messenger("ERROR", "Room not every tick", record.value.room.name, COLOR_RED)
            }
        }
        this.houseKeeping()
    }

    fun runInEndOfTick() {
        for (creep in Game.creeps.values) {
            try {
                creep.doTask(this.parent)
            } catch (e: Exception) {
                parent.messenger("ERROR", "CREEP Do task", "${creep.memory.mainRoom} ${creep.memory.slaveRoom} ${creep.memory.role} ${creep.id}", COLOR_RED)
            }
        }
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