package creep

import mainRoom.MainRoom
import slaveRoom.SlaveRoom
import kotlin.random.Random
import mainContext.MainContext
import CreepTask
import TypeOfTask
import screeps.api.*
import screeps.api.structures.*
import screeps.utils.toMap
import slaveRoom

fun Creep.takeFromStorage(creepCarry: Int, mainContext: MainContext, mainRoom: MainRoom): Boolean {
    var result = false
    if (creepCarry == 0) {
        val tStorage: StructureStorage? = mainRoom.structureStorage[0]
        if (tStorage!=null && mainRoom.getEnergyInStorage() > 0) {
            mainContext.tasks.add(this.id, CreepTask(TypeOfTask.Take, idObject0 = tStorage.id, posObject0 = tStorage.pos, resource = RESOURCE_ENERGY))
            result = true
        }
    }
    return result
}

fun Creep.transferToStorage(creepCarry: Int, mainContext: MainContext, mainRoom: MainRoom): Boolean {
    var result = false
    if (creepCarry != 0) {
        val tStorage: StructureStorage? = mainRoom.structureStorage[0]
        if (tStorage!=null) {
            mainContext.tasks.add(this.id, CreepTask(TypeOfTask.TransferTo, idObject0 = tStorage.id, posObject0 = tStorage.pos, resource = RESOURCE_ENERGY))
            result = true
        }
    }
    return result
}

fun Creep.harvestFromSource(type: Int, creepCarry: Int, mainContext: MainContext, mainRoom: MainRoom): Boolean {
    //0 - Source 0, 1 - Source 1, 2 - free source, 3 - random source
    var result = false
    if (creepCarry == 0) {
        var tSource: Source? = null
        when(type) {
            0 -> tSource = mainRoom.source[0]
            1 -> tSource = mainRoom.source[1]
            2 -> tSource = mainRoom.getSourceForHarvest(this.pos, mainContext)
        }
        if (tSource != null) {
            mainContext.tasks.add(this.id, CreepTask(TypeOfTask.Harvest, idObject0 = tSource.id, posObject0 = tSource.pos))
            result = true
        }
    }
    return result
}

fun Creep.transferToFilling(creepCarry: Int, mainContext: MainContext, mainRoom: MainRoom): Boolean {
    var result = false
    if (creepCarry > 0) {
        val objForFilling: Structure? = mainRoom.getSpawnOrExtensionForFiling(this.pos, mainContext)
        if (objForFilling != null) {
            mainContext.tasks.add(this.id, CreepTask(TypeOfTask.TransferTo, idObject0 = objForFilling.id, posObject0 = objForFilling.pos))
            result = true
        }
    }
    return result
}

fun Creep.buildStructure(creepCarry: Int, mainContext: MainContext, mainRoom: MainRoom): Boolean {
    var result = false
    if (creepCarry > 0 && mainRoom.constructionSite.isNotEmpty()) {
        val tConstructionSite = mainRoom.getConstructionSite(this.pos)
        if (tConstructionSite != null) {
            mainContext.tasks.add(this.id, CreepTask(TypeOfTask.Build, idObject0 = tConstructionSite.id, posObject0 = tConstructionSite.pos))
            result = true
        }
    }
    return result
}

fun Creep.upgradeNormalOrEmergency(type: Int, creepCarry: Int, mainContext: MainContext, mainRoom: MainRoom): Boolean {
    //0 - normal, 1 - emergency
    var result = false
    if (type == 0) {
        if (creepCarry > 0) {
            val structureController: StructureController? = mainRoom.structureController[0]
            if (structureController != null)
                mainContext.tasks.add(this.id, CreepTask(TypeOfTask.Upgrade, idObject0 = structureController.id, posObject0 = structureController.pos))
            result = true
        }
    }else{
        if (creepCarry > 0) {
            val structureController: StructureController? = mainRoom.structureController[0]
            if (structureController != null && (structureController.level < 2 || structureController.ticksToDowngrade < 1000)) {
                mainContext.tasks.add(this.id, CreepTask(TypeOfTask.Upgrade, idObject0 = structureController.id, posObject0 = structureController.pos))
                result = true
            }
        }
    }

    return result
}

fun Creep.transferToContainer(type: Int, creepCarry: Int, mainContext: MainContext, mainRoom: MainRoom): Boolean {
    // 0 - Source 0, 1 - Source 1, 2 - Controller
    var result = false
    if (creepCarry > 0) {
        var objForFilling: StructureContainer? = null
        when(type) {
            0 -> objForFilling = mainRoom.structureContainerNearSource[0]
            1 -> objForFilling = mainRoom.structureContainerNearSource[1]
            2 -> objForFilling = mainRoom.structureContainerNearController[0]
        }


        if (objForFilling != null) {
            //val canStore: Int = objForFilling.storeCapacity - objForFilling.store.toMap().map { it.value }.sum()
            //if (creepCarry > canStore) return false
            mainContext.tasks.add(this.id, CreepTask(TypeOfTask.TransferTo, idObject0 = objForFilling.id, posObject0 = objForFilling.pos))
            result = true
        }
    }
    return result
}

fun Creep.takeFromContainer(type: Int, creepCarry: Int, mainContext: MainContext, mainRoom: MainRoom): Boolean {
    // 0 - Source 0, 1 - Source 1, 2 - Controller, 3 - any container
    var result = false
    if (creepCarry == 0) {
        var objForFilling: Structure? = null
        when (type) {
            0 -> objForFilling = mainRoom.structureContainerNearSource[0]
            1 -> objForFilling = mainRoom.structureContainerNearSource[1]
            2 -> objForFilling = mainRoom.structureContainerNearController[0]
            3 -> objForFilling = mainRoom.structureContainer.values.filter { it.store.energy != 0 }.maxBy { it.store.energy}
        }
        if (objForFilling != null) {
            mainContext.tasks.add(this.id, CreepTask(TypeOfTask.Take, idObject0 = objForFilling.id, posObject0 = objForFilling.pos))
            result = true
        }
    }
    return result
}

fun Creep.signRoom(mainContext: MainContext, mainRoom: MainRoom): Boolean {
    var result = false
    val structureController: StructureController? = mainRoom.structureController[0]
    if (structureController != null) {
        val sign = structureController.sign
        var needSign = false
        if (sign != null && sign.text != mainRoom.describe) needSign = true
        if (sign == null) needSign = true

        if (needSign){
            mainContext.tasks.add(this.id, CreepTask(TypeOfTask.SignRoom, idObject0 = structureController.id, posObject0 = structureController.pos))
            result = true
        }
    }
    return result
}

fun Creep.slaveSignRoom(mainContext: MainContext, slaveRoom: SlaveRoom?): Boolean {
    var result = false
    if (slaveRoom != null) {
        val structureController: StructureController? = slaveRoom.structureController[0]
        if (structureController != null) {
            val sign = structureController.sign
            var needSign = false
            if (sign != null && sign.text != slaveRoom.describe) needSign = true
            if (sign == null) needSign = true

            if (needSign){
                mainContext.tasks.add(this.id, CreepTask(TypeOfTask.SignSlaveRoom, idObject0 = structureController.id, posObject0 = structureController.pos))
                result = true
            }
        }
    }

    return result
}

fun Creep.takeDroppedEnergy(creepCarry: Int, mainContext: MainContext, mainRoom: MainRoom): Boolean {
    var result = false
    if (creepCarry == 0) {
        val objDroppedEnergy: Resource? = mainRoom.room.find(FIND_DROPPED_ENERGY).minBy { this.pos.getRangeTo(it.pos) }
        if (objDroppedEnergy != null) {
            mainContext.tasks.add(this.id, CreepTask(TypeOfTask.TakeDropped, idObject0 = objDroppedEnergy.id, posObject0 = objDroppedEnergy.pos))
            result = true
        }
    }
    return result
}

fun Creep.slaveTakeFromContainer(type: Int, creepCarry: Int, mainContext: MainContext, mainRoom: MainRoom, slaveRoom: SlaveRoom?): Boolean {
    var result = false
    if (creepCarry == 0 && slaveRoom != null) {
        var objForFilling: Structure? = null
        when (type) {
            0 -> objForFilling = slaveRoom.structureContainerNearSource[0]
            1 -> objForFilling = slaveRoom.structureContainerNearSource[1]
            4 -> objForFilling = slaveRoom.structureContainer.values.filter { it.store.energy > 0}.minBy { this.pos.getRangeTo(it)}
        }
        if (objForFilling != null) {
            mainContext.tasks.add(this.id, CreepTask(TypeOfTask.Take, idObject0 = objForFilling.id, posObject0 = objForFilling.pos))
            result = true
        }
    }
    return result
}

fun Creep.slaveGoToRoom(mainContext: MainContext): Boolean {
    var result = false
    if (this.room.name != this.memory.slaveRoom) {
        mainContext.tasks.add(this.id, CreepTask(TypeOfTask.GoToRoom, idObject0 = this.memory.slaveRoom, posObject0 = RoomPosition(25, 25, this.memory.slaveRoom)))
        result = true
    }
    return result
}

fun Creep.goToPoint(mainContext: MainContext, pos: RoomPosition): Boolean {
    var result = false
    if (this.pos != pos) {
        mainContext.tasks.add(this.id, CreepTask(TypeOfTask.GoToPos, idObject0 = this.memory.slaveRoom, posObject0 = pos))
        result = true
    }
    return result
}



fun Creep.slaveClaim(mainContext: MainContext, slaveRoom: SlaveRoom?): Boolean {
    var result = false
    if (slaveRoom != null) {
        val structureController: StructureController? = slaveRoom.structureController[0]
        if (structureController != null && !structureController.my) {
            mainContext.tasks.add(this.id, CreepTask(TypeOfTask.Claim, idObject0 = structureController.id, posObject0 = structureController.pos))
            result = true
        }
    }
    return result
}

fun Creep.slaveReserve(mainContext: MainContext, slaveRoom: SlaveRoom?): Boolean {
    var result = false
    if (slaveRoom != null) {
        val structureController: StructureController? = slaveRoom.structureController[0]
        if (structureController != null && !structureController.my) {
            mainContext.tasks.add(this.id, CreepTask(TypeOfTask.Reserve, idObject0 = structureController.id, posObject0 = structureController.pos))
            result = true
        }
    }
    return result
}

fun Creep.slaveGoToPosOfContainer(type: Int, mainContext: MainContext, slaveRoom: SlaveRoom?): Boolean {
    //type 0 - source 0, 1 - source 1, 2 - random
    var result = false
    if (slaveRoom != null) {
        val tContainer: StructureContainer? = slaveRoom.structureContainerNearSource[type]
        if (tContainer != null) {
            if (!this.pos.inRangeTo(tContainer.pos,0)) {
                mainContext.tasks.add(this.id, CreepTask(TypeOfTask.GoToPos, idObject0 = tContainer.id, posObject0 = tContainer.pos))
                result = true
            }
        }
    }
    return result
}

fun Creep.slaveHarvest(type: Int, creepCarry: Int, mainContext: MainContext, slaveRoom: SlaveRoom?): Boolean {
    //type 0 - source 0, 1 - source 1, 2 - random
    var result = false
    if (slaveRoom != null) {
        if (creepCarry == 0) {


            var tSource: Source? = null
            when (type) {
                0 -> tSource = slaveRoom.source[0]
                1 -> tSource = slaveRoom.source[1]
                2 -> tSource = slaveRoom.source[Random.nextInt(slaveRoom.source.size)]
            }


            if (slaveRoom.name == "E57N34")  tSource = Game.getObjectById("59bbc5a12052a716c3ce9d1b") //ToDo костиль
            if (slaveRoom.name == "E51N33")  tSource = Game.getObjectById("59bbc52e2052a716c3ce91c0") //ToDo костиль

            if (tSource!=null) {
                mainContext.tasks.add(this.id, CreepTask(TypeOfTask.Harvest, idObject0 = tSource.id, posObject0 = tSource.pos))
                result = true
            }
        }
    }
    return result
}

fun Creep.slaveUpgradeNormalOrEmergency(type: Int, creepCarry: Int, mainContext: MainContext, slaveRoom: SlaveRoom?): Boolean {
    var result = false
    if (type == 0) {
        if (creepCarry > 0) {
            if (slaveRoom != null) {
                val structureController: StructureController? = slaveRoom.structureController[0]
                if (structureController != null && (structureController.level < 2 || structureController.ticksToDowngrade < 1000)) {
                    mainContext.tasks.add(this.id, CreepTask(TypeOfTask.Upgrade, idObject0 = structureController.id, posObject0 = structureController.pos))
                    result = true
                }
            }
        }
    }else{
        if (slaveRoom != null) {
            if (creepCarry > 0) {
                val structureController: StructureController? = slaveRoom.structureController[0]
                if (structureController != null){
                    mainContext.tasks.add(this.id, CreepTask(TypeOfTask.Upgrade, idObject0 = structureController.id, posObject0 = structureController.pos))
                    result = true
                }
            }
        }
    }
    return result
}

fun Creep.slaveBuild(creepCarry: Int, mainContext: MainContext, slaveRoom: SlaveRoom?, range: Int = 100): Boolean {
    var result = false
    if (slaveRoom != null) {
        if (creepCarry > 0 && slaveRoom.constructionSite.isNotEmpty()) {
            val tConstructionSite = slaveRoom.getConstructionSite(this.pos)
            if (tConstructionSite != null)
                if (this.pos.inRangeTo(tConstructionSite.pos, range)){
                    mainContext.tasks.add(this.id, CreepTask(TypeOfTask.Build, idObject0 = tConstructionSite.id, posObject0 = tConstructionSite.pos))
                    result = true
                }
        }
    }
    return result
}

fun Creep.slaveTransferToStorageOrContainer(type: Int, creepCarry: Int, mainContext: MainContext, mainRoom: MainRoom, slaveRoom: SlaveRoom?): Boolean {
    //type 0 - cont 0, 1 - cont 1, 2 - any
    var result = false
    if (slaveRoom != null) {
        if (creepCarry > 0) {
            var objForFilling: Structure? =  null
            when (type) {
                0 -> objForFilling = slaveRoom.structureContainerNearSource[0]
                1 -> objForFilling = slaveRoom.structureContainerNearSource[1]
                2 -> {
                    objForFilling =  slaveRoom.structureStorage[0]
                    if (objForFilling == null) objForFilling = slaveRoom.structureContainer.values.firstOrNull()
                }
            }

            if (objForFilling != null) {
                mainContext.tasks.add(this.id, CreepTask(TypeOfTask.TransferTo, idObject0 = objForFilling.id, posObject0 = objForFilling.pos))
                result = true
            }
        }
    }
    return result
}

fun Creep.slaveRepairContainer(type: Int, creepCarry: Int, mainContext: MainContext, slaveRoom: SlaveRoom?): Boolean {
    var result = false
    if (slaveRoom != null) {
        if (creepCarry > 0) {
            var containerRepair: StructureContainer? = null
            when (type) {
                0 -> containerRepair = slaveRoom.structureContainerNearSource[0]
                1 -> containerRepair = slaveRoom.structureContainerNearSource[1]
            }

            if (containerRepair != null) {
                if (containerRepair.hits < (containerRepair.hitsMax - 10000)){
                    mainContext.tasks.add(this.id, CreepTask(TypeOfTask.Repair, idObject0 = containerRepair.id, posObject0 = containerRepair.pos))
                    result = true
                }
            }

        }
    }
    return result
}

fun Creep.slaveTransferToFilling(creepCarry: Int, mainContext: MainContext, mainRoom: MainRoom, slaveRoom: SlaveRoom?): Boolean {
    var result = false
    if (creepCarry > 0 && slaveRoom != null) {
        var objForFilling: Structure? = slaveRoom.room?.find(FIND_STRUCTURES)?.filter {
            it.structureType == STRUCTURE_EXTENSION
        }?.firstOrNull { (it as StructureExtension).energy < it.energyCapacity }

        if (objForFilling == null) objForFilling = slaveRoom.room?.find(FIND_STRUCTURES)?.filter {
            it.structureType == STRUCTURE_SPAWN
        }?.firstOrNull { (it as StructureSpawn).energy < it.energyCapacity }

        if (objForFilling == null) objForFilling = slaveRoom.room?.find(FIND_STRUCTURES)?.filter {
            it.structureType == STRUCTURE_TOWER
        }?.firstOrNull { (it as StructureTower).energy < it.energyCapacity }

        if (objForFilling != null) {
            mainContext.tasks.add(this.id, CreepTask(TypeOfTask.TransferTo, idObject0 = objForFilling.id, posObject0 = objForFilling.pos))
            result = true
        }
    }
    return result
}

fun Creep.slaveAttackRanged(mainContext: MainContext, slaveRoom: SlaveRoom?): Boolean {
    var result = false
    if (slaveRoom?.room != null) {
        val hostileCreep : Creep? =  slaveRoom.room.find(FIND_HOSTILE_CREEPS).firstOrNull()
        if (hostileCreep != null) {
            mainContext.tasks.add(this.id, CreepTask(TypeOfTask.AttackRange, idObject0 = hostileCreep.id, posObject0 = hostileCreep.pos))
            result = true
        }
    }
    return result
}

fun Creep.slaveAttack(mainContext: MainContext, slaveRoom: SlaveRoom?): Boolean {
    var result = false
    if (slaveRoom?.room != null) {
        val hostileCreep : Creep? =  slaveRoom.room.find(FIND_HOSTILE_CREEPS).firstOrNull()
        if (hostileCreep != null) {
            mainContext.tasks.add(this.id, CreepTask(TypeOfTask.AttackMile, idObject0 = hostileCreep.id, posObject0 = hostileCreep.pos))
            result = true
        }
    }
    return result
}
