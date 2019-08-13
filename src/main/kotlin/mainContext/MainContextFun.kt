package mainContext

import screeps.api.*
import constants.CacheCarrier
import mainRoom.MainRoom
import screeps.api.COLOR_YELLOW
import screeps.api.Game
import screeps.api.structures.Structure
import slaveRoom.SlaveRoom


//Test all need information here, if return null, way is impossible
//type mainContainer0, mainContainer1

fun MainContext.getCacheRecordRoom(type: String, mainRoom: MainRoom, slaveRoom: SlaveRoom? = null, recalculate: Boolean = false, inSwampCost: Int = 10, inPlainCost: Int = 2) : CacheCarrier? {
    var objectTo : Structure? = null

    when(type) {
        "mainContainer0" -> objectTo = mainRoom.structureContainerNearSource[0]
        "mainContainer1" -> objectTo = mainRoom.structureContainerNearSource[1]
        "slaveContainer0" -> if (slaveRoom != null)  objectTo = slaveRoom.structureContainerNearSource[0]
        "slaveContainer1" -> if (slaveRoom != null)  objectTo = slaveRoom.structureContainerNearSource[1]
    }

    if (objectTo == null) return null

    val objectFrom : Structure = mainRoom.structureStorage[0] ?: return null

    val keyRecord : String = objectFrom.id + objectTo.id

    var carrierAuto: CacheCarrier? = this.constants.globalConstant.dataCacheCarrierAuto[keyRecord]

    if (recalculate || carrierAuto == null || carrierAuto.default || (carrierAuto.tickRecalculate + 1000) < Game.time){
        val ret = this.getWayFromPosToPos(objectFrom.pos, objectTo.pos, inSwampCost = inSwampCost, inPlainCost = inPlainCost)
        messenger("TEST", mainRoom.name, "Recalculate ways: $type ${!ret.incomplete}", COLOR_YELLOW)
        if (!ret.incomplete)
            Memory["path"] = ret.path
            carrierAuto = this.getCarrierAuto(ret, mainRoom, slaveRoom = slaveRoom)
        this.constants.globalConstant.dataCacheCarrierAuto[keyRecord] = carrierAuto
    }
    return this.constants.globalConstant.dataCacheCarrierAuto[keyRecord]
}