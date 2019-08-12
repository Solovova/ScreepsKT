package mainRoomCollector

import mainRoom.MainRoom
import screeps.api.RESOURCE_ENERGY
import screeps.api.structures.StructureTerminal

fun MainRoomCollector.terminalCalculate() {
    this.terminalSent()
}

fun MainRoomCollector.terminalSent() {
    for (roomFrom in this.rooms.values) {
        if (roomFrom.constant.sentEnergyToRoom == "") continue
        val roomTo: MainRoom = this.rooms[roomFrom.constant.sentEnergyToRoom] ?: continue
        if (roomTo.getResourceInTerminal()<20000 && roomFrom.getResourceInTerminal() > 40000) {
            val terminalFrom:StructureTerminal = roomFrom.structureTerminal[0] ?: continue
            val terminalTo:StructureTerminal = roomTo.structureTerminal[0] ?: continue
            if (terminalFrom.cooldown == 0 && terminalTo.cooldown == 0)
                terminalFrom.send(RESOURCE_ENERGY,20000,roomTo.name)
        }
    }
}