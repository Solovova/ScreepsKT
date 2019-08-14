package mainRoom

import mainContext.messenger
import screeps.api.*
import snapshotDeserialize
import snapshotSerialize
import RecordOfStructurePosition

fun MainRoom.doSnapShot() {
    val structures = this.room.find(FIND_STRUCTURES)
    if (Memory["snap"] == null) Memory["snap"] = object {}
    Memory["snap"][this.name] = snapshotSerialize(structures)
}

fun MainRoom.restoreSnapShot(){
    if (this.room.find(FIND_CONSTRUCTION_SITES).isNotEmpty()) return
    if (Memory["snap"] == null || Memory["snap"][this.name] == null){
        parent.parent.messenger("INFO", this.name, "Snapshot not present", COLOR_RED)
        return
    }
    val d:Array<RecordOfStructurePosition> = snapshotDeserialize(Memory["snap"][this.name] as String,this.name)
    for (record in d)
        this.room.createConstructionSite(record.roomPosition,record.structureConstant)
}

fun MainRoom.directControl() {
    var flags = this.room.find(FIND_FLAGS).filter { it.color == COLOR_RED && it.secondaryColor == COLOR_GREEN }
    if (flags.isNotEmpty()) this.restoreSnapShot()
    for (flag in flags) flag.remove()

    flags = this.room.find(FIND_FLAGS).filter { it.color == COLOR_RED && it.secondaryColor == COLOR_RED }
    if (flags.isNotEmpty()) this.doSnapShot()
    for (flag in flags) flag.remove()
}