package slaveRoom

import mainRoom.MainRoom
import mainRoom.doSnapShot
import screeps.api.*

fun SlaveRoom.doSnapShot() {
    if (this.room == null) return
    val flagsIgnore = this.room.find(FIND_FLAGS).filter { it.color == COLOR_RED && it.secondaryColor == COLOR_WHITE }


    val structures = this.room.find(FIND_STRUCTURES)
    val d: dynamic = object {}
    var index = 0
    for (structure in structures) {
        if (flagsIgnore.firstOrNull {it.pos.x == structure.pos.x && it.pos.y == structure.pos.y} != null) continue
        d[index] = object {}
        d[index]["type"] = structure.structureType
        d[index]["pos"] = structure.pos
        index++
    }
    if (Memory["snapShots"] == null) Memory["snapShots"] = object {}
    Memory["snapShots"][this.name] = d
    parent.parent.parent.messenger("INFO", this.name, "Snapshot successful", COLOR_GREEN)

    for (flagIgnore in flagsIgnore) flagIgnore.remove()
}

fun SlaveRoom.restoreSnapShot() {
    if (this.room == null) return
    if (Memory["snapShots"] == null || Memory["snapShots"][this.name] == null){
        parent.parent.parent.messenger("INFO", this.name, "Snapshot slave room not present", COLOR_RED)
        return
    }
    val d: dynamic = Memory["snapShots"][this.name]
    var index = 0
    while (true) {
        if (Memory["snapShots"][this.name][index] == null) break
        val roomPosition = RoomPosition(d[index]["pos"]["x"] as Int,d[index]["pos"]["y"] as Int,d[index]["pos"]["roomName"] as String)
        val structureType: StructureConstant = d[index]["type"].unsafeCast<StructureConstant>()
        this.room.createConstructionSite(roomPosition,structureType)
        index++
    }
}

fun SlaveRoom.directControl() {
    if (this.room == null) return
    var flags = this.room.find(FIND_FLAGS).filter { it.color == COLOR_RED && it.secondaryColor == COLOR_GREEN }
    if (flags.isNotEmpty()) this.restoreSnapShot()
    for (flag in flags) flag.remove()

    flags = this.room.find(FIND_FLAGS).filter { it.color == COLOR_RED && it.secondaryColor == COLOR_RED }
    if (flags.isNotEmpty()) this.doSnapShot()
    for (flag in flags) flag.remove()
}