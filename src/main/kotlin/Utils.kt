import dataCache.CacheCarrier
import mainRoom.MainRoom
import screeps.api.*
import kotlin.math.min
import kotlin.math.roundToInt

fun messenger(type: String, room: String, text: String, color: ColorConstant) {
    if (type == "TASK") return
    if (type == "TEST") return

    var fHTMLColor = ""
    if (color === COLOR_YELLOW) fHTMLColor = "yellow"
    if (color === COLOR_RED) fHTMLColor = "red"
    if (color === COLOR_GREEN) fHTMLColor = "green"
    if (color === COLOR_BLUE) fHTMLColor = "blue"
    if (color === COLOR_ORANGE) fHTMLColor = "orange"
    if (color === COLOR_CYAN) fHTMLColor = "cyan"

    var showText = text
    if (fHTMLColor !== "") showText = "<font color=$fHTMLColor > $text </font>"
    console.log("$type : $room  $showText")
}

fun arrayCopy(ar0: Array<Int>, ar1: Array<Int>) {
    ar1.forEachIndexed { index, value -> ar0[index] = value }
}



fun getWayFromPosToPos(fPos1: RoomPosition,fPos2: RoomPosition): PathFinder.Path {
    fun roomCallback(roomName: String): CostMatrix {
        val room: Room = Game.rooms[roomName] ?: return PathFinder.CostMatrix()
        val costs = PathFinder.CostMatrix()
        room.find(FIND_MY_STRUCTURES).forEach { struct ->
            if (struct.structureType === STRUCTURE_ROAD) {
                costs.set(struct.pos.x, struct.pos.y, 1)
            } else if (struct.structureType !== STRUCTURE_CONTAINER &&
                    (struct.structureType !== STRUCTURE_RAMPART)) {
                costs.set(struct.pos.x, struct.pos.y, 0xff)
            }
        }
        room.find(FIND_HOSTILE_STRUCTURES).forEach { struct ->
            if (struct.structureType === STRUCTURE_ROAD) {
                costs.set(struct.pos.x, struct.pos.y, 1)
            } else {
                costs.set(struct.pos.x, struct.pos.y, 0xff)
            }
        }
        return costs
    }

    val goals = object : PathFinder.GoalWithRange {
        override var pos : RoomPosition = fPos2
        override var range: Int = 1
    }

    return PathFinder.search(fPos1, goals, options {
        maxOps = 3000
        maxRooms = 6
        plainCost = 2
        swampCost = 10
        roomCallback = :: roomCallback
    })
}

fun getCarrierAuto (ret: PathFinder.Path, mainRoom: MainRoom): CacheCarrier {
    val weight: Int = (SOURCE_ENERGY_CAPACITY+500)*ret.path.size*2 / 300
    val fMaxCapacity = min((mainRoom.room.energyCapacityAvailable.toDouble() / 150).roundToInt() *100,1600)
    val needCarriers = (weight.toDouble() / fMaxCapacity).roundToInt() + 1
    val needCapacity = (weight.toDouble()/needCarriers/100).roundToInt()*100 + 100
    val timeForDeath = ret.path.size*2 + 20
    var fBody : Array<BodyPartConstant> = arrayOf()
    for (i in 0 until (needCapacity/100)) fBody += arrayOf(CARRY, CARRY, MOVE)

    return CacheCarrier(default = false, tickRecalculate = Game.time, needCarriers = needCarriers, timeForDeath = timeForDeath, needBody = fBody)
}
