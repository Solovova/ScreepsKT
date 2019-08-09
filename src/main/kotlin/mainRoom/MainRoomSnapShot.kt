package mainRoom

import screeps.api.*

fun MainRoom.doSnapShot() {
    val flags = this.room.find(FIND_FLAGS).filter { it.color == COLOR_RED && it.secondaryColor == COLOR_RED }
    if (flags.isEmpty()) return
    for (flag in flags) flag.remove()

    val structures = this.room.find(FIND_STRUCTURES)
    val d: dynamic = object {}
    for ((index, structure) in structures.withIndex()) {
        d[index] = object {}
        d[index]["type"] = structure.structureType
        d[index]["pos"] = structure.pos
    }
    if (Memory["snapShots"] == null) Memory["snapShots"] = object {}
    Memory["snapShots"][this.name] = d
    parent.parent.messenger("TEST", this.name, "Snapshot successful", COLOR_GREEN)


}