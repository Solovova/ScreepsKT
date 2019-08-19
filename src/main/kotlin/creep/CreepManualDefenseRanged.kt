package creep

import mainContext.MainContext
import mainRoom
import mainRoom.MainRoom
import screeps.api.*

fun Creep.manualDefenceRanged(mainContext: MainContext) {
    this.manualDefenceMove(mainContext,COLOR_BLUE,"Ranged")
    val mainRoom: MainRoom = mainContext.mainRoomCollector.rooms[this.memory.mainRoom] ?: return

    var target: Creep? = mainRoom.constant.manualDefenceTargetCreep
    if (target == null || !target.pos.inRangeTo(this.pos,3)) {
        target = this.room.find(FIND_HOSTILE_CREEPS).firstOrNull {it.pos.inRangeTo(this.pos,3)
                && it.body.any { part -> part.type == HEAL }
        }
        if (target == null)
            target = this.room.find(FIND_HOSTILE_CREEPS).firstOrNull {it.pos.inRangeTo(this.pos,3)}
    }

    if ( target != null) this.rangedAttack(target)
}