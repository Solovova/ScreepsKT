package slaveRoom

import constants.CacheCarrier
import mainContext.getCacheRecordRoom
import screeps.api.*
import screeps.api.structures.Structure

fun SlaveRoom.buildWayByPath(inPath : Array<RoomPosition>):Boolean {
    var builden: Int = 0
    for (record in inPath.reversedArray()) {
        if (record.x == 0 || record.x == 49 || record.y == 0 || record.y == 49) continue
        val room: Room = Game.rooms[record.roomName] ?: return false
        val fFind: Array<Structure> = (room.lookForAt(LOOK_STRUCTURES,record.x, record.y) ?: arrayOf()).filter { it.structureType == STRUCTURE_ROAD }.toTypedArray()
        if (fFind.isEmpty())
            if (room.createConstructionSite(record, STRUCTURE_ROAD) != OK) return false
        else builden ++
    }
    return builden == 0
}

fun SlaveRoom.buildWaysInRoom():Boolean {
    if (this.constructionSite.isNotEmpty() || this.parent.constructionSite.isNotEmpty()) return false

    for (ind in 0..1) {
        if (this.structureContainerNearSource.containsKey(ind)) {
            val cacheCarrier: CacheCarrier = this.parent.parent.parent.getCacheRecordRoom("slaveContainer$ind", mainRoom = this.parent,
                    slaveRoom = this, inSwampCost = 6, inPlainCost = 6, recalculate = true)
                    ?: return false
            if (cacheCarrier.mPath.isEmpty()) return false
            if (!this.buildWayByPath(cacheCarrier.mPath)) return false
        }
    }


    if (this.structureContainerNearSource.size != this.source.size) return false

    return true
}

