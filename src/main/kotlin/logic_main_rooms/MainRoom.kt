package logic_main_rooms

import screeps.api.*
import screeps.api.structures.StructureSpawn
import screeps.api.structures.StructureExtension

class MainRoom {
    var name: String
    private var room: Room

    //StructureSpawn
    private var _structureSpawn: Map<String, StructureSpawn>? = null
    val structureSpawn: Map<String, StructureSpawn>
        get() {
            if (this._structureSpawn == null) {
                console.log("Recalculate _structureSpawn")
                _structureSpawn = this.room.find(FIND_STRUCTURES).filter { it.structureType == STRUCTURE_SPAWN}.associate { it.id to it as StructureSpawn}
            }
            return _structureSpawn ?: throw AssertionError("Set to null by another thread")
        }

    //StructureExtension
    private var _structureExtension: Map<String, StructureExtension>? = null
    val structureExtension: Map<String, StructureExtension>
        get() {
            if (this._structureExtension == null) {
                console.log("Recalculate _structureExtension")
                _structureExtension = this.room.find(FIND_STRUCTURES).filter { it.structureType == STRUCTURE_EXTENSION}.associate { it.id to it as StructureExtension}
            }
            return _structureExtension ?: throw AssertionError("Set to null by another thread")
        }


    constructor(name: String) {
        this.name = name
        this.room = Game.rooms[this.name] ?: throw AssertionError("Not room $this.name")
    }
}