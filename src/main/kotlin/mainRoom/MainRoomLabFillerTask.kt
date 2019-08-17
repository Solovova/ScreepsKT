package mainRoom

import screeps.api.Creep
import screeps.api.ResourceConstant
import LabFillerTask
import screeps.api.RESOURCE_ENERGY
import CreepTask
import kotlin.math.min

fun MainRoom.setLabFillerTask(creep: Creep) {
    val terminal = this.structureTerminal[0] ?: return
    val lab0 = this.structureLabSort[0] ?: return
    val lab1 = this.structureLabSort[1] ?: return
    val sourceLab = arrayOf(lab0, lab1)
    val listTasks: MutableList<LabFillerTask> = mutableListOf()

    if (this.constant.reactionActive != "") {
        val reaction = this.constant.reactionActive.unsafeCast<ResourceConstant>()
        if (this.structureLabSort.size !in arrayOf(3, 6, 10)) return
        val reactionComponent = this.parent.parent.constants.globalConstant.labReactionComponent[reaction]
                ?: return
        if (reactionComponent.size != 2) return

        for (ind in 0..1) {
            if (sourceLab[ind].mineralAmount != 0 && sourceLab[ind].mineralType != reactionComponent[ind]) {
                val takeComponent = sourceLab[ind].mineralAmount
                listTasks.add(listTasks.size,
                        LabFillerTask(sourceLab[ind], terminal, sourceLab[ind].mineralType, takeComponent, takeComponent + 10000))
            } else {
                val needComponent = min((sourceLab[ind].mineralCapacity - sourceLab[ind].mineralAmount), this.getResourceInTerminal(reactionComponent[ind]))
                if (needComponent > creep.carryCapacity) {
                    listTasks.add(listTasks.size,
                            LabFillerTask(terminal, sourceLab[ind], reactionComponent[ind], needComponent, needComponent))
                }
            }
        }

        for (ind in 2 until this.structureLabSort.size) {
            val lab = this.structureLabSort[ind] ?: continue
            if (lab.mineralAmount != 0 && lab.mineralType != reaction) {
                val takeComponent = lab.mineralAmount
                listTasks.add(listTasks.size,
                        LabFillerTask(lab, terminal, lab.mineralType, takeComponent, takeComponent + 10000))
            } else {
                val haveProduction = lab.mineralAmount
                if (haveProduction > creep.carryCapacity) {
                    listTasks.add(listTasks.size,
                            LabFillerTask(lab, terminal, reaction, haveProduction, haveProduction + 5000))
                }
            }
        }
    } else {
        for (ind in 0..1) {
            if (sourceLab[ind].mineralAmount != 0) {
                val takeComponent = sourceLab[ind].mineralAmount
                listTasks.add(listTasks.size,
                        LabFillerTask(sourceLab[ind], terminal, sourceLab[ind].mineralType, takeComponent, takeComponent + 10000))
            }
        }
        for (ind in 2 until this.structureLabSort.size) {
            val lab = this.structureLabSort[ind] ?: continue
            if (lab.mineralAmount != 0) {
                val takeComponent = lab.mineralAmount
                listTasks.add(listTasks.size,
                        LabFillerTask(lab, terminal, lab.mineralType, takeComponent, takeComponent + 10000))
            }
        }
    }

        if (listTasks.isNotEmpty()) {
            val tmpTask: LabFillerTask? = listTasks.toMutableList().maxBy { it.priority }
            if (tmpTask != null)
                parent.parent.tasks.add(creep.id, CreepTask(TypeOfTask.Transport, tmpTask.StructureFrom.id, tmpTask.StructureFrom.pos,
                        tmpTask.StructureTo.id, tmpTask.StructureTo.pos, tmpTask.resource, min(creep.carryCapacity, tmpTask.quantity)))
        }
    }