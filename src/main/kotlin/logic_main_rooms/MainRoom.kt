package logic_main_rooms

import screeps.api.*
import screeps.api.structures.StructureController
import screeps.api.structures.StructureSpawn
import screeps.api.structures.StructureExtension
import utils.messenger

class MainRoom(var name: String) {
    private var room: Room = Game.rooms[this.name] ?: throw AssertionError("Not room $this.name")

    //StructureSpawn
    private var _structureSpawn: Map<String, StructureSpawn>? = null
    val structureSpawn: Map<String, StructureSpawn>
        get() {
            if (this._structureSpawn == null) {
                messenger("RECALCULATE", name, "StructureSpawn", COLOR_YELLOW)
                _structureSpawn = this.room.find(FIND_STRUCTURES).filter { it.structureType == STRUCTURE_SPAWN }.associate { it.id to it as StructureSpawn }
            }
            return _structureSpawn ?: throw AssertionError("Error get StructureSpawn")
        }

    //StructureExtension
    private var _structureExtension: Map<String, StructureExtension>? = null
    val structureExtension: Map<String, StructureExtension>
        get() {
            if (this._structureExtension == null) {
                messenger("RECALCULATE", name, "StructureExtension", COLOR_YELLOW)
                _structureExtension = this.room.find(FIND_STRUCTURES).filter { it.structureType == STRUCTURE_EXTENSION }.associate { it.id to it as StructureExtension }
            }
            return _structureExtension ?: throw AssertionError("Error get StructureExtension")
        }

    //StructureController
    private var _structureController: StructureController? = null
    val structureController: StructureController
        get() {
            if (this._structureController == null) {
                messenger("RECALCULATE", name, "StructureController", COLOR_YELLOW)
                _structureController = this.room.find(FIND_STRUCTURES).firstOrNull { it.structureType == STRUCTURE_CONTROLLER } as StructureController
            }
            return _structureController ?: throw AssertionError("Error get StructureController")
        }

    //Source
    private var _source: Map<String, Source>? = null
    val source: Map<String, Source>
        get() {
            if (this._source == null) {
                messenger("RECALCULATE", name, "Source", COLOR_YELLOW)
                _source = this.room.find(FIND_SOURCES).associateBy { it.id }
            }
            return _source ?: throw AssertionError("Error get Source")
        }


}