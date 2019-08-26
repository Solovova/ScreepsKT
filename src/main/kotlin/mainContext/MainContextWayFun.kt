package mainContext

import constants.CacheCarrier
import mainRoom.MainRoom
import screeps.api.*
import slaveRoom.SlaveRoom
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

        room.find(FIND_CONSTRUCTION_SITES).forEach { cs ->
            if (cs.structureType == STRUCTURE_ROAD)
                costs.set(cs.pos.x, cs.pos.y, 1)
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
        maxOps = 5000
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
        weight = (((SOURCE_ENERGY_CAPACITY +300)*ret.path.size*2).toDouble() / ENERGY_REGEN_TIME).roundToInt()
        fMaxCapacity = min(mainRoom.room.energyCapacityAvailable / 150  *100,1600)
        needCarriers  = weight / fMaxCapacity + 1
        needCapacity = weight / needCarriers / 100 * 100 + 100
        timeForDeath = ret.path.size*2 + 20
        fBody = arrayOf()
        for (i in 0 until (needCapacity/100)) fBody += arrayOf(CARRY, CARRY, MOVE)
    }else{
        weight = if (slaveRoom.constant.model == 2) (((SOURCE_ENERGY_KEEPER_CAPACITY + 1000)*ret.path.size*2).toDouble() / ENERGY_REGEN_TIME).roundToInt()
        else (((SOURCE_ENERGY_CAPACITY +300)*ret.path.size*2).toDouble() / ENERGY_REGEN_TIME).roundToInt()
        fMaxCapacity = min((mainRoom.room.energyCapacityAvailable - 200) / 150 * 100 + 50,1550)
        needCarriers  = weight / fMaxCapacity + 1

        needCapacity = weight / needCarriers / 50 * 50 + 50
        if  (needCapacity / 100 * 100 == needCapacity) needCapacity += 50
        if ( needCapacity>1550) needCapacity = 1550
        timeForDeath = ret.path.size*2 + 20
        fBody = arrayOf(WORK,CARRY,MOVE)
        for (i in 0 until (needCapacity/100)) fBody += arrayOf(CARRY, CARRY, MOVE)
    }


    return CacheCarrier(default = false, tickRecalculate = Game.time, needCarriers = needCarriers, timeForDeath = timeForDeath, needBody = fBody, mPath = ret.path)
}