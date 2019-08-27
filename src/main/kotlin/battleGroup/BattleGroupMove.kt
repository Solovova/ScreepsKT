package battleGroup

import screeps.api.Creep
import screeps.api.RoomPosition
import screeps.utils.copy

fun BattleGroup.chainPushPos(forcePush: Boolean = false) {
    if (this.constants.creeps.size < 2) return
    if (!forcePush
            &&this.constants.creeps[0].posChainMove?.roomName == this.constants.creeps[0].creep?.pos?.roomName
            &&this.constants.creeps[0].posChainMove?.x == this.constants.creeps[0].creep?.pos?.x
            &&this.constants.creeps[0].posChainMove?.y == this.constants.creeps[0].creep?.pos?.y
            &&this.constants.creeps[0].posChainMove != null)  return

    if (forcePush) {
        this.constants.creeps[0].posChainMove = this.constants.creeps[0].creep?.pos?.copy()
        for (ind in (this.constants.creeps.size-1) downTo  1 )
            this.constants.creeps[ind].posChainMove = this.constants.creeps[ind-1].posChainMove?.copy()
    }else{
        for (ind in (this.constants.creeps.size-1) downTo  1 )
            this.constants.creeps[ind].posChainMove = this.constants.creeps[ind-1].posChainMove?.copy()
        this.constants.creeps[0].posChainMove = this.constants.creeps[0].creep?.pos?.copy()
    }
}
//forcePush if i know what creep move
fun BattleGroup.moveTo0Creep(creep: Creep, newPos: RoomPosition, forcePush: Boolean = false) {
    if (forcePush) {
        if (creep.pos.inRangeTo(newPos,0)) return
        this.chainPushPos(forcePush)
        for (cp in constants.creeps) console.log(cp.posChainMove)
        creep.moveTo(newPos)
    }else{
        this.chainPushPos(forcePush)
        for (cp in constants.creeps) console.log(cp.posChainMove)
        if (creep.pos.inRangeTo(newPos,0)) return
        creep.moveTo(newPos)
    }
}

fun BattleGroup.moveChain(creep: Creep, newPos: RoomPosition?) {
    if (newPos == null) return
    if (creep.pos.inRangeTo(newPos,0)) return
    creep.moveTo(newPos)
}



fun BattleGroup.moveToFree(pos: RoomPosition) {
    for (( ind, creepData) in constants.creeps.withIndex()){
        val creep = creepData.creep ?: continue
        if (ind == 0) this.moveTo0Creep(creep,pos)
        else this.moveChain(creep,creepData.posChainMove)
    }
}

fun BattleGroup.moveToChain(pos: RoomPosition) {
    if (!this.allInTheirPos() || !this.creepsFatigue()) return

    for (( ind, creepData) in constants.creeps.withIndex()){
        val creep = creepData.creep ?: continue
        if (ind == 0) this.moveTo0Creep(creep,pos,true)
        else this.moveChain(creep,creepData.posChainMove)
    }
}

fun BattleGroup.creepsFatigue(): Boolean {
    for (creepRecord in this.constants.creeps) {
        val creep = creepRecord.creep ?: return false
        if (creep.fatigue != 0) return false
    }
    return true
}

fun BattleGroup.allInTheirPos(pos: RoomPosition? = null): Boolean {
    for (ind in this.constants.creeps.indices) {
        val creep = this.constants.creeps[ind].creep
        if (pos == null && ind == 0 && creep != null) continue
        val mPos = if (ind == 0) pos else this.constants.creeps[ind].posChainMove
        if (creep == null || mPos == null || creep.pos.x != mPos.x || creep.pos.y != mPos.y || creep.pos.roomName != mPos.roomName)
            return false
    }
    return true
}






