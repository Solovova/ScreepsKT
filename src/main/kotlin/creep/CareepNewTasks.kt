package creep

import mainRoom.MainRoom
import slaveRoom.SlaveRoom
import kotlin.random.Random
import mainContext.MainContext
import CreepTask
import TypeOfTask
import role
import screeps.api.*
import screeps.api.Game.structures
import screeps.api.structures.*
import screeps.utils.toMap
import slaveRoom
import upgrade

fun Creep.takeFromStorage(creepCarry: Int, mainContext: MainContext, mainRoom: MainRoom): Boolean {
    var result = false
    if (creepCarry == 0) {
        val tStorage: StructureStorage? = mainRoom.structureStorage[0]
        if (tStorage != null && mainRoom.getResourceInStorage() > 0) {
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
        if (tStorage != null) {
            mainContext.tasks.add(this.id, CreepTask(TypeOfTask.TransferTo, idObject0 = tStorage.id, posObject0 = tStorage.pos, resource = RESOURCE_ENERGY))
            result = true
        }
    }
    return result
}

fun Creep.harvestFromSource(type: Int, creepCarry: Int, mainContext: MainContext, mainRoom: MainRoom): Boolean {
    //0 - Source 0, 1 - Source 1, 2 - free source, 3 - random source, 4 - mineral
    var result = false
    if (creepCarry == 0) {
        var tSource: Source? = null
        when (type) {
            0 -> tSource = mainRoom.source[0]
            1 -> tSource = mainRoom.source[1]
            2 -> tSource = mainRoom.getSourceForHarvest(this.pos, mainContext)
        }
        if (mainRoom.name == "E57N34" && this.memory.role == 0) tSource = Game.getObjectById("59bbc5a12052a716c3ce9d1b") //ToDo костиль
        if (tSource != null) {
            mainContext.tasks.add(this.id, CreepTask(TypeOfTask.Harvest, idObject0 = tSource.id, posObject0 = tSource.pos))
            result = true
        }
    }
    return result
}

fun Creep.harvestFromMineral(creepCarry: Int, mainContext: MainContext, mainRoom: MainRoom): Boolean {
    var result = false
    if (creepCarry == 0) {
        val extractor: StructureExtractor? = mainRoom.structureExtractor[0]
        val container: StructureContainer? = mainRoom.structureContainerNearMineral[0]
        if (extractor != null
                && container != null
                && extractor.cooldown == 0
                && container.store.toMap().map { it.value }.sum() < (container.storeCapacity - 30)) {
            mainContext.tasks.add(this.id, CreepTask(TypeOfTask.HarvestMineral, idObject0 = mainRoom.mineral.id, posObject0 = mainRoom.mineral.pos))
            result = true
        }
    }
    return result
}

fun Creep.slaveHarvestFromMineral(creepCarry: Int, mainContext: MainContext, slaveRoom: SlaveRoom?): Boolean {
    var result = false
    if (slaveRoom != null) {
        val extractor = slaveRoom.structureExtractor[0]
        val mineral = slaveRoom.mineral[0]
        if (extractor != null
                && mineral != null
                && extractor.cooldown == 0
                && creepCarry != this.carryCapacity) {
            mainContext.tasks.add(this.id, CreepTask(TypeOfTask.HarvestMineral, idObject0 = mineral.id, posObject0 = mineral.pos))
            result = true
        }
    }
    return result
}

fun Creep.transportMineralToStorage(mainContext: MainContext, mainRoom: MainRoom): Boolean {
    var result = false
    val container: StructureContainer? = mainRoom.structureContainerNearMineral[0]
    val storage: StructureStorage? = mainRoom.structureStorage[0]
    if (container != null && storage != null) {
        mainContext.tasks.add(this.id, CreepTask(TypeOfTask.Transport, idObject0 = container.id, posObject0 = container.pos, idObject1 = storage.id, posObject1 = storage.pos, resource = mainRoom.mineral.mineralType))
        result = true
    }
    return result
}

fun Creep.slaveTakeMineralFromMineralHarvester(creepCarry: Int, mainContext: MainContext, mainRoom: MainRoom, slaveRoom: SlaveRoom?): Boolean {
    var result = false
    if (slaveRoom != null) {
        if (creepCarry == 0) {
            val objForTake: Creep? = Game.getObjectById(slaveRoom.constant.creepIdMineralHarvester)
            val mineral = slaveRoom.mineral[0]
            if (objForTake != null && mineral != null) {
                mainContext.tasks.add(this.id, CreepTask(TypeOfTask.TransferFromCreep, idObject0 = objForTake.id, posObject0 = objForTake.pos, resource = mineral.mineralType))
                result = true
            }
        }
    }
    return result
}

fun Creep.slaveTransferMineralToStorage(creepCarry: Int, mainContext: MainContext, mainRoom: MainRoom, slaveRoom: SlaveRoom?): Boolean {
    var result = false
    if (slaveRoom != null && creepCarry > 0) {
        val storage: StructureStorage? = mainRoom.structureStorage[0]
        val mineral = slaveRoom.mineral[0]
        if (mineral != null && storage != null) {
            mainContext.tasks.add(this.id, CreepTask(TypeOfTask.TransferTo, idObject0 = storage.id, posObject0 = storage.pos, resource = mineral.mineralType))
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

fun Creep.buildBigStructure(mainContext: MainContext, mainRoom: MainRoom): Boolean {
    var result = false
    if (mainRoom.constructionSite.isNotEmpty()) {
        val tConstructionSite = mainRoom.getConstructionSite(this.pos)
        if (tConstructionSite != null) {
            mainContext.tasks.add(this.id, CreepTask(TypeOfTask.Build, idObject0 = tConstructionSite.id, posObject0 = tConstructionSite.pos))
            result = true
        }
    }
    if (!result) {
        var structure: Structure? = mainRoom.room.find(FIND_STRUCTURES).filter {
            (it.structureType == STRUCTURE_RAMPART || it.structureType == STRUCTURE_WALL)
                    && it.hits < mainRoom.constant.defenceHits
                    && it.pos.x != 49 && it.pos.x != 0 && it.pos.y != 49 && it.pos.y != 0
        }.minBy { it.hits + it.pos.getRangeTo(this.pos) * 30000 }

        if (structure == null) {
            structure = mainRoom.room.find(FIND_STRUCTURES).filter {
                ((it.structureType == STRUCTURE_RAMPART || it.structureType == STRUCTURE_WALL)
                        && it.pos.x != 49 && it.pos.x != 0 && it.pos.y != 49 && it.pos.y != 0)
            }.minBy { it.hits + it.pos.getRangeTo(this.pos) * 30000 }
        }

        if (structure != null) {
            mainContext.tasks.add(this.id, CreepTask(TypeOfTask.UpgradeStructure, idObject0 = structure.id, posObject0 = structure.pos, quantity = (structure.hits + 30000)))
            result = true
        }
    }
    return result
}

fun Creep.clean(creepCarry: Int, mainContext: MainContext, mainRoom: MainRoom): Boolean {
    var result = false
    if (creepCarry == 0) {
        var clean: ResourceConstant? = null
        var container: StructureContainer? = null

        if (clean == null) {
            clean = mainRoom.needCleanWhat(mainRoom.structureContainerNearSource[0]?.store, RESOURCE_ENERGY)
            if (clean != null) container = mainRoom.structureContainerNearSource[0]
        }

        if (clean == null) {
            clean = mainRoom.needCleanWhat(mainRoom.structureContainerNearSource[1]?.store, RESOURCE_ENERGY)
            if (clean != null) container = mainRoom.structureContainerNearSource[1]
        }
        if (clean == null) {
            clean = mainRoom.needCleanWhat(mainRoom.structureContainerNearSource[2]?.store, RESOURCE_ENERGY)
            if (clean != null) container = mainRoom.structureContainerNearSource[2]
        }
        if (clean == null) {
            clean = mainRoom.needCleanWhat(mainRoom.structureContainerNearController[0]?.store, RESOURCE_ENERGY)
            if (clean != null) container = mainRoom.structureContainerNearController[0]
        }
        if (clean == null) {
            clean = mainRoom.needCleanWhat(mainRoom.structureContainerNearMineral[0]?.store, mainRoom.mineral.mineralType)
            if (clean != null) container = mainRoom.structureContainerNearMineral[0]
        }

        if (clean != null && container != null) {
            mainContext.tasks.add(this.id, CreepTask(TypeOfTask.Take, idObject0 = container.id, posObject0 = container.pos, resource = clean))
            result = true
        }
    }
    return result
}

fun Creep.drop(creepCarry: Int, mainContext: MainContext): Boolean {
    var result = false
    if (creepCarry != 0) {
        var drop: ResourceConstant? = null
        for (record in this.carry.keys)
            if (this.carry[record] != 0) {
                drop = record
                break
            }

        if (drop != null) {
            mainContext.tasks.add(this.id, CreepTask(TypeOfTask.Drop, idObject0 = this.id, posObject0 = this.pos, resource = drop))
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
    } else {
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
        when (type) {
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

fun Creep.transferToBigBuilder(creepCarry: Int, mainContext: MainContext, mainRoom: MainRoom): Boolean {
    var result = false
    if (creepCarry > 0) {
        val objForFilling: Creep? = Game.getObjectById(mainRoom.constant.creepIdOfBigBuilder)
        if (objForFilling != null) {
            mainContext.tasks.add(this.id, CreepTask(TypeOfTask.TransferToCreep, idObject0 = objForFilling.id, posObject0 = objForFilling.pos))
            result = true
        }
    }
    return result
}

fun Creep.transferToLink(type: Int, creepCarry: Int, mainContext: MainContext, mainRoom: MainRoom): Boolean {
    // 0 - Source 0, 1 - Source 1
    var result = false
    if (creepCarry > 0) {
        var objForFilling: StructureLink? = null
        when (type) {
            0 -> objForFilling = mainRoom.structureLinkNearSource[0]
            1 -> objForFilling = mainRoom.structureLinkNearSource[1]
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
            3 -> objForFilling = mainRoom.structureContainer.values.filter { it.store.energy != 0 }.maxBy { it.store.energy }
        }
        if (objForFilling != null) {
            mainContext.tasks.add(this.id, CreepTask(TypeOfTask.Take, idObject0 = objForFilling.id, posObject0 = objForFilling.pos))
            result = true
        }
    }
    return result
}

fun Creep.upgradeCreep(mainContext: MainContext, mainRoom: MainRoom): Boolean {
    // 0 - Source 0, 1 - Source 1, 2 - Controller, 3 - any container
    var result = false
    if (this.memory.upgrade == "w") {
        val lab2: StructureLab? = mainRoom.structureLabSort[2]
        if (lab2 == null) {
            this.memory.upgrade = "u"
            return false
        }else{
            mainContext.tasks.add(this.id, CreepTask(TypeOfTask.UpgradeCreep, idObject0 = lab2.id, posObject0 = lab2.pos))
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

        if (needSign) {
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

            if (needSign) {
                mainContext.tasks.add(this.id, CreepTask(TypeOfTask.SignSlaveRoom, idObject0 = structureController.id, posObject0 = structureController.pos))
                result = true
            }
        }
    }

    return result
}

fun Creep.takeDroppedEnergy(creepCarry: Int, mainContext: MainContext, range: Int = 100): Boolean {
    var result = false
    if (creepCarry == 0) {
        val objDroppedEnergy: Resource? = this.room.find(FIND_DROPPED_RESOURCES).filter { it.resourceType == RESOURCE_ENERGY }.minBy { this.pos.getRangeTo(it.pos) }
        if (objDroppedEnergy != null && objDroppedEnergy.pos.inRangeTo(this.pos, range)) {
            mainContext.tasks.add(this.id, CreepTask(TypeOfTask.TakeDropped, idObject0 = objDroppedEnergy.id, posObject0 = objDroppedEnergy.pos))
            result = true
        }
    }
    return result
}

fun Creep.slaveTakeFromContainer(type: Int, creepCarry: Int, mainContext: MainContext, slaveRoom: SlaveRoom?): Boolean {
    var result = false
    if (creepCarry == 0 && slaveRoom != null) {
        var objForFilling: Structure? = null
        when (type) {
            0, 1, 2 -> objForFilling = slaveRoom.structureContainerNearSource[type]
            4 -> objForFilling = slaveRoom.structureContainer.values.filter { it.store.energy > 0 }.minBy { this.pos.getRangeTo(it) }
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

fun Creep.gotoPosOfContainer(type: Int, mainContext: MainContext, mainRoom: MainRoom): Boolean {
    //type 0 - source 0, 1 - source 1, 2 - mineral
    var result = false
    var tContainer: StructureContainer? = null
    when (type) {
        0 -> tContainer = mainRoom.structureContainerNearSource[0]
        1 -> tContainer = mainRoom.structureContainerNearSource[1]
        2 -> tContainer = mainRoom.structureContainerNearMineral[0]
    }

    if (tContainer != null) {
        if (!this.pos.inRangeTo(tContainer.pos, 0)) {
            mainContext.tasks.add(this.id, CreepTask(TypeOfTask.GoToPos, idObject0 = tContainer.id, posObject0 = tContainer.pos))
            result = true
        }
    }
    return result
}

fun Creep.slaveGoToPosOfMineral(mainContext: MainContext, slaveRoom: SlaveRoom?): Boolean {
    var result = false
    if (slaveRoom != null) {
        val mineral = slaveRoom.mineral[0]
        if (mineral != null) {
            if (!this.pos.inRangeTo(mineral.pos, 1)) {
                mainContext.tasks.add(this.id, CreepTask(TypeOfTask.GoToPos, idObject0 = mineral.id, posObject0 = mineral.pos, quantity = 1))
                result = true
            }
        }
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

fun Creep.slaveGoToRescueFlag(type: Int, mainContext: MainContext, slaveRoom: SlaveRoom?): Boolean {
    var result = false
    if (slaveRoom != null) {
        val keeperLair = slaveRoom.structureKeeperLair[type]
        if (keeperLair != null) {
            val hostileNear = this.room.find(FIND_HOSTILE_CREEPS).firstOrNull { keeperLair.pos.inRangeTo(it.pos, 10) }
            if ((keeperLair.ticksToSpawn < 20 || hostileNear != null)
                    && this.pos.inRangeTo(keeperLair.pos, 10)) {
                val flag: Flag? = slaveRoom.rescueFlag[type]
                if (flag != null) {
                    mainContext.tasks.add(this.id, CreepTask(TypeOfTask.GoToRescueFlag, idObject0 = flag.name, posObject0 = flag.pos))
                    result = true
                }
            }
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
            if (!this.pos.inRangeTo(tContainer.pos, 0)) {
                mainContext.tasks.add(this.id, CreepTask(TypeOfTask.GoToPos, idObject0 = tContainer.id, posObject0 = tContainer.pos))
                result = true
            }
        }
    }
    return result
}

fun Creep.slaveGoToPosNearSource(type: Int, mainContext: MainContext, slaveRoom: SlaveRoom?): Boolean {
    //type 0 - source 0, 1 - source 1, 2 - random
    var result = false
    if (slaveRoom != null) {
        val tSource: Source? = slaveRoom.source[type]
        if (tSource != null) {
            if (!this.pos.inRangeTo(tSource.pos, 1)) {
                mainContext.tasks.add(this.id, CreepTask(TypeOfTask.GoToPos, idObject0 = tSource.id, posObject0 = tSource.pos, quantity = 1))
                result = true
            }
        }
    }
    return result
}

fun Creep.slaveHarvest(type: Int, creepCarry: Int, mainContext: MainContext, slaveRoom: SlaveRoom?): Boolean {
    //type 0 - source 0, 1 - source 1, 2 - source, 3 - random
    var result = false
    if (slaveRoom != null) {
        if (creepCarry == 0) {

            var tSource: Source? = null
            when (type) {
                0, 1, 2 -> tSource = slaveRoom.source[type]
                3 -> tSource = slaveRoom.source[Random.nextInt(slaveRoom.source.size)]
            }


            if (slaveRoom.name == "E57N34") tSource = Game.getObjectById("59bbc5a12052a716c3ce9d1b") //ToDo костиль
            if (slaveRoom.name == "E51N33") tSource = Game.getObjectById("59bbc52e2052a716c3ce91c0") //ToDo костиль

            if (tSource != null) {
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
    } else {
        if (slaveRoom != null) {
            if (creepCarry > 0) {
                val structureController: StructureController? = slaveRoom.structureController[0]
                if (structureController != null) {
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
                if (this.pos.inRangeTo(tConstructionSite.pos, range)) {
                    mainContext.tasks.add(this.id, CreepTask(TypeOfTask.Build, idObject0 = tConstructionSite.id, posObject0 = tConstructionSite.pos))
                    result = true
                }
        }
    }
    return result
}

fun Creep.slaveTransferToStorageOrContainer(type: Int, creepCarry: Int, mainContext: MainContext, slaveRoom: SlaveRoom?): Boolean {
    //type 0 - cont 0, 1 - cont 1, 3 - any
    var result = false
    if (slaveRoom != null) {
        if (creepCarry > 0) {
            var objForFilling: Structure? = null
            when (type) {
                0, 1, 2 -> objForFilling = slaveRoom.structureContainerNearSource[type]
                3 -> {
                    objForFilling = slaveRoom.structureStorage[0]
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
            val containerRepair: StructureContainer? = slaveRoom.structureContainerNearSource[type]

            if (containerRepair != null) {
                if (containerRepair.hits < (containerRepair.hitsMax - 10000)) {
                    mainContext.tasks.add(this.id, CreepTask(TypeOfTask.Repair, idObject0 = containerRepair.id, posObject0 = containerRepair.pos))
                    result = true
                }
            }
        }
    }
    return result
}

fun Creep.slaveTransferToFilling(creepCarry: Int, mainContext: MainContext, slaveRoom: SlaveRoom?): Boolean {
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
        val hostileCreep: Creep? = slaveRoom.room.find(FIND_HOSTILE_CREEPS).firstOrNull()
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
        //ToDo костыль
//        if (slaveRoom.name == "W5N2"){
//            val structure: Structure? = Game.getObjectById("4158ccbdf12cc91")
//            if (structure != null) {
//                mainContext.tasks.add(this.id, CreepTask(TypeOfTask.AttackMile, idObject0 = structure.id, posObject0 = structure.pos))
//                return true
//            }
//        }

        val hostileCreep: Creep? = slaveRoom.room.find(FIND_HOSTILE_CREEPS).firstOrNull()
        if (hostileCreep != null) {
            mainContext.tasks.add(this.id, CreepTask(TypeOfTask.AttackMile, idObject0 = hostileCreep.id, posObject0 = hostileCreep.pos))
            result = true
        }
    }
    return result
}

fun Creep.slaveEraser(mainContext: MainContext, slaveRoom: SlaveRoom?): Boolean {
    var result = false
    if (slaveRoom?.room != null) {
        val hostileCreep: Creep? = slaveRoom.room.find(FIND_HOSTILE_CREEPS).minBy { it.pos.getRangeTo(this.pos) }
        if (hostileCreep != null) {
            mainContext.tasks.add(this.id, CreepTask(TypeOfTask.EraserAttack, idObject0 = hostileCreep.id, posObject0 = hostileCreep.pos))
            result = true
        }

        if (!result) {
            val keeperLair: StructureKeeperLair? = slaveRoom.structureKeeperLair.minBy { it.value.ticksToSpawn }?.value
            if (keeperLair != null) {
                mainContext.tasks.add(this.id, CreepTask(TypeOfTask.EraserGoToKL, idObject0 = keeperLair.id, posObject0 = keeperLair.pos))
                result = true
            }
        }
    }
    return result
}


