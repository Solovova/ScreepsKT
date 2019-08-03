import screeps.api.*
import screeps.api.structures.*
import screeps.utils.toMap

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
        // Harvest
        if (!isTask) {
            if (creepCarry == 0) {// Ищем c минимальным расстоянием
                val tSource: Source = mainRoom.getSourceForHarvest(this.pos, mainContext)
                mainContext.tasks.add(this.id, CreepTask(TypeOfTask.Harvest, idObject0 = tSource.id, posObject0 = tSource.pos))
                isTask = true
            }
        }

        // TransferTo
        if (!isTask) {
            if (creepCarry > 0) {
                val objForFilling: Structure? = mainRoom.getSpawnOrExtensionForFiling(this.pos, mainContext)
                if (objForFilling != null) {
                    mainContext.tasks.add(this.id, CreepTask(TypeOfTask.TransferTo, idObject0 = objForFilling.id, posObject0 = objForFilling.pos))
                    isTask = true
                }
            }
        }

        // Upgrade
        if (!isTask) {
            if (creepCarry > 0) {
                val structureController:StructureController? = mainRoom.structureController[0]
                if (structureController != null && (structureController.level < 2 || structureController.ticksToDowngrade < 1000)) {
                    mainContext.tasks.add(this.id, CreepTask(TypeOfTask.Upgrade, idObject0 = structureController.id, posObject0 = structureController.pos))
                    isTask = true
                }
            }
        }

        // Build
        if (!isTask) {
            if (creepCarry > 0 && mainRoom.constructionSite.isNotEmpty()) {
                val tConstructionSite = mainRoom.getConstructionSite(this.pos)
                if (tConstructionSite != null) {
                    mainContext.tasks.add(this.id, CreepTask(TypeOfTask.Build, idObject0 = tConstructionSite.id, posObject0 = tConstructionSite.pos))
                    isTask = true
                }
            }
        }

        // Upgrade
        if (!isTask) {
            if (creepCarry > 0) {
                val structureController: StructureController? = mainRoom.structureController[0]
                if (structureController != null)
                    mainContext.tasks.add(this.id, CreepTask(TypeOfTask.Upgrade, idObject0 = structureController.id, posObject0 = structureController.pos))
                //isTask = true
            }
        }
    }

    if (this.memory.role == 100) {
        //GoToRoom
        if (!isTask) {
            if (this.room.name != this.memory.slaveRoom) {
                mainContext.tasks.add(this.id, CreepTask(TypeOfTask.GoToRoom, idObject0 = this.memory.slaveRoom, posObject0 = RoomPosition(25, 25, this.memory.slaveRoom)))
                isTask = true
            }
        }
        //Claim
        if (!isTask) {
            if (slaveRoom != null) {
                val structureController: StructureController? = slaveRoom.structureController[0]
                if (structureController != null && !structureController.my) {
                    mainContext.tasks.add(this.id, CreepTask(TypeOfTask.Claim, idObject0 = structureController.id, posObject0 = structureController.pos))
                    //isTask = true
                }
            }
        }

    }

    if (this.memory.role == 101) {
        //GoToRoom
        if (!isTask) {
            if (this.room.name != this.memory.slaveRoom) {
                mainContext.tasks.add(this.id, CreepTask(TypeOfTask.GoToRoom, idObject0 = this.memory.slaveRoom, posObject0 = RoomPosition(25, 25, this.memory.slaveRoom)))
                isTask = true
            }
        }

        // Harvest
        if (!isTask) {
            if (slaveRoom != null) {
                if (creepCarry == 0) {
                    val tSource: Source? = slaveRoom.source[0]
                    if (tSource!=null) {
                        console.log(tSource.id)
                        mainContext.tasks.add(this.id, CreepTask(TypeOfTask.Harvest, idObject0 = tSource.id, posObject0 = tSource.pos))
                        isTask = true
                    }
                }
            }
        }

        // Upgrade
        if (!isTask) {
            if (creepCarry > 0) {
                if (slaveRoom != null) {
                    val structureController: StructureController? = slaveRoom.structureController[0]
                    if (structureController != null && (structureController.level < 2 || structureController.ticksToDowngrade < 1000)) {
                        mainContext.tasks.add(this.id, CreepTask(TypeOfTask.Upgrade, idObject0 = structureController.id, posObject0 = structureController.pos))
                        isTask = true
                    }
                }
            }
        }

        // Build
        if (!isTask) {
            if (slaveRoom != null) {
                if (creepCarry > 0 && slaveRoom.constructionSite.isNotEmpty()) {
                    val tConstructionSite = slaveRoom.getConstructionSite(this.pos)
                    if (tConstructionSite != null) {
                        mainContext.tasks.add(this.id, CreepTask(TypeOfTask.Build, idObject0 = tConstructionSite.id, posObject0 = tConstructionSite.pos))
                        isTask = true
                    }
                }
            }
        }

        // Upgrade
        if (!isTask) {
            if (slaveRoom != null) {
                if (creepCarry > 0) {
                    val structureController: StructureController? = slaveRoom.structureController[0]
                    if (structureController != null)
                        mainContext.tasks.add(this.id, CreepTask(TypeOfTask.Upgrade, idObject0 = structureController.id, posObject0 = structureController.pos))
                    //isTask = true
                }
            }
        }


    }


}

fun Creep.doTask(mainContext: MainContext) {
    if (!mainContext.tasks.isTaskForCreep(this)) return

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
