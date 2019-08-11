package mainRoom

import screeps.api.*

fun MainRoom.doSnapShot() {
    val structures = this.room.find(FIND_STRUCTURES)
    val d: dynamic = object {}
    for ((index, structure) in structures.withIndex()) {
        d[index] = object {}
        d[index]["type"] = structure.structureType
        d[index]["pos"] = structure.pos
    }
    if (Memory["snapShots"] == null) Memory["snapShots"] = object {}
    Memory["snapShots"][this.name] = d
    parent.parent.messenger("INFO", this.name, "Snapshot successful", COLOR_GREEN)
}

fun MainRoom.restoreSnapShot() {
    if (Memory["snapShots"] == null || Memory["snapShots"][this.name] == null){
        parent.parent.messenger("INFO", this.name, "Snapshot not present", COLOR_RED)
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

fun MainRoom.directControl() {
    var flags = this.room.find(FIND_FLAGS).filter { it.color == COLOR_RED && it.secondaryColor == COLOR_GREEN }
    if (flags.isNotEmpty()) this.restoreSnapShot()
    for (flag in flags) flag.remove()

    flags = this.room.find(FIND_FLAGS).filter { it.color == COLOR_RED && it.secondaryColor == COLOR_RED }
    if (flags.isNotEmpty()) this.doSnapShot()
    for (flag in flags) flag.remove()
}