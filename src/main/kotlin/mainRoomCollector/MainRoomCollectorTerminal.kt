package mainRoomCollector

import mainContext.messenger
import mainRoom.MainRoom
import screeps.api.COLOR_GREEN
import screeps.api.OK
import screeps.api.RESOURCE_ENERGY
import screeps.api.structures.StructureTerminal
import screeps.utils.toMap
import kotlin.math.min

fun MainRoomCollector.runTerminalsTransfer() {
    this.terminalSentEnergy()
    this.terminalSentMineral()
    this.terminalSentEnergyOverflow()
}

fun MainRoomCollector.terminalSentEnergy() {
    for (roomFrom in this.rooms.values) {
        if (roomFrom.constant.sentEnergyToRoom == "") continue
        val roomTo: MainRoom = this.rooms[roomFrom.constant.sentEnergyToRoom] ?: continue
        if (roomTo.getResourceInTerminal() < 20000 && roomFrom.getResourceInTerminal() > 30000) {
            val terminalFrom: StructureTerminal = roomFrom.structureTerminal[0] ?: continue
            val terminalTo: StructureTerminal = roomTo.structureTerminal[0] ?: continue
            if (terminalFrom.cooldown == 0 && terminalTo.cooldown == 0)
                terminalFrom.send(RESOURCE_ENERGY, 20000, roomTo.name)
        }
    }
}

fun MainRoomCollector.terminalSentMineral() {
    val minTransfer = 1000

    for (roomTo in this.rooms.values) {
        val terminalTo = roomTo.structureTerminal[0] ?: continue
        if (terminalTo.cooldown != 0) continue
        for (needResourceRecord in roomTo.constant.needMineral) {
            val needResourceQuantity = needResourceRecord.value - roomTo.getResource(needResourceRecord.key)
            val needResource = needResourceRecord.key
            val canMineralTakeTerminal = roomTo.constant.mineralAllMaxTerminal - (terminalTo.store.toMap().map { it.value }.sum()
                    - roomTo.getResourceInTerminal(RESOURCE_ENERGY))

            for (roomFrom in this.rooms.values) {
                if (roomFrom.name == roomTo.name) continue
                val terminalFrom = roomFrom.structureTerminal[0] ?: continue
                if (terminalFrom.cooldown != 0) continue
                val haveResourceQuantity = roomFrom.getResource(needResource) -
                        (roomFrom.constant.needMineral[needResource] ?: 0)
                val quantityTransfer = min(min(min(
                        haveResourceQuantity, needResourceQuantity),
                        parent.constants.globalConstant.sentMaxMineralQuantity),
                        canMineralTakeTerminal)
                if (quantityTransfer < 100) continue
                //One transfer per tick
                if (terminalFrom.send(needResource, quantityTransfer, roomTo.name) == OK) return
            }
        }
    }
}

fun MainRoomCollector.terminalSentEnergyOverflow() {
    val sentFromIfHaveMoreThen = 220000
    val sentToIfHaveLessThen = 90000
    val sentQuantity = 5000
    var mainRoomFrom: MainRoom? = null
    var mainRoomFromQuantityHave = 0
    var mainRoomTo: MainRoom? = null
    var mainRoomToQuantityHave = 2000000

    for (room in this.rooms.values) {
        if (room.getLevelOfRoom() < 2) continue
        val quality = room.getResource()
        if (quality > mainRoomFromQuantityHave) {
            mainRoomFromQuantityHave = quality
            mainRoomFrom = room
        }
        if (quality < mainRoomToQuantityHave) {
            mainRoomToQuantityHave = quality
            mainRoomTo = room
        }
    }



    if (mainRoomFromQuantityHave > sentFromIfHaveMoreThen
            && mainRoomToQuantityHave < sentToIfHaveLessThen
            && mainRoomFrom != null
            && mainRoomTo != null) {
        val terminalFrom: StructureTerminal = mainRoomFrom.structureTerminal[0] ?: return
        val terminalTo: StructureTerminal = mainRoomTo.structureTerminal[0] ?: return
        if (terminalFrom.cooldown == 0 && terminalTo.cooldown == 0) {
            val result = terminalFrom.send(RESOURCE_ENERGY, sentQuantity, mainRoomTo.name)
            if (result == OK)
                parent.messenger("INFO", mainRoomFrom.name,
                        "Send energy $sentQuantity from ${mainRoomFrom.name} $mainRoomFromQuantityHave -> ${mainRoomTo.name} $mainRoomToQuantityHave", COLOR_GREEN)
        }
    }
}
