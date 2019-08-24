package mainRoomCollector

import mainContext.messenger
import mainRoom.MainRoom
import screeps.api.COLOR_GREEN
import screeps.api.OK
import screeps.api.RESOURCE_ENERGY
import screeps.api.ResourceConstant
import screeps.api.structures.StructureTerminal
import screeps.utils.toMap
import kotlin.math.max
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

    //Logic
    //needResource term, haveResource term, haveResourceInRoom term+storage
    //needResource > haveResource && needResource <= haveResourceInRoom -> wait
    //needResource < haveResource && needResource <100 -> needResource = 100


    for (roomTo in this.rooms.values) {
        val terminalTo = roomTo.structureTerminal[0] ?: continue
        if (terminalTo.cooldown != 0) continue
        for (needResourceRecord in roomTo.needMineral) {
            val needResource = needResourceRecord.key
            var needResourceQuantity = needResourceRecord.value - roomTo.getResource(needResourceRecord.key)
            if (needResource == "GH2O".unsafeCast<ResourceConstant>())
                console.log("TEST Room ${roomTo.name} need: ${needResourceRecord.value} have: ${roomTo.getResource(needResourceRecord.key)} trans: $needResourceQuantity")


            if (needResourceQuantity <= 0) continue
            needResourceQuantity = max(needResourceQuantity, 100)

            val canMineralTakeTerminal = roomTo.constant.mineralAllMaxTerminal - (terminalTo.store.toMap().map { it.value }.sum()
                    - roomTo.getResourceInTerminal(RESOURCE_ENERGY))



            for (roomFrom in this.rooms.values) {
                if (roomFrom.name == roomTo.name) continue
                val terminalFrom = roomFrom.structureTerminal[0] ?: continue
                if (terminalFrom.cooldown != 0) continue
                val haveResourceQuantity = roomFrom.getResourceInTerminal(needResource)
                val haveResourceQuantityInRoom = roomFrom.getResource(needResource) -
                        (roomFrom.needMineral[needResource] ?: 0)

                val quantityTransfer = min(min(min(
                        haveResourceQuantity, needResourceQuantity),
                        parent.constants.globalConstant.sentMaxMineralQuantity),
                        canMineralTakeTerminal)

                if (needResource == "GH2O".unsafeCast<ResourceConstant>())
                    console.log("TEST Room ${roomFrom.name} transfer: $quantityTransfer")

                if (quantityTransfer < needResourceQuantity
                        && quantityTransfer < parent.constants.globalConstant.sentMaxMineralQuantity
                        && quantityTransfer < haveResourceQuantityInRoom) continue  //wait because not all resource transfer from storage to terminal

                if (needResource == "GH2O".unsafeCast<ResourceConstant>())
                    console.log("TEST Room ${roomFrom.name} transfer: $quantityTransfer")

                if (quantityTransfer < 100) continue
                //One transfer per tick

                if (needResource == "GH2O".unsafeCast<ResourceConstant>())
                    console.log("TEST Transfer Room ${roomFrom.name} transfer: $quantityTransfer roomTo: ${roomTo.name}")
                val result = terminalFrom.send(needResource, quantityTransfer, roomTo.name)
                console.log(result)
                if (result == OK) return
            }
        }
    }
}

fun MainRoomCollector.terminalSentEnergyOverflow() {
    val sentQuantity = 5000
    var mainRoomFrom: MainRoom? = null
    var mainRoomFromQuantityHave = 0
    var mainRoomTo: MainRoom? = null
    var mainRoomToQuantityHave = 2000000

    for (room in this.rooms.values) {
        if (room.getLevelOfRoom() < 2) continue
        val quantity = room.getResource() - room.constant.energyExcessSent
        if (quantity > mainRoomFromQuantityHave) {
            mainRoomFromQuantityHave = quantity
            mainRoomFrom = room
        }
        if (quantity < mainRoomToQuantityHave) {
            mainRoomToQuantityHave = quantity
            mainRoomTo = room
        }
    }



    if (sentQuantity in (mainRoomToQuantityHave + 1) until mainRoomFromQuantityHave
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
