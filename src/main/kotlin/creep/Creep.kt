package creep

import CreepTask
import MainContext
import mainRoom.MainRoom
import slaveRoom.SlaveRoom
import TypeOfTask
import mainRoom
import messenger
import role
import screeps.api.*
import screeps.api.structures.*
import screeps.utils.toMap
import slaveRoom

fun Creep.newTask(mainContext: MainContext) {

    if (this.spawning) return
    val mainRoom: MainRoom = mainContext.mainRoomCollector.rooms[this.memory.mainRoom] ?: return
    var slaveRoom: SlaveRoom? = null
    if (this.memory.role in 100..199 || this.memory.role in 1100..1199) {
        slaveRoom = mainRoom.slaveRooms[this.memory.slaveRoom] ?: return
    }

    this.endTask(mainContext)
    if (mainContext.tasks.isTaskForCreep(this)) return

    var isTask = false
    val creepCarry: Int = this.carry.toMap().map { it.value }.sum()

    //00 starting creep, harvester, upgrader, builder to level 4
    if (this.memory.role == 0) {
        if (!isTask) isTask = this.takeFromStorage(creepCarry, mainContext, mainRoom)
        if (!isTask) isTask = this.takeFromContainer(3,creepCarry, mainContext, mainRoom)
        if (!isTask) isTask = this.harvestFromSource(2, creepCarry, mainContext, mainRoom)
        if (!isTask) isTask = this.transferToFilling(creepCarry, mainContext, mainRoom)
        if (!isTask) isTask = this.upgradeNormalOrEmergency(1, creepCarry, mainContext, mainRoom)
        if (!isTask) isTask = this.buildStructure(creepCarry, mainContext, mainRoom)
        if (!isTask) isTask = this.upgradeNormalOrEmergency(0, creepCarry, mainContext, mainRoom)
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
        if ((this.memory.role == 5) && this.ticksToLive<150) this.memory.role = this.memory.role + 1000
        if (!isTask) isTask = this.takeFromStorage(creepCarry,mainContext,mainRoom)
        if (!isTask) isTask = this.transferToFilling(creepCarry,mainContext,mainRoom)
    }

    if (this.memory.role == 6) {
        if (!isTask) isTask = this.takeFromStorage(creepCarry,mainContext,mainRoom)
        if (!isTask) isTask = this.transferToContainer(2,creepCarry,mainContext,mainRoom)
    }

    if (this.memory.role == 7) {
        if (!isTask) isTask = this.takeFromContainer(2,creepCarry,mainContext,mainRoom)
        if (!isTask) isTask = this.upgradeNormalOrEmergency(0,creepCarry,mainContext,mainRoom)
    }

    if (this.memory.role == 8) {
        if (!isTask) isTask = this.takeFromStorage(creepCarry,mainContext,mainRoom)
        if (!isTask) isTask = this.buildStructure(creepCarry, mainContext, mainRoom)
    }

    if (this.memory.role == 9) {
        if (!isTask) isTask = this.takeFromStorage(creepCarry,mainContext,mainRoom)
        if (!isTask) isTask = this.transferToFilling(creepCarry, mainContext, mainRoom)
    }

    if (this.memory.role == 13) {
        if (!isTask) isTask = this.takeFromStorage(creepCarry, mainContext, mainRoom)
        if (!isTask) isTask = this.takeFromContainer(3,creepCarry, mainContext, mainRoom)
        if (!isTask) isTask = this.harvestFromSource(2, creepCarry, mainContext, mainRoom)
        if (!isTask) isTask = this.upgradeNormalOrEmergency(0, creepCarry, mainContext, mainRoom)
    }

    if (this.memory.role == 100) {
        if (!isTask) isTask = this.slaveGoToRoom(mainContext)
        if (!isTask) isTask = this.slaveClaim(mainContext,slaveRoom)
    }

    if (this.memory.role == 101) {
        if (!isTask) isTask = this.slaveGoToRoom(mainContext)
        if (!isTask) isTask = this.slaveHarvest(creepCarry,mainContext,slaveRoom)
        if (!isTask) isTask = this.slaveUpgradeNormalOrEmergency(0,creepCarry,mainContext,slaveRoom)
        if (!isTask) isTask = this.slaveBuild(creepCarry,mainContext,slaveRoom)
        if (!isTask) isTask = this.slaveUpgradeNormalOrEmergency(1,creepCarry,mainContext,slaveRoom)
    }

    if (this.memory.role == 102) {
        if (!isTask) isTask = this.takeFromStorage(creepCarry,mainContext,mainRoom)
        if (!isTask) isTask = this.slaveTransferToStorageOrContainer(creepCarry,mainContext,mainRoom,slaveRoom)
    }
}

fun Creep.doTask(mainContext: MainContext) {
    if (!mainContext.tasks.isTaskForCreep(this)) return

    val mainRoom: MainRoom = mainContext.mainRoomCollector.rooms[this.memory.mainRoom] ?: return
    if (this.memory.role in 100..199 || this.memory.role in 1100..1199) {
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
            val flag:Flag? = this.room.find(FIND_FLAGS).firstOrNull { it.color == COLOR_GREY && it.secondaryColor == COLOR_GREY }
            if (flag != null) {
                this.moveTo(flag.pos.x,flag.pos.y)
            }else {
                if (this.pos.roomName != this.memory.slaveRoom) {
                    val exitDir = this.room.findExitTo(this.memory.slaveRoom)
                    val exitPath = this.pos.findClosestByRange(exitDir)
                    if (exitPath != null) if (this.fatigue == 0) this.moveTo(exitPath.x, exitPath.y)
                }
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
    if (this.pos.inRangeTo(pos, range)) task.come = true
    else if (this.fatigue ==0) this.moveTo(pos)
}
