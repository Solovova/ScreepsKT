package mainRoomCollector

import mainContext.MainContext
import constants.MainRoomConstant
import creep.doTask
import creep.newTask
import mainContext.messenger
import slaveRoom.SlaveRoom
import mainRoom
import mainRoom.MainRoom
import role
import screeps.api.*
import screeps.utils.isEmpty
import screeps.utils.toMap
import screeps.utils.unsafe.delete
import slaveRoom
import tickDeath
import upgrade
import upgradeQuantity
import upgradeResource
import kotlin.math.roundToInt

class MainRoomCollector(val parent: MainContext, names: Array<String>) {
    val rooms: MutableMap<String, MainRoom> = mutableMapOf()
    val flags = Game.flags.toMap().values.toList()

    init {
        names.forEachIndexed { index, name ->
            val mainRoomConstant: MainRoomConstant? = this.parent.constants.mainRoomConstantContainer[name]
            if (mainRoomConstant != null && Game.rooms[name] != null && (Game.rooms[name]?.controller?.my == true))
                rooms[name] = MainRoom(this, name, "M${index.toString().padStart(2, '0')}", mainRoomConstant)
            else parent.messenger("ERROR", name, "initialization don't see mainRoomConstant", COLOR_RED)
        }
    }

    private fun creepsCalculate() {
        for (creep in Game.creeps.values) {
            if (creep.memory.tickDeath != 0
                    && creep.ticksToLive < creep.memory.tickDeath
                    && creep.carry.toMap().map { it.value }.sum() == 0
            ) creep.suicide()

            // Main rooms
            if (creep.memory.role in 0..99) {
                val mainRoom: MainRoom = this.rooms[creep.memory.mainRoom] ?: continue
                mainRoom.have[creep.memory.role]++

                if (creep.memory.role == 10) mainRoom.constant.creepIdOfBigBuilder = creep.id
                //Upgrade
                //ToDo can be more then one upgrade, not only then spawn
                if (creep.spawning && creep.memory.upgrade == "") {
                    if (mainRoom.constant.creepUpgradeRole[creep.memory.role] == true) {
                        var upgradeParts = mainRoom.constant.creepUpgradableParts[creep.memory.role]
                        if (upgradeParts == null) upgradeParts = parent.constants.globalConstant.creepUpgradableParts[creep.memory.role]
                        if (upgradeParts != null) {
                            for (upgradePart in upgradeParts) {
                                val quantityParts: Int = creep.body.filter { it.type == upgradePart.key }.size
                                if (quantityParts != 0) {
                                    creep.memory.upgradeResource = upgradePart.value.unsafeCast<String>()
                                    creep.memory.upgradeQuantity = quantityParts * 30
                                    creep.memory.upgrade = "w"
                                    break
                                }
                            }
                        } else creep.memory.upgrade = "u"

                    } else creep.memory.upgrade = "u"
                }

                if (creep.memory.upgrade == "w" && mainRoom.creepNeedUpgradeID == "") {
                    mainRoom.creepNeedUpgradeID = creep.id
                    mainRoom.creepNeedUpgradeResource = creep.memory.upgradeResource.unsafeCast<ResourceConstant>()
                    mainRoom.creepNeedUpgradeResourceQuantity = creep.memory.upgradeQuantity
                    //after this
                    //LabFiller fill need Resource in Lab2
                    //Lab reaction in Lab2 stop
                    //creep have 1 task go to lab and upgrade and if OK write "u" to creep.memory.upgrade
                }
            }

            // Slave rooms
            if (creep.memory.role in 100..199) {
                val mainRoom: MainRoom = this.rooms[creep.memory.mainRoom] ?: continue
                val slaveRoom: SlaveRoom = mainRoom.slaveRooms[creep.memory.slaveRoom] ?: continue



                slaveRoom.have[creep.memory.role - 100]++

                if (!slaveRoom.constant.roomHostile
                        && (creep.memory.role == 107 || creep.memory.role == 105)
                        && creep.hits < creep.hitsMax) creep.suicide()

                if (creep.memory.role == 126) slaveRoom.constant.creepIdMineralHarvester = creep.id
            }

            // Logist add transfer
            if (creep.memory.role == 14 || creep.memory.role == 1014) {
                val mainRoom: MainRoom = this.rooms[creep.memory.mainRoom] ?: continue
                for (res in creep.carry.toMap()) mainRoom.resStorage[res.key] = (mainRoom.resStorage[res.key] ?: 0) + res.value
            }


        }

        //Add resource upgrade
        for (mainRoom in this.rooms.values) {
            if (mainRoom.creepNeedUpgradeID == "") continue
            val resource: ResourceConstant = mainRoom.creepNeedUpgradeResource.unsafeCast<ResourceConstant>()
            var resourceQuantityAllLabFiller = 0
            val creepsLabFiller = Game.creeps.toMap().filter { (it.value.memory.role == 18 || it.value.memory.role == 1018)
                    && it.value.memory.mainRoom == mainRoom.name}
            for (creep in creepsLabFiller) resourceQuantityAllLabFiller += creep.value.carry[resource] ?: 0

            val lab = mainRoom.structureLabSort[2]
            val resourceQuantityLab2 = if (lab != null && lab.mineralType == resource) lab.mineralAmount else 0

            //console.log("Test $resourceQuantityAllLabFiller $resourceQuantityLab2")

            mainRoom.resTerminal[resource] = (mainRoom.resTerminal[resource] ?: 0) + resourceQuantityLab2 + resourceQuantityAllLabFiller
        }
    }

    private fun creepsCalculateProfit() {
        if (Memory["profit"] == null) Memory["profit"] = object {}

        for (creep in Game.creeps.values) {
            if (creep.memory.role in arrayOf(106, 1006, 108, 1008, 121, 123, 125, 1121, 1123, 1125)) {
                val mainRoom: MainRoom = this.rooms[creep.memory.mainRoom] ?: continue
                val slaveRoom: SlaveRoom = mainRoom.slaveRooms[creep.memory.slaveRoom] ?: continue

                val carry: Int = creep.carry.energy
                var oldCarry = 0
                if (Memory["profit"][creep.id] != null)
                    oldCarry = Memory["profit"][creep.id] as Int

                if ((carry - oldCarry) > 2) slaveRoom.profitPlus(carry - oldCarry)
                Memory["profit"][creep.id] = carry
            }
        }

        //clear
        try {
            for (key in js("Object").keys(Memory["profit"]).unsafeCast<Array<String>>())
                if (Game.getObjectById<Creep>(key) == null)
                    delete(Memory["profit"][key])
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
        for (room in rooms.values) {
            try {
                room.runInEndOfTick()
            } catch (e: Exception) {
                parent.messenger("ERROR", "Room in end of tick", room.name, COLOR_RED)
            }
        }

        this.runTerminalsTransfer()

        val cpuStartCreeps = Game.cpu.getUsed()
        for (creep in Game.creeps.values) {
            try {
                creep.newTask(this.parent)
            } catch (e: Exception) {
                parent.messenger("ERROR", "CREEP New task", "${creep.memory.mainRoom} ${creep.memory.slaveRoom} ${creep.memory.role} ${creep.id}", COLOR_RED)
            }

            try {
                creep.doTask(this.parent)
            } catch (e: Exception) {
                parent.messenger("ERROR", "CREEP Do task", "${creep.memory.mainRoom} ${creep.memory.slaveRoom} ${creep.memory.role} ${creep.id}", COLOR_RED)
            }
        }

        Memory["CPUCreep"] = (Game.cpu.getUsed() - cpuStartCreeps).roundToInt()
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