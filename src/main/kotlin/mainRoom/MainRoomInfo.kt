package mainRoom

import creep.getDescribeForQueue
import TypeOfMainRoomInfo
import MainRoomInfoRecord
import mainContext.messenger
import screeps.api.*
import screeps.api.structures.StructureStorage
import screeps.api.structures.StructureTerminal
import screeps.utils.toMap

fun MainRoom.getInfoQueue(): MainRoomInfoRecord {
    var textSpawning = ""
    for (spawn in this.structureSpawn) {
        val recordSpawning = spawn.value.spawning
        if (recordSpawning != null) {
            val creep: Creep? = Game.creeps[recordSpawning.name]
            textSpawning += creep?.getDescribeForQueue(parent.parent) ?: ""
        }
    }

    var showText = textSpawning.padEnd(45) + ":"


    for (record in this.queue) {
        var prefix = ""
        if (record.mainRoom != record.slaveRoom)
            prefix = this.slaveRooms[record.slaveRoom]?.describe ?: "und"
        showText += "$prefix ${record.role},"
    }

    val alarm = (this.queue.size > 4)
    return MainRoomInfoRecord(showText, alarm)
}

fun MainRoom.getInfoController(): MainRoomInfoRecord {
    var result = "l: ${this.structureController[0]?.level} "
    if (this.structureController[0]?.level == 8) return MainRoomInfoRecord(result, false)
    result += "${this.structureController[0]?.progress}".padStart(9)
    result += "/ "
    result += "${this.structureController[0]?.progressTotal}".padStart(9)
    result += "  "
    result += "${(this.structureController[0]?.progressTotal
            ?: 0) - (this.structureController[0]?.progress ?: 0)}".padStart(9)
    return MainRoomInfoRecord(result, false)
}

fun MainRoom.getInfoRoomLevel(): MainRoomInfoRecord {
    val level = this.getLevelOfRoom()
    return if (level >= this.constant.levelOfRoom)
        MainRoomInfoRecord("$level", false)
    else MainRoomInfoRecord("$level/${this.constant.levelOfRoom}", true)
}

fun MainRoom.getInfoNeedBuild(): MainRoomInfoRecord {
    val resultEmpty = MainRoomInfoRecord("", false)

    if (this.constructionSite.isNotEmpty()) return resultEmpty
    this.structureController[0] ?: return resultEmpty
    val answer: String = this.missingStructures()
    if (answer=="") return resultEmpty
    return MainRoomInfoRecord(answer,true)
}

fun MainRoom.getInfoConstructionSites(): MainRoomInfoRecord {
    val resultEmpty = MainRoomInfoRecord("", false)

    if (this.constructionSite.isEmpty()) return resultEmpty
    this.structureController[0] ?: return resultEmpty
    return MainRoomInfoRecord("build: ${this.constructionSite.size}",true)
}

fun MainRoom.getInfoEnergy(): MainRoomInfoRecord {
    val fullEnergy = this.getResource()
    val buyEnergy = if (this.constant.marketBuyEnergy) "buy" else ""
    return MainRoomInfoRecord("e: $fullEnergy $buyEnergy", fullEnergy <= 50000)
}

fun MainRoom.getInfoPlaceInStorage(): MainRoomInfoRecord {
    val resultEmpty = MainRoomInfoRecord("", false)
    val storage:StructureStorage = this.structureStorage[0] ?: return resultEmpty
    val placeInStorage: Int = storage.storeCapacity - storage.store.toMap().map { it.value }.sum()
    return if (placeInStorage<100000) MainRoomInfoRecord("sp: $placeInStorage", true)
    else return resultEmpty
}

fun MainRoom.getInfoPlaceInTerminal(): MainRoomInfoRecord {
    val resultEmpty = MainRoomInfoRecord("", false)
    val terminal:StructureTerminal = this.structureTerminal[0] ?: return resultEmpty
    val placeInTerminal: Int = terminal.storeCapacity - terminal.store.toMap().map { it.value }.sum()
    return if (placeInTerminal<50000) MainRoomInfoRecord("tp: $placeInTerminal", true)
    else return resultEmpty
}

fun MainRoom.getInfoNeedUpgrade(): MainRoomInfoRecord {
    val resultEmpty = MainRoomInfoRecord("", false)
    return MainRoomInfoRecord(if (this.constant.defenceNeedUpgrade) "upgrade" else "",
            this.constant.defenceNeedUpgrade)
}

fun MainRoom.getInfoReactionInfo(): MainRoomInfoRecord {
    val resultEmpty = MainRoomInfoRecord("", false)
    if (this.structureLabSort.size !in arrayOf(3,6,10)) return resultEmpty
    return MainRoomInfoRecord("${this.structureLabSort.size}: ${this.constant.reactionActive}",
            this.constant.reactionActive == "")
}


fun MainRoom.getInfo(): Map<TypeOfMainRoomInfo, MainRoomInfoRecord> {
    val result: MutableMap<TypeOfMainRoomInfo, MainRoomInfoRecord> = mutableMapOf()
    result[TypeOfMainRoomInfo.infoQueue] = this.getInfoQueue()
    result[TypeOfMainRoomInfo.infoController] = this.getInfoController()
    result[TypeOfMainRoomInfo.infoRoomName] = MainRoomInfoRecord(this.name, false)
    result[TypeOfMainRoomInfo.infoRoomDescribe] = MainRoomInfoRecord(this.describe, false)
    result[TypeOfMainRoomInfo.infoRoomLevel] = this.getInfoRoomLevel()
    result[TypeOfMainRoomInfo.infoNeedBuild] = this.getInfoNeedBuild()
    result[TypeOfMainRoomInfo.infoConstructionSites] = this.getInfoConstructionSites()
    result[TypeOfMainRoomInfo.infoRoomEnergy] = this.getInfoEnergy()
    result[TypeOfMainRoomInfo.infoPlaceInStorage] = this.getInfoPlaceInStorage()
    result[TypeOfMainRoomInfo.infoPlaceInTerminal] = this.getInfoPlaceInTerminal()
    result[TypeOfMainRoomInfo.infoReaction] = this.getInfoReactionInfo()
    result[TypeOfMainRoomInfo.infoNeedUpgrade] = this.getInfoNeedUpgrade()



    return result
}