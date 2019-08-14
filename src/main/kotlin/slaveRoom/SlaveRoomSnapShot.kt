package slaveRoom

import mainContext.messenger
import screeps.api.*
import snapshotDeserialize
import snapshotSerialize
import RecordOfStructurePosition

fun SlaveRoom.doSnapShot() {
    if (this.room == null) return
    val structures = this.room.find(FIND_STRUCTURES)
    val flagsIgnore = this.room.find(FIND_FLAGS).filter { it.color == COLOR_RED && it.secondaryColor == COLOR_WHITE }
    val structureFiltered = structures.filter {
        for (flag in flagsIgnore)
            if (it.pos.roomName == flag.pos.roomName && it.pos.x == flag.pos.x && it.pos.y == flag.pos.y)
            return@filter false
        return@filter true
    }.toTypedArray()
    for (flag in flagsIgnore) flag.remove()
    if (Memory["snap"] == null) Memory["snap"] = object {}
    Memory["snap"][this.name] = snapshotSerialize(structureFiltered)
}

fun SlaveRoom.restoreSnapShot(){
    if (this.room == null) return
    if (this.room.find(FIND_CONSTRUCTION_SITES).isNotEmpty()) return
    if (Memory["snap"] == null || Memory["snap"][this.name] == null){
        parent.parent.parent.messenger("INFO", this.name, "Slave snapshot not present", COLOR_RED)
        return
    }
    val d:Array<RecordOfStructurePosition> = snapshotDeserialize(Memory["snap"][this.name] as String,this.name)
    for (record in d)
        this.room.createConstructionSite(record.roomPosition,record.structureConstant)
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