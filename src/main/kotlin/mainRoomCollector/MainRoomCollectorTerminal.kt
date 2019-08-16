package mainRoomCollector

import mainRoom.MainRoom
import mainRoom.setLogistTask
import screeps.api.OK
import screeps.api.RESOURCE_ENERGY
import screeps.api.structures.StructureTerminal
import screeps.utils.toMap
import kotlin.math.max
import kotlin.math.min

fun MainRoomCollector.runTerminalsTransfer() {
    this.terminalSentEnergy()
    this.terminalSentMineral()
}

fun MainRoomCollector.terminalSentEnergy() {
    for (roomFrom in this.rooms.values) {
        if (roomFrom.constant.sentEnergyToRoom == "") continue
        val roomTo: MainRoom = this.rooms[roomFrom.constant.sentEnergyToRoom] ?: continue
        if (roomTo.getResourceInTerminal()<20000 && roomFrom.getResourceInTerminal() > 30000) {
            val terminalFrom:StructureTerminal = roomFrom.structureTerminal[0] ?: continue
            val terminalTo:StructureTerminal = roomTo.structureTerminal[0] ?: continue
            if (terminalFrom.cooldown == 0 && terminalTo.cooldown == 0)
                terminalFrom.send(RESOURCE_ENERGY,20000,roomTo.name)
        }
    }
}

fun MainRoomCollector.terminalSentMineral() {
    for (roomTo in this.rooms.values) {
        val terminalTo = roomTo.structureTerminal[0] ?: continue
        if (terminalTo.cooldown != 0) continue
        for (needResourceRecord in roomTo.constant.needMineral) {
            val needResourceQuantity = needResourceRecord.value - roomTo.getResource(needResourceRecord.key)
            if (needResourceQuantity < 500) continue
            val needResource = needResourceRecord.key
            val canMineralTakeTerminal = roomTo.constant.mineralAllMaxTerminal - (terminalTo.store.toMap().map { it.value }.sum()
                    -roomTo.getResourceInTerminal(RESOURCE_ENERGY))
            if (canMineralTakeTerminal < 500) continue

            for (roomFrom in this.rooms.values) {
                if (roomFrom.name == roomTo.name) continue
                val terminalFrom = roomFrom.structureTerminal[0] ?: continue
                if (terminalFrom.cooldown != 0) continue
                val haveResourceQuantity =  roomFrom.getResource(needResource) -
                        (roomFrom.constant.needMineral[needResource] ?: 0)
                val quantityTransfer = min(min(min(
                        haveResourceQuantity,needResourceQuantity),
                        parent.constants.globalConstant.sentMaxMineralQuantity),
                        canMineralTakeTerminal)
                if (quantityTransfer < 100) continue
                //One transfer per tick
                if (terminalFrom.send(needResource,quantityTransfer,roomTo.name) == OK) return
            }
        }
    }
}
