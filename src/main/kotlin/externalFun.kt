import screeps.api.*
import screeps.api.structures.Structure
import screeps.utils.mutableRecordOf
import screeps.utils.recordOf

val zipStructure: Map<String,String> = mapOf(
        "0" to STRUCTURE_SPAWN.toString(),
        "1" to STRUCTURE_EXTENSION.toString(),
        "2" to STRUCTURE_ROAD.toString(),
        "3" to STRUCTURE_WALL.toString(),
        "4" to STRUCTURE_RAMPART.toString(),
        "5" to STRUCTURE_KEEPER_LAIR.toString(),
        "6" to STRUCTURE_PORTAL.toString(),
        "7" to STRUCTURE_CONTROLLER.toString(),
        "8" to STRUCTURE_LINK.toString(),
        "9" to STRUCTURE_STORAGE.toString(),
        "a" to STRUCTURE_TOWER.toString(),
        "b" to STRUCTURE_OBSERVER.toString(),
        "c" to STRUCTURE_POWER_BANK.toString(),
        "d" to STRUCTURE_POWER_SPAWN.toString(),
        "e" to STRUCTURE_EXTRACTOR.toString(),
        "f" to STRUCTURE_LAB.toString(),
        "g" to STRUCTURE_TERMINAL.toString(),
        "h" to STRUCTURE_CONTAINER.toString(),
        "i" to STRUCTURE_NUKER.toString()
)

fun snapshotSerialize(arrayStructures : Array<Structure>) : String {
    var result = ""
    for (record in arrayStructures){
        val key = zipStructure.filterValues { it == record.structureType.toString() }.keys
        if (key.isEmpty()) continue
        result += "${key.first()},${record.pos.x},${record.pos.y};"
    }
    return result
}

data class RecordOfStructurePosition(val structureConstant: StructureConstant, val roomPosition: RoomPosition)

fun snapshotDeserialize(str: String, name: String): Array<RecordOfStructurePosition> {
    var result:Array<RecordOfStructurePosition> = arrayOf()
    val elements = str.split(";")
    for (element in elements) {
        val struct = element.split(",")
        if (struct.size!=3) continue
        result += RecordOfStructurePosition( (zipStructure[struct[0]] ?: STRUCTURE_ROAD).unsafeCast<StructureConstant>(), RoomPosition(struct[1].toInt(),struct[2].toInt(),name))
    }
    return result
}

fun String.toSecDigit(): String {
    var result = ""
    for (ind in 0 .. ((this.length - 1) / 3)){
        val startIndex = ind * 3
        val endIndex = if ((startIndex + 3)<this.length) (startIndex + 3) else this.length

        result = "${this.subSequence(this.length - startIndex,this.length - endIndex)} " + result
    }
    return result
}