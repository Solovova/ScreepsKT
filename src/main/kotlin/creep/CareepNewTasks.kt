package creep

import mainRoom.MainRoom
import screeps.api.Creep
import screeps.api.RESOURCE_ENERGY
import screeps.api.RoomPosition
import screeps.api.Source
import screeps.api.structures.Structure
import screeps.api.structures.StructureController
import screeps.api.structures.StructureStorage
import slaveRoom.SlaveRoom
import kotlin.random.Random
import MainContext
import CreepTask
import TypeOfTask
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
        var objForFilling: Structure? = null
        when(type) {
            0 -> objForFilling = mainRoom.structureContainerNearSource[0]
            1 -> objForFilling = mainRoom.structureContainerNearSource[1]
            2 -> objForFilling = mainRoom.structureContainerNearController[0]
        }
        if (objForFilling != null) {
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

fun Creep.slaveGoToRoom(mainContext: MainContext): Boolean {
    var result = false
    if (this.room.name != this.memory.slaveRoom) {
        mainContext.tasks.add(this.id, CreepTask(TypeOfTask.GoToRoom, idObject0 = this.memory.slaveRoom, posObject0 = RoomPosition(25, 25, this.memory.slaveRoom)))
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

fun Creep.slaveHarvest(creepCarry: Int, mainContext: MainContext, slaveRoom: SlaveRoom?): Boolean {
    var result = false
    if (slaveRoom != null) {
        if (creepCarry == 0) {
            val tSource: Source? = slaveRoom.source[Random.nextInt(slaveRoom.source.size)]
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

fun Creep.slaveBuild(creepCarry: Int, mainContext: MainContext, slaveRoom: SlaveRoom?): Boolean {
    var result = false
    if (slaveRoom != null) {
        if (creepCarry > 0 && slaveRoom.constructionSite.isNotEmpty()) {
            val tConstructionSite = slaveRoom.getConstructionSite(this.pos)
            if (tConstructionSite != null) {
                mainContext.tasks.add(this.id, CreepTask(TypeOfTask.Build, idObject0 = tConstructionSite.id, posObject0 = tConstructionSite.pos))
                result = true
            }
        }
    }
    return result
}

fun Creep.slaveTransferToStorageOrContainer(creepCarry: Int, mainContext: MainContext, mainRoom: MainRoom, slaveRoom: SlaveRoom?): Boolean {
    var result = false
    if (slaveRoom != null) {
        if (creepCarry > 0) {
            var objForFilling: Structure? =  slaveRoom.structureStorage[0]
            if (objForFilling == null) objForFilling = slaveRoom.structureContainer.values.firstOrNull()
            if (objForFilling != null) {
                mainContext.tasks.add(this.id, CreepTask(TypeOfTask.TransferTo, idObject0 = objForFilling.id, posObject0 = objForFilling.pos))
                result = true
            }
        }
    }
    return result
}