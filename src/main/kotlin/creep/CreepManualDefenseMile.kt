package creep

import mainContext.MainContext
import mainRoom.MainRoom
import screeps.api.*
import mainRoom

fun Creep.manualDefenceMile(mainContext: MainContext) {
    this.manualDefenceMove(mainContext,COLOR_PURPLE,"Mile")
    val mainRoom: MainRoom = mainContext.mainRoomCollector.rooms[this.memory.mainRoom] ?: return
    var target: Creep? = mainRoom.constant.manualDefenceTargetCreep
    if (target == null || !target.pos.inRangeTo(this.pos,1))
        target = this.room.find(FIND_HOSTILE_CREEPS).firstOrNull {it.pos.inRangeTo(this.pos,3)}
    if ( target != null && this.hits > 1000) this.attack(target)
}