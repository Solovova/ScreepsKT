package slaveRoom

import screeps.api.*

fun SlaveRoom.doSnapShot() {
    if (this.room == null) return
    val flags = this.room.find(FIND_FLAGS).filter { it.color == COLOR_RED && it.secondaryColor == COLOR_RED }
    if (flags.isEmpty()) return
    for (flag in flags) flag.remove()

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