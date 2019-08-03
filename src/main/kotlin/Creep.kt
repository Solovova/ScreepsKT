import screeps.api.*
import screeps.api.structures.*
import screeps.utils.toMap
import kotlin.random.Random

fun Creep.takeFromStorage(creepCarry: Int, mainContext: MainContext, mainRoom: MainRoom): Boolean {
    var result = false
    if (creepCarry == 0) {
        val tStorage: StructureStorage? = mainRoom.structureStorage[0]
        if (tStorage!=null) {
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
            val structureController:StructureController? = mainRoom.structureController[0]
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
    // 0 - Source 0, 1 - Source 1, 2 - Controller
    var result = false
    if (creepCarry == 0) {
        var objForFilling: Structure? = null
        when(type) {
            0 -> objForFilling = mainRoom.structureContainerNearSource[0]
            1 -> objForFilling = mainRoom.structureContainerNearSource[1]
            2 -> objForFilling = mainRoom.structureContainerNearController[0]
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

fun Creep.slaveClaim(mainContext: MainContext,slaveRoom: SlaveRoom?): Boolean {
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


fun Creep.newTask(mainContext: MainContext) {

    if (this.spawning) return
    val mainRoom: MainRoom = mainContext.mainRooms.rooms[this.memory.mainRoom] ?: return
    var slaveRoom: SlaveRoom? = null
    if (this.memory.role > 99) {
        slaveRoom = mainRoom.slaveRooms[this.memory.slaveRoom] ?: return
    }

    this.endTask(mainContext)
    if (mainContext.tasks.isTaskForCreep(this)) return

    var isTask = false
    val creepCarry: Int = this.carry.toMap().map { it.value }.sum()

    //00 starting creep, harvester, upgrader, builder to level 4
    if (this.memory.role == 0) {
        if (mainRoom.name == "E54N37") {
            if (!isTask) isTask = this.takeFromStorage(creepCarry,mainContext,mainRoom)
            if (!isTask) isTask = this.buildStructure(creepCarry,mainContext,mainRoom)
            if (!isTask) isTask = this.upgradeNormalOrEmergency(0,creepCarry,mainContext,mainRoom)
        }else{
            if (!isTask) isTask = this.takeFromStorage(creepCarry,mainContext,mainRoom)
            if (!isTask) isTask = this.harvestFromSource(2,creepCarry,mainContext,mainRoom)
            if (!isTask) isTask = this.transferToFilling(creepCarry,mainContext,mainRoom)
            if (!isTask) isTask = this.upgradeNormalOrEmergency(1,creepCarry,mainContext,mainRoom)
            if (!isTask) isTask = this.buildStructure(creepCarry,mainContext,mainRoom)
            if (!isTask) isTask = this.upgradeNormalOrEmergency(0,creepCarry,mainContext,mainRoom)
        }

    }

    if (this.memory.role == 1 || this.memory.role == 1001) {
        if ((this.memory.role == 1) && this.ticksToLive<100) this.memory.role = this.memory.role + 1000

        if (!isTask) isTask = this.harvestFromSource(0,creepCarry,mainContext,mainRoom)
        if (!isTask) isTask = this.transferToContainer(0,creepCarry,mainContext,mainRoom)
    }

    if (this.memory.role == 3 || this.memory.role == 1003) {
        if ((this.memory.role == 3) && this.ticksToLive<100) this.memory.role = this.memory.role + 1000

        if (!isTask) isTask = this.harvestFromSource(1,creepCarry,mainContext,mainRoom)
        if (!isTask) isTask = this.transferToContainer(1,creepCarry,mainContext,mainRoom)
    }


    if (this.memory.role == 2 || this.memory.role == 1002) {
        if ((this.memory.role == 2) && this.ticksToLive<100) this.memory.role = this.memory.role + 1000
        if (!isTask) isTask = this.takeFromContainer(0,creepCarry,mainContext,mainRoom)
        if (!isTask) isTask = this.transferToStorage(creepCarry,mainContext,mainRoom)
    }

    if (this.memory.role == 4 || this.memory.role == 1004) {
        if ((this.memory.role == 4) && this.ticksToLive<100) this.memory.role = this.memory.role + 1000
        if (!isTask) isTask = this.takeFromContainer(1,creepCarry,mainContext,mainRoom)
        if (!isTask) isTask = this.transferToStorage(creepCarry,mainContext,mainRoom)
    }

    if (this.memory.role == 5 || this.memory.role == 1005) {
        if ((this.memory.role == 5) && this.ticksToLive<100) this.memory.role = this.memory.role + 1000
        if (!isTask) isTask = this.takeFromStorage(creepCarry,mainContext,mainRoom)
        if (!isTask) isTask = this.transferToFilling(creepCarry,mainContext,mainRoom)
    }

    if (this.memory.role == 100) {
        if (!isTask) isTask = this.slaveGoToRoom(mainContext)
        if (!isTask) isTask = this.slaveClaim(mainContext,slaveRoom)
    }

    if (this.memory.role == 101) {
        if (!isTask) isTask = this.slaveGoToRoom(mainContext)
        if (!isTask) isTask = this.slaveHarvest(creepCarry,mainContext,slaveRoom)
        if (!isTask) isTask = this.slaveUpgradeNormalOrEmergency(1,creepCarry,mainContext,slaveRoom)
        if (!isTask) isTask = this.slaveBuild(creepCarry,mainContext,slaveRoom)
        if (!isTask) isTask = this.slaveUpgradeNormalOrEmergency(0,creepCarry,mainContext,slaveRoom)
    }
}

fun Creep.doTask(mainContext: MainContext) {
    if (!mainContext.tasks.isTaskForCreep(this)) return

    val mainRoom: MainRoom = mainContext.mainRooms.rooms[this.memory.mainRoom] ?: return
    if (this.memory.role > 99) {
        mainRoom.slaveRooms[this.memory.slaveRoom] ?: return
    }

    val task: CreepTask = mainContext.tasks.tasks[this.id] ?: return
    if (task.posObject0 == null) {
        messenger("ERROR", "", "PosFrom not have", COLOR_RED)
        return
    }

    when (task.type) {
        TypeOfTask.Harvest -> {
            if (!task.come) this.doTaskGoTo(task, task.posObject0,1)
            if (task.come) {
                val source: Source? = (Game.getObjectById(task.idObject0) as Source?)
                if (source != null) this.harvest(source)
            }
        }

        TypeOfTask.TransferTo -> {
            if (!task.come) this.doTaskGoTo(task, task.posObject0, 1)
            if (task.come) {
                val structure: Structure? = (Game.getObjectById(task.idObject0) as Structure?)
                if (structure != null) this.transfer(structure, task.resource)
            }
        }

        TypeOfTask.Upgrade -> {
            if (!task.come) this.doTaskGoTo(task, task.posObject0, 3)
            if (task.come) {
                val controller: StructureController? = (Game.getObjectById(task.idObject0) as StructureController?)
                if (controller != null) this.upgradeController(controller)
            }
        }

        TypeOfTask.Build -> {
            if (!task.come) this.doTaskGoTo(task, task.posObject0, 3)
            if (task.come) {
                val building: ConstructionSite? = (Game.getObjectById(task.idObject0) as ConstructionSite?)
                if (building != null) this.build(building)
            }
        }

        TypeOfTask.GoToRoom -> {
            if (this.pos.roomName != this.memory.slaveRoom) {
                val exitDir = this.room.findExitTo(this.memory.slaveRoom)
                val exitPath = this.pos.findClosestByRange(exitDir)
                if (exitPath != null) this.moveTo(exitPath.x,exitPath.y)
            }
        }

        TypeOfTask.Claim -> {
            if (!task.come) this.doTaskGoTo(task, task.posObject0, 1)
            if (task.come) {
                val structureController: StructureController? = (Game.getObjectById(task.idObject0) as StructureController?)
                if (structureController != null) this.claimController(structureController)
            }
        }

        TypeOfTask.Take -> {
            if (!task.come) this.doTaskGoTo(task, task.posObject0, 1)
            if (task.come) {
                val structure: Structure? = (Game.getObjectById(task.idObject0) as Structure?)
                if (structure != null) this.withdraw(structure, task.resource)
            }
        }

        else -> {
        }
    }
}

fun Creep.endTask(mainContext: MainContext) {
    if (!mainContext.tasks.isTaskForCreep(this)) return
    val task: CreepTask = mainContext.tasks.tasks[this.id] ?: return

    val creepCarry: Int = this.carry.toMap().map { it.value }.sum()



    when (task.type) {
        TypeOfTask.Harvest -> {
            if (creepCarry == this.carryCapacity) mainContext.tasks.deleteTask(this.id)
            val source = Game.getObjectById<Source>(task.idObject0)
            if (source?.energy == 0) mainContext.tasks.deleteTask(this.id)
        }

        TypeOfTask.Take -> {
            if (creepCarry != 0) mainContext.tasks.deleteTask(this.id)
        }

        TypeOfTask.TransferTo -> {
            if (creepCarry == 0) {
                mainContext.tasks.deleteTask(this.id)
                return
            }

            val structure: Structure? = (Game.getObjectById(task.idObject0) as Structure?)
            if (structure == null) {
                mainContext.tasks.deleteTask(this.id)
                return
            }
            var filled = false
            if (structure.structureType == STRUCTURE_EXTENSION && (structure as StructureExtension).energyCapacity == structure.energy) filled = true
            if (structure.structureType == STRUCTURE_SPAWN && (structure as StructureSpawn).energyCapacity == structure.energy) filled = true
            if (structure.structureType == STRUCTURE_TOWER && (structure as StructureTower).energyCapacity == structure.energy) filled = true
            if (filled) mainContext.tasks.deleteTask(this.id)
        }

        TypeOfTask.Upgrade -> {
            if (creepCarry == 0) mainContext.tasks.deleteTask(this.id)
        }

        TypeOfTask.Build -> {
            val building: ConstructionSite? = (Game.getObjectById(task.idObject0) as ConstructionSite?)
            if (creepCarry == 0 || building == null) mainContext.tasks.deleteTask(this.id)
        }

        TypeOfTask.GoToRoom -> {
            if (this.pos.roomName == this.memory.slaveRoom) mainContext.tasks.deleteTask(this.id)
        }

        else -> {
        }
    }


}

fun Creep.doTaskGoTo(task: CreepTask, pos: RoomPosition, range: Int) {
    if (this.pos.roomName != pos.roomName) {
        val exitDir = this.room.findExitTo(pos.roomName)
        val exitPath = this.pos.findClosestByRange(exitDir)
        if (exitPath != null) this.moveTo(exitPath.x,exitPath.y)
        return
    }

    if (this.pos.inRangeTo(pos, range)) task.come = true
    else this.moveTo(pos.x, pos.y)
}
