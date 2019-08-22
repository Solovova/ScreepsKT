package creep

import mainContext.MainContext
import mainRoom.MainRoom
import screeps.api.*
import screeps.api.structures.*
import slaveRoom.SlaveRoom
import CreepTask
import mainContext.messenger
import role
import mainRoom
import screeps.utils.toMap
import slaveRoom
import upgrade

fun Creep.doTask(mainContext: MainContext) {
    if (!mainContext.tasks.isTaskForCreep(this)) return

    val mainRoom: MainRoom = mainContext.mainRoomCollector.rooms[this.memory.mainRoom] ?: return
    if (this.memory.role in 100..199 || this.memory.role in 1100..1199) {
        mainRoom.slaveRooms[this.memory.slaveRoom] ?: return
    }

    val task: CreepTask = mainContext.tasks.tasks[this.id] ?: return
    if (task.posObject0 == null) {
        mainContext.messenger("ERROR", "", "PosFrom not have", COLOR_RED)
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

        TypeOfTask.HarvestMineral -> {
            val mineral: Mineral? = (Game.getObjectById(task.idObject0) as Mineral?)
            if (mineral != null) this.harvest(mineral)
        }

        TypeOfTask.TransferTo -> {
            if (!task.come) this.doTaskGoTo(task, task.posObject0, 1)
            if (task.come) {
                val structure: Structure? = (Game.getObjectById(task.idObject0) as Structure?)
                if (structure != null) this.transfer(structure, task.resource)
            }
        }

        TypeOfTask.TransferToCreep -> {
            val objForFilling: Creep? = Game.getObjectById(task.idObject0)
            if (objForFilling != null) {
                if (this.pos.inRangeTo(objForFilling.pos,1)){
                    val carryCreepTo = objForFilling.carry.toMap().map { it.value }.sum()
                    if (carryCreepTo == 0)
                        this.transfer(objForFilling, task.resource)
                }
                else this.moveTo(objForFilling.pos)
            }
        }

        TypeOfTask.TransferFromCreep -> {
            val objForFilling: Creep? = Game.getObjectById(task.idObject0)
            if (objForFilling != null) {
                if (this.pos.inRangeTo(objForFilling.pos,1)){
                    val carryCreepTo = objForFilling.carry.toMap().map { it.value }.sum()
                    if (carryCreepTo != 0)
                        objForFilling.transfer(this,task.resource)
                }
                else this.moveTo(objForFilling.pos)
            }
        }

        TypeOfTask.HealCreep -> {
            if (this.hits < (this.hitsMax - 100)) this.heal(this)

            val objForFilling: Creep? = Game.getObjectById(mainRoom.constant.creepIdOfBigBuilder)
            if (objForFilling != null) {
                if (this.pos.inRangeTo(objForFilling.pos,1))
                    this.heal(objForFilling)
                else {
                    if (this.pos.inRangeTo(objForFilling.pos,3)) this.heal(objForFilling)
                    this.moveTo(objForFilling.pos)
                }
            }
        }



        TypeOfTask.Upgrade -> {
            if (!task.come) this.doTaskGoTo(task, task.posObject0, 3)
            if (task.come) {
                val controller: StructureController? = (Game.getObjectById(task.idObject0) as StructureController?)
                if (controller != null) this.upgradeController(controller)
            }
        }

        TypeOfTask.UpgradeCreep -> {
            if (!task.come) this.doTaskGoTo(task, task.posObject0, 1)
            if (task.come) {
                val lab: StructureLab? = (Game.getObjectById(task.idObject0) as StructureLab?)
                if (lab == null) this.memory.upgrade = "u"
                if (lab != null && mainRoom.creepNeedUpgradeID == this.id) {
                    lab.boostCreep(this)
                    this.memory.upgrade = "u"
                }
            }
        }

        TypeOfTask.Build -> {
            if (!task.come) this.doTaskGoTo(task, task.posObject0, 3)
            if (task.come) {
                val building: ConstructionSite? = (Game.getObjectById(task.idObject0) as ConstructionSite?)
                if (building != null) this.build(building)
            }
        }

        TypeOfTask.UpgradeStructure -> {
            if (!task.come) this.doTaskGoTo(task, task.posObject0, 3)
            if (task.come) {
                val building: Structure? = (Game.getObjectById(task.idObject0) as Structure?)
                if (building != null) this.repair(building)
            }
        }

        TypeOfTask.Repair -> {
            if (!task.come) this.doTaskGoTo(task, task.posObject0, 3)
            if (task.come) {
                val repairStructure: Structure? = (Game.getObjectById(task.idObject0) as Structure?)
                if (repairStructure != null) {
                    this.repair(repairStructure)
                }
            }
        }

        TypeOfTask.GoToRoom -> {
            val flag: Flag? = this.room.find(FIND_FLAGS).firstOrNull { it.color == COLOR_GREY && it.secondaryColor == COLOR_GREY }
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

        TypeOfTask.TakeDropped -> {
            if (!task.come) this.doTaskGoTo(task, task.posObject0, 1)
            if (task.come) {
                val resource: Resource? = (Game.getObjectById(task.idObject0) as Resource?)
                if (resource != null) this.pickup(resource)
            }
        }

        TypeOfTask.Drop -> {
            this.drop(task.resource)
        }

        TypeOfTask.Reserve -> {
            if (!task.come) this.doTaskGoTo(task, task.posObject0, 1)
            if (task.come) {
                val structureController: StructureController? = (Game.getObjectById(task.idObject0) as StructureController?)
                if (structureController != null) this.reserveController(structureController)
            }
        }

        TypeOfTask.GoToPos -> {
            if (!task.come) this.doTaskGoTo(task, task.posObject0, task.quantity)
        }

        TypeOfTask.AttackRange -> {
            val hostileCreep: Creep? = Game.getObjectById(task.idObject0)
            if (hostileCreep != null)
                if (this.pos.inRangeTo(hostileCreep.pos, 3))
                    this.rangedAttack(hostileCreep)
                else this.moveTo(hostileCreep)
        }

        TypeOfTask.AttackMile -> {
            val hostileCreep: Creep? = Game.getObjectById(task.idObject0)
            if (hostileCreep != null)
                if (this.pos.inRangeTo(hostileCreep.pos, 1))
                    this.attack(hostileCreep)
                else this.moveTo(hostileCreep)
        }

        TypeOfTask.SignRoom -> {
            if (!task.come) this.doTaskGoTo(task, task.posObject0, 1)
            if (task.come) {
                val structureController: StructureController? = (Game.getObjectById(task.idObject0) as StructureController?)
                if (structureController != null) this.signController(structureController, mainRoom.describe)
            }
        }

        TypeOfTask.SignSlaveRoom -> {
            if (!task.come) this.doTaskGoTo(task, task.posObject0, 1)
            if (task.come) {
                val structureController: StructureController? = (Game.getObjectById(task.idObject0) as StructureController?)
                val slaveRoom: SlaveRoom? = mainRoom.slaveRooms[this.memory.slaveRoom]
                if (structureController != null && slaveRoom!=null) this.signController(structureController, slaveRoom.describe)
            }
        }

        TypeOfTask.Transport -> {
            val posGo: RoomPosition = (if (task.take) task.posObject1 else task.posObject0)
                    ?: return
            if (!task.come) this.doTaskGoTo(task, posGo, 1)
            if (task.come) {
                if (!task.take) {
                    val structure: Structure? = (Game.getObjectById(task.idObject0) as Structure?)
                    if (structure != null) {
                        if (task.quantity == 0) this.withdraw(structure, task.resource)
                        else this.withdraw(structure, task.resource, task.quantity)
                    }
                    task.take = true
                    task.come = false
                }else{
                    val structure: Structure? = (Game.getObjectById(task.idObject1) as Structure?)
                    if (structure != null) {
                        if (task.quantity == 0) this.transfer(structure, task.resource)
                        else this.transfer(structure, task.resource, task.quantity)
                    }
                }

            }
        }

        TypeOfTask.EraserAttack -> {
            val hostileCreep: Creep? = Game.getObjectById(task.idObject0)
            if (hostileCreep != null)
                if (this.pos.inRangeTo(hostileCreep.pos, 1)) {
                    this.heal(this)
                    this.attack(hostileCreep)
                } else {
                    if (this.pos.getRangeTo(hostileCreep) in 4..10 && this.hits < this.hitsMax) {
                        this.heal(this)
                    } else {
                        this.heal(this)
                        this.moveTo(hostileCreep)
                    }
                }
        }

        TypeOfTask.EraserGoToKL -> {
            val structureKeeperLair: StructureKeeperLair? = Game.getObjectById(task.idObject0)
            if (structureKeeperLair != null)
                if (!this.pos.inRangeTo(structureKeeperLair.pos, 1)) {
                    if (this.pos.getRangeTo(structureKeeperLair) in 4..10 && this.hits < this.hitsMax) {
                        this.heal(this)
                    } else {
                        this.heal(this)
                        this.moveTo(structureKeeperLair)
                    }
                }
        }

        TypeOfTask.GoToRescueFlag -> {
            if (!task.come) this.doTaskGoTo(task, task.posObject0, 1)
        }



        else -> {
        }
    }
}



fun Creep.doTaskGoTo(task: CreepTask, pos: RoomPosition, range: Int) {
    if (this.pos.inRangeTo(pos, range)) task.come = true
    else {
        if (this.memory.role in arrayOf(106,108,1106,1108,121,123,125))
            if (this.room.name != this.memory.mainRoom) {
                val room: Room? = Game.rooms[this.pos.roomName]
                if (room != null) {
                    val fFind: Array<Structure> = (room.lookForAt(LOOK_STRUCTURES,this.pos.x, this.pos.y) ?: arrayOf())
                            .filter { it.structureType == STRUCTURE_ROAD && it.hits < (it.hitsMax - 100)}.toTypedArray()
                    if (fFind.isNotEmpty()) this.repair(fFind[0])
                }
            }

        if (this.fatigue ==0) {
            this.moveTo(pos)
        }
    }
}
