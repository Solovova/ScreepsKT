package creep

import mainContext.MainContext
import mainContext.messenger
import mainRoom
import mainRoom.MainRoom
import role
import screeps.api.*
import screeps.utils.toMap
import subRole

fun Creep.manualDefenceGetMyFlag(color: ColorConstant): Flag? {

    val flag: Flag? = this.room.find(FIND_FLAGS).firstOrNull { it.name == this.memory.subRole }
    if (flag == null) {
        this.memory.subRole = ""
        val flags = this.room.find(FIND_FLAGS).filter { it.color == color && it.secondaryColor == color }
        val anotherCreep = Game.creeps.toMap().filter {
            it.value.memory.mainRoom == this.memory.mainRoom
                    && it.value.memory.role == this.memory.role
        }
        for (flagRecord in flags)
            if (anotherCreep.filter { it.value.memory.subRole == flagRecord.name }.isEmpty()) {
                this.memory.subRole = flagRecord.name
                break
            }
    } else return flag

    return this.room.find(FIND_FLAGS).firstOrNull { it.name == this.memory.subRole }
}

fun Creep.manualDefenceMove(mainContext: MainContext, color: ColorConstant, role: String) {
    val mainRoom: MainRoom = mainContext.mainRoomCollector.rooms[this.memory.mainRoom] ?: return
    val groupPos = mainRoom.constant.manualDefenceGroupPos
    if (groupPos != null && groupPos.roomName != this.pos.roomName) {
        if (!this.pos.inRangeTo(groupPos, 1)) this.moveTo(groupPos)
    }else{
        val flag: Flag? = this.manualDefenceGetMyFlag(color)
        if (flag == null) {
            mainContext.messenger("ERROR", this.memory.mainRoom, "$role id: ${this.id} cant find flag", COLOR_RED)
            return
        }
        if (!this.pos.inRangeTo(flag.pos, 0)) this.moveTo(flag)
    }
}






