package mainRoom

import screeps.api.Creep
import screeps.api.ResourceConstant
import LabFillerTask
import screeps.api.RESOURCE_ENERGY
import CreepTask
import kotlin.math.min

fun MainRoom.setLabFillerTask(creep: Creep) {
    val terminal = this.structureTerminal[0] ?: return
    val storage = this.structureStorage[0] ?: return
    val reaction = this.constant.reactionActive.unsafeCast<ResourceConstant>()
    if (this.structureLabSort.size !in arrayOf(3, 6, 10)) return
    val reactionComponent = this.parent.parent.constants.globalConstant.labReactionComponent[reaction]
            ?: return
    if (reactionComponent.size != 2) return
    val lab0 = this.structureLabSort[0] ?: return
    val lab1 = this.structureLabSort[1] ?: return

    var listTasks: MutableList<LabFillerTask> = mutableListOf()

    //energy -> labs
//    for (lab in this.structureLab.values){
//        val needEnergy:Int = lab.energyCapacity-lab.energy
//        listTasks.add(listTasks.size,
//                LabFillerTask(storage,lab, RESOURCE_ENERGY,needEnergy,needEnergy))
//    }
    //ToDo check quantity in terminal
    val needComponent0 = min((lab0.mineralCapacity - lab0.mineralAmount),this.getResourceInTerminal(reactionComponent[0]))
    listTasks.add(listTasks.size,
            LabFillerTask(terminal, lab0, reactionComponent[0], needComponent0, needComponent0))

    val needComponent1 = min((lab1.mineralCapacity - lab1.mineralAmount),this.getResourceInTerminal(reactionComponent[1]))
    listTasks.add(listTasks.size,
            LabFillerTask(terminal, lab1, reactionComponent[1], needComponent1, needComponent1))

    for (ind in 2 until this.structureLabSort.size) {
        val lab = this.structureLabSort[ind] ?: continue
        val haveProduction = lab.mineralAmount
        listTasks.add(listTasks.size,
                LabFillerTask(lab, terminal, reaction, haveProduction, haveProduction + 5000))
    }




    if (listTasks.isNotEmpty()) {
        val tmpTask: LabFillerTask? = listTasks.filter { it.quantity >= creep.carryCapacity }.toMutableList().maxBy { it.priority }
        if (tmpTask != null)
            parent.parent.tasks.add(creep.id, CreepTask(TypeOfTask.Transport, tmpTask.StructureFrom.id, tmpTask.StructureFrom.pos,
                    tmpTask.StructureTo.id, tmpTask.StructureTo.pos, tmpTask.resource, creep.carryCapacity))
    }
}