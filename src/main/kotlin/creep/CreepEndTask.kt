package creep

import mainContext.MainContext
import mainRoom.MainRoom
import screeps.api.*
import screeps.api.structures.*
import screeps.utils.toMap
import slaveRoom.SlaveRoom
import CreepTask
import role
import mainRoom
import slaveRoom

fun Creep.endTask(mainContext: MainContext) {
    if (!mainContext.tasks.isTaskForCreep(this)) return
    val task: CreepTask = mainContext.tasks.tasks[this.id] ?: return

    val creepCarry: Int = this.carry.toMap().map { it.value }.sum()


    //Break in dang room
    if ((this.memory.role in 120..125 || this.memory.role in 1120..1125) && task.type != TypeOfTask.GoToRescueFlag) {
        val mainRoom: MainRoom? = mainContext.mainRoomCollector.rooms[this.memory.mainRoom]
        if (mainRoom == null) mainContext.tasks.deleteTask(this.id)
        else {
            val slaveRoom: SlaveRoom? = mainRoom.slaveRooms[this.memory.slaveRoom]
            if (slaveRoom == null) mainContext.tasks.deleteTask(this.id)
            else {
                var indKl: Int = -1
                if (this.memory.role in arrayOf(120, 121, 1120, 1121)) indKl = 0
                if (this.memory.role in arrayOf(122, 123, 1122, 1123)) indKl = 1
                if (this.memory.role in arrayOf(124, 125, 1124, 1125)) indKl = 2
                val keeperLair = slaveRoom.structureKeeperLair[indKl]
                if (keeperLair != null) {
                    if (this.pos.inRangeTo(keeperLair.pos, 10) && keeperLair.ticksToSpawn < 20) {
                        console.log("Task deleted")
                        mainContext.tasks.deleteTask(this.id)
                        return
                    }

                }
            }
        }
    }


    when (task.type) {
        TypeOfTask.Harvest -> {
            if (creepCarry == this.carryCapacity) mainContext.tasks.deleteTask(this.id)
            val source = Game.getObjectById<Source>(task.idObject0)
            if (source?.energy == 0) mainContext.tasks.deleteTask(this.id)
        }

        TypeOfTask.HarvestMineral -> {
            mainContext.tasks.deleteTask(this.id)
        }

        TypeOfTask.Take -> {
            if (creepCarry != 0) mainContext.tasks.deleteTask(this.id)

            val structure: Structure? = (Game.getObjectById(task.idObject0) as Structure?)

            if (structure == null) mainContext.tasks.deleteTask(this.id)
            else {
                if (structure.structureType == STRUCTURE_CONTAINER
                        && (structure as StructureContainer).store.energy == 0) mainContext.tasks.deleteTask(this.id)
                if (structure.structureType == STRUCTURE_STORAGE
                        && (structure as StructureStorage).store.energy == 0) mainContext.tasks.deleteTask(this.id)
            }
        }

        TypeOfTask.TakeDropped -> {
            if (creepCarry != 0) mainContext.tasks.deleteTask(this.id)

            val resource: Resource? = (Game.getObjectById(task.idObject0) as Resource?)

            if (resource == null) mainContext.tasks.deleteTask(this.id)
        }

        TypeOfTask.Drop -> {
            if (creepCarry == 0) mainContext.tasks.deleteTask(this.id)
        }

        TypeOfTask.TransferTo -> {
            if (this.memory.role in arrayOf(106,1006,108,1008,121,123,125,1121,1123,1125)) {
                val mainRoom: MainRoom = mainContext.mainRoomCollector.rooms[this.memory.mainRoom] ?: return
                val slaveRoom: SlaveRoom?  = mainRoom.slaveRooms[this.memory.slaveRoom] ?: return
                if (slaveRoom != null) {
                    if (creepCarry > 0 && slaveRoom.constructionSite.isNotEmpty()) {
                        val tConstructionSite = slaveRoom.getConstructionSite(this.pos)
                        if (tConstructionSite != null)
                            if (this.pos.inRangeTo(tConstructionSite.pos, 3)) {
                                mainContext.tasks.deleteTask(this.id)
                                return
                            }
                    }
                }
            }

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
            if (structure.structureType == STRUCTURE_CONTAINER &&
                    (structure as StructureContainer).store.toMap().map { it.value }.sum() == structure.storeCapacity)  filled = true
            if (structure.structureType == STRUCTURE_STORAGE &&
                    (structure as StructureStorage).store.toMap().map { it.value }.sum() == structure.storeCapacity)  filled = true
            if (structure.structureType == STRUCTURE_TERMINAL &&
                    (structure as StructureTerminal).store.toMap().map { it.value }.sum() == structure.storeCapacity)  filled = true
            if (filled) mainContext.tasks.deleteTask(this.id)
        }

        TypeOfTask.TransferToCreep -> {
            val mainRoom: MainRoom? = mainContext.mainRoomCollector.rooms[this.memory.mainRoom]
            if (mainRoom == null) mainContext.tasks.deleteTask(this.id)
            else {
                val objForFilling: Creep? = Game.getObjectById(mainRoom.constant.creepIdOfBigBuilder)
                if (objForFilling == null) mainContext.tasks.deleteTask(this.id)
                else if (creepCarry == 0) mainContext.tasks.deleteTask(this.id)
            }
        }

        TypeOfTask.HealCreep -> {
            val mainRoom: MainRoom? = mainContext.mainRoomCollector.rooms[this.memory.mainRoom]
            if (mainRoom == null) mainContext.tasks.deleteTask(this.id)
            else {
                val slaveRoom: SlaveRoom?  = mainRoom.slaveRooms[this.memory.slaveRoom]
                if (slaveRoom == null) mainContext.tasks.deleteTask(this.id)
                else{
                    val objForFilling: Creep? = Game.getObjectById(slaveRoom.constant.creepIdEraser)
                    if (objForFilling == null) mainContext.tasks.deleteTask(this.id)
                }
            }
        }


        TypeOfTask.Upgrade -> {
            if (creepCarry == 0) mainContext.tasks.deleteTask(this.id)
        }

        TypeOfTask.Build -> {
            val building: ConstructionSite? = (Game.getObjectById(task.idObject0) as ConstructionSite?)
            if (creepCarry == 0 || building == null) mainContext.tasks.deleteTask(this.id)
        }

        TypeOfTask.UpgradeStructure -> {
            val building: Structure? = (Game.getObjectById(task.idObject0) as Structure?)
            if (building == null) mainContext.tasks.deleteTask(this.id)
            if (building != null && building.hits > task.quantity) mainContext.tasks.deleteTask(this.id)
        }

        TypeOfTask.Repair -> {
            val structure: Structure? = (Game.getObjectById(task.idObject0) as Structure?)
            if (creepCarry == 0 || structure == null || structure.hits > structure.hitsMax - 1000) mainContext.tasks.deleteTask(this.id)
        }

        TypeOfTask.GoToRoom -> {
            if (this.pos.roomName == this.memory.slaveRoom) mainContext.tasks.deleteTask(this.id)
        }

        TypeOfTask.GoToPos -> {
            if (task.posObject0 == null) mainContext.tasks.deleteTask(this.id)
            else if (this.pos.inRangeTo(task.posObject0,task.quantity)) mainContext.tasks.deleteTask(this.id)
        }

        TypeOfTask.AttackRange -> {
            val hostileCreep: Creep? = Game.getObjectById(task.idObject0)
            if (hostileCreep == null) mainContext.tasks.deleteTask(this.id)
        }

        TypeOfTask.AttackMile -> {
            val hostileCreep: Creep? = Game.getObjectById(task.idObject0)
            if (hostileCreep == null) mainContext.tasks.deleteTask(this.id)
        }

        TypeOfTask.SignRoom -> {
            val structureController: StructureController? = (Game.getObjectById(task.idObject0) as StructureController?)
            if (structureController == null) mainContext.tasks.deleteTask(this.id)
            if (structureController != null) {
                val sign = structureController.sign
                val mainRoom: MainRoom? = mainContext.mainRoomCollector.rooms[this.memory.mainRoom]
                if (mainRoom == null) mainContext.tasks.deleteTask(this.id)
                else {
                    var needSign = false
                    if (sign != null && sign.text != mainRoom.describe) needSign = true
                    if (sign == null) needSign = true
                    if (!needSign) mainContext.tasks.deleteTask(this.id)
                }
            }
        }

        TypeOfTask.SignSlaveRoom -> {
            val structureController: StructureController? = (Game.getObjectById(task.idObject0) as StructureController?)
            if (structureController == null) mainContext.tasks.deleteTask(this.id)
            if (structureController != null) {
                val sign = structureController.sign
                val mainRoom: MainRoom? = mainContext.mainRoomCollector.rooms[this.memory.mainRoom]
                if (mainRoom == null) mainContext.tasks.deleteTask(this.id)
                else {
                    val slaveRoom: SlaveRoom? = mainRoom.slaveRooms[this.memory.slaveRoom]
                    if (slaveRoom == null) mainContext.tasks.deleteTask(this.id)
                    else {
                        var needSign = false
                        if (sign != null && sign.text != slaveRoom.describe) needSign = true
                        if (sign == null) needSign = true
                        if (!needSign) mainContext.tasks.deleteTask(this.id)
                    }
                }
            }
        }

        TypeOfTask.Transport -> {
            if (creepCarry == 0 && task.take) mainContext.tasks.deleteTask(this.id)
        }

        TypeOfTask.EraserAttack -> {
            val hostileCreep: Creep? = Game.getObjectById(task.idObject0)
            if (hostileCreep == null) mainContext.tasks.deleteTask(this.id)
        }

        TypeOfTask.EraserGoToKL -> {
            val structureKeeperLair: StructureKeeperLair? = Game.getObjectById(task.idObject0)

            if (structureKeeperLair == null) {
                mainContext.tasks.deleteTask(this.id);return}

            if (this.pos.inRangeTo(structureKeeperLair.pos,1)) {
                mainContext.tasks.deleteTask(this.id); return}

            val mainRoom: MainRoom? = mainContext.mainRoomCollector.rooms[this.memory.mainRoom]
            if (mainRoom == null) {mainContext.tasks.deleteTask(this.id); return}

            val slaveRoom: SlaveRoom? = mainRoom.slaveRooms[this.memory.slaveRoom]
            if (slaveRoom == null) {mainContext.tasks.deleteTask(this.id); return}

            val hostileCreep : Creep? =  slaveRoom.room?.find(FIND_HOSTILE_CREEPS)?.firstOrNull()
            if (hostileCreep != null) {mainContext.tasks.deleteTask(this.id); return}
        }

        TypeOfTask.GoToRescueFlag -> {
            val mainRoom: MainRoom? = mainContext.mainRoomCollector.rooms[this.memory.mainRoom]
            if (mainRoom == null) mainContext.tasks.deleteTask(this.id)
            else {
                val slaveRoom: SlaveRoom? = mainRoom.slaveRooms[this.memory.slaveRoom]
                if (slaveRoom == null) mainContext.tasks.deleteTask(this.id)
                else {
                    var indKl: Int = -1
                    if (this.memory.role in arrayOf(120, 121, 1120, 1121)) indKl = 0
                    if (this.memory.role in arrayOf(122, 123, 1122, 1123)) indKl = 1
                    if (this.memory.role in arrayOf(124, 125, 1124, 1125)) indKl = 2
                    val keeperLair = slaveRoom.structureKeeperLair[indKl]
                    if (keeperLair == null) mainContext.tasks.deleteTask(this.id)
                    else {
                        if (keeperLair.ticksToSpawn > 30) {
                            val hostileNear = this.room.find(FIND_HOSTILE_CREEPS).filter { it.pos.inRangeTo(this.pos,10) }
                            if (hostileNear.isEmpty()) mainContext.tasks.deleteTask(this.id)
                            return
                        }

                    }
                }
            }
        }

        else -> {
        }
    }


}