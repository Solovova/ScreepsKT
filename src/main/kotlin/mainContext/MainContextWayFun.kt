package mainContext

import constants.CacheCarrier
import mainRoom.MainRoom
import screeps.api.*
import slaveRoom.SlaveRoom
import kotlin.js.Math
import kotlin.math.min
import kotlin.math.roundToInt

fun MainContext.getWayFromPosToPos(fPos1: RoomPosition, fPos2: RoomPosition, inSwampCost: Int = 10, inPlainCost: Int = 2): PathFinder.Path {
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

        room.find(FIND_STRUCTURES).forEach { struct ->
            if (struct.structureType == STRUCTURE_ROAD)
                costs.set(struct.pos.x, struct.pos.y, 1)
        }

        room.find(FIND_HOSTILE_STRUCTURES).forEach { struct ->
            costs.set(struct.pos.x, struct.pos.y, 0xff)
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
        plainCost = inSwampCost
        swampCost = inPlainCost
        roomCallback = :: roomCallback
    })
}

fun MainContext.getCarrierAuto (ret: PathFinder.Path, mainRoom: MainRoom, slaveRoom: SlaveRoom?): CacheCarrier {
    //ToDo SOURCE_ENERGY_KEEPER_CAPACITY
    val weight: Int
    val fMaxCapacity: Int
    val needCarriers: Int
    var needCapacity: Int
    val timeForDeath: Int
    var fBody : Array<BodyPartConstant>
    if (slaveRoom == null) {
        weight = (SOURCE_ENERGY_CAPACITY +500)*ret.path.size*2 / ENERGY_REGEN_TIME
        fMaxCapacity = min((mainRoom.room.energyCapacityAvailable.toDouble() / 150).roundToInt() *100,1600)
        needCarriers  = (weight.toDouble() / fMaxCapacity).roundToInt() + 1
        needCapacity = (weight.toDouble()/needCarriers/100).roundToInt()*100 + 100
        timeForDeath = ret.path.size*2 + 20
        fBody = arrayOf()
        for (i in 0 until (needCapacity/100)) fBody += arrayOf(CARRY, CARRY, MOVE)
    }else{
        weight = (SOURCE_ENERGY_CAPACITY +500)*ret.path.size*2 / ENERGY_REGEN_TIME
        fMaxCapacity = min(((mainRoom.room.energyCapacityAvailable - 200).toDouble() / 150).roundToInt() *100 + 50,1550)
        needCarriers  = (weight.toDouble() / fMaxCapacity).roundToInt() + 1
        needCapacity = (weight.toDouble()/needCarriers/50).roundToInt()*50 + 50
        if  (((needCapacity.toDouble() / 100.0) * 100.0).roundToInt() == needCapacity) needCapacity += 50
        if (needCapacity>1550) needCapacity = 1550

        timeForDeath = ret.path.size*2 + 20
        fBody = arrayOf(WORK,CARRY,MOVE)
        for (i in 0 until (needCapacity/100)) fBody += arrayOf(CARRY, CARRY, MOVE)
    }


    return CacheCarrier(default = false, tickRecalculate = Game.time, needCarriers = needCarriers, timeForDeath = timeForDeath, needBody = fBody, mPath = ret.path)
}