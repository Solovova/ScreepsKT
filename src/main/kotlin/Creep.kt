import screeps.api.*
import screeps.utils.toMap
import screeps.api.structures.Structure
import screeps.api.structures.StructureExtension
import screeps.api.structures.StructureSpawn

fun Creep.newTask(mainContext: MainContext) {

    if (this.spawning) return
    val mainRoom: MainRoom = mainContext.mainRooms.rooms[this.memory.dstRoom] ?: return
    this.endTask(mainContext)
    if (mainContext.tasks.isTaskForCreep(this)) return

    var isTask = false
    val creepCarry: Int = this.carry.toMap().map { it.value }.sum()

    // Harvest
    if (!isTask) {
        if (creepCarry == 0) {// Ищем c минимальным расстоянием
            var tSource: Source = mainRoom.source.values.first()
            var tDistance = 1000
            for (source in mainRoom.source.values) {
                val tRangeTmp = this.pos.getRangeTo(source.pos)
                if (tRangeTmp < tDistance) {
                    tDistance = tRangeTmp
                    tSource = source
                }
            }
            mainContext.tasks.add(this.id, CreepTask(TypeOfTask.Harvest, idObject0 = tSource.id, posObject0 = tSource.pos))
            isTask = true
        }
    }

    // TransferTo
    if (!isTask) {
        if (creepCarry > 0) {
            val objForFilling: Structure = mainRoom.structureSpawn.values.first() as Structure  //ToDo all structure
            console.log(objForFilling.id)
            //if (objForFilling != null) {
                mainContext.tasks.add(this.id, CreepTask(TypeOfTask.TransferTo, idObject0 = objForFilling.id, posObject0 = objForFilling.pos))
                //isTask = true
            //}
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
            if (!task.come) {this.doTaskGoTo(task, task.posObject0, 1); }
            if (task.come) {
                val structure: Structure? = (Game.getObjectById(task.idObject0) as Structure?)
                if (structure!=null) {
                    this.transfer(structure, task.resource)
                }
            }
        }
        else -> {}
    }
}

fun Creep.endTask(mainContext: MainContext) {
    if (!mainContext.tasks.isTaskForCreep(this)) return
    val task: CreepTask = mainContext.tasks.tasks[this.id] ?: return

    val creepCarry: Int = this.carry.toMap().map { it.value }.sum()

    when (task.type) {
        TypeOfTask.Harvest -> {
            if (creepCarry == this.carryCapacity) mainContext.tasks.deleteTask(this.id)
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
            if (filled) mainContext.tasks.deleteTask(this.id)
        }
        else -> {
        }
    }
}

fun Creep.doTaskGoTo(task: CreepTask, pos: RoomPosition, range: Int) {
    if (this.pos.inRangeTo(pos, range)) task.come = true
    else this.moveTo(pos.x, pos.y)
}
