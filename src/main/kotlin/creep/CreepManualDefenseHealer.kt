package creep

import mainContext.MainContext
import screeps.api.*

fun Creep.manualDefenceHealer(mainContext: MainContext) {
    this.manualDefenceMove(mainContext, COLOR_CYAN, "Healer")



    val targetHeal: Creep? = this.room.find(FIND_CREEPS).filter {it.pos.inRangeTo(this.pos,3)
            && it.hits < it.hitsMax}.maxBy { it.hitsMax - it.hits }

    if ( targetHeal != null) this.heal(targetHeal)
    //ToDo overHeal control
}