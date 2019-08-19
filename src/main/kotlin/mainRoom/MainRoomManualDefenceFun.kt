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
    var borderLeft = 0
    var borderTop = 0
    var borderRight = 49
    var borderBottom = 49

    var groupCanMove = true
    var groupFreeWay = true
    var groupNeedMove = false
    var groupInFlags = true

    data class RecordMove(val color: ColorConstant, val dx: Int, val dy: Int, val direction: String)

    val listMove = listOf(
            RecordMove(COLOR_GREEN, 1, 0, "right"),
            RecordMove(COLOR_YELLOW, -1, 0, "left"),
            RecordMove(COLOR_ORANGE, 0, -1, "top"),
            RecordMove(COLOR_BROWN, 0, 1, "bottom"))

    val flagBorder: Flag? = parent.flags.firstOrNull {
        it.color == this.constant.manualDefenceRoomMainColorFlag
                && it.secondaryColor == COLOR_WHITE
    }

    for (listMoveRecord in listMove) {

        val flagMove: Flag? = parent.flags.firstOrNull {
            it.color == this.constant.manualDefenceRoomMainColorFlag
                    && it.secondaryColor == listMoveRecord.color
        }

        if (flagMove != null) {
            dx += listMoveRecord.dx
            dy += listMoveRecord.dy
            groupNeedMove = true
            if (flagBorder != null)
                when (listMoveRecord.direction) {
                    "right" -> borderRight = flagBorder.pos.x-1
                    "left" -> borderLeft = flagBorder.pos.x+1
                    "top" -> borderTop = flagBorder.pos.y+1
                    "bottom" -> borderBottom = flagBorder.pos.y-1
                }
        }
    }

    console.log("$borderLeft $borderRight $borderTop $borderBottom")

    //have dx,dy
    val creeps = Game.creeps.values.toList().filter {
        it.memory.mainRoom == this.name
                && it.memory.role in arrayOf(20, 21, 22)
    }
    //ToDo solve problem
    var roomTerrain: Room.Terrain? = null
    for (creep in creeps) {
        val flag: Flag = Game.flags[creep.memory.subRole] ?: continue
        if (creep.fatigue != 0) {
            groupCanMove = false
            break
        }

        if (creep.pos.x != flag.pos.x || creep.pos.y != flag.pos.y) {
            groupInFlags = false
            break
        }

        val posNext = RoomPosition(flag.pos.x + dx, flag.pos.y + dy, flag.pos.roomName)

        if (roomTerrain == null) roomTerrain = Game.map.getRoomTerrain(posNext.roomName)
        //ToDo add structure and creep free way calculate
        //ToDo add structure target
        if (roomTerrain.get(posNext.x, posNext.y) == TERRAIN_MASK_WALL) groupFreeWay = false
        if (posNext.x !in borderLeft..borderRight || posNext.y !in borderTop..borderBottom) groupFreeWay = false
    }


    if (groupCanMove && groupFreeWay && groupNeedMove && groupInFlags) {
        for (creep in creeps) {
            val flag: Flag = Game.flags[creep.memory.subRole] ?: continue
            val posNext = RoomPosition(flag.pos.x + dx, flag.pos.y + dy, flag.pos.roomName)
            flag.setPosition(posNext)
            creep.moveTo(posNext)
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