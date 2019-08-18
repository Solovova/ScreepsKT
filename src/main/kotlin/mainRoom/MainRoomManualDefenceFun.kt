package mainRoom

import mainRoom
import role
import screeps.api.*
import screeps.utils.toMap
import subRole

fun MainRoom.manualDefenceInStartOfTick() {
    // Group GoTo
    val flagGoTo: Flag? = parent.flags.firstOrNull {
        it.color == this.constant.manualDefenceRoomMainColorFlag
                && it.secondaryColor == COLOR_RED
    }

    if (flagGoTo != null) this.constant.manualDefenceGroupPos = flagGoTo.pos
    else this.constant.manualDefenceGroupPos = null

    // Group move
    var dx = 0
    var dy = 0
    var groupCanMove = true
    var groupFreeWay = true
    var groupNeedMove = false

    data class RecordMove(val color: ColorConstant, val dx: Int, val dy: Int)

    val listMove = listOf(
            RecordMove(COLOR_GREEN, 0, 1),
            RecordMove(COLOR_YELLOW, 0, -1),
            RecordMove(COLOR_ORANGE, -1, 0),
            RecordMove(COLOR_BROWN, 1, 0))
    for (listMoveRecord in listMove) {

        val flagMove: Flag? = parent.flags.firstOrNull {
            it.color == this.constant.manualDefenceRoomMainColorFlag
                    && it.secondaryColor == listMoveRecord.color
        }

        if (flagMove != null) {
            dx += listMoveRecord.dx
            dy += listMoveRecord.dy
            groupNeedMove = true
        }
    }

    //have dx,dy
    val creeps = Game.creeps.values.toList().filter { it.memory.mainRoom == this.name
            && it.memory.role in arrayOf(20,21,22) }


    var roomTerrain:Room.Terrain? = null
    for (creep in creeps) {
        val flag:Flag = Game.flags[creep.memory.subRole] ?: continue
        if (creep.fatigue != 0) groupCanMove = false
        val posNext = RoomPosition(flag.pos.x + dx,flag.pos.y + dy, flag.pos.roomName)

        if (roomTerrain == null) roomTerrain = Game.map.getRoomTerrain(posNext.roomName)

        if (roomTerrain.get(posNext.x,posNext.y) == TERRAIN_MASK_WALL) groupFreeWay = false
    }

    if (groupCanMove && groupFreeWay && groupNeedMove) {
        for (creep in creeps) {
            val flag:Flag = Game.flags[creep.memory.subRole] ?: continue
            val posNext = RoomPosition(flag.pos.x + dx,flag.pos.y + dy, flag.pos.roomName)
            flag.setPosition(posNext)
        }



        val flagOneTurn: Flag? = parent.flags.firstOrNull {
            it.color == this.constant.manualDefenceRoomMainColorFlag
                    && it.secondaryColor == COLOR_WHITE
        }

        if (flagOneTurn != null) {
            for (listMoveRecord in listMove) {
                val flagMove: Flag? = parent.flags.firstOrNull {
                    it.color == this.constant.manualDefenceRoomMainColorFlag
                            && it.secondaryColor == listMoveRecord.color
                }
                flagMove?.remove()
            }
        }
    }

    //Group target
    val flagTarget: Flag? = parent.flags.firstOrNull {
        it.color == this.constant.manualDefenceRoomMainColorFlag
                && it.secondaryColor == COLOR_GREY
    }

    this.constant.manualDefenceTargetCreep = null
    if (flagTarget != null) {
        val tRoom = Game.rooms[flagTarget.pos.roomName]
        if (tRoom != null) {
            this.constant.manualDefenceTargetCreep = tRoom.find(FIND_HOSTILE_CREEPS).firstOrNull { it.pos.inRangeTo(flagTarget.pos, 0) }
        }
    }
}