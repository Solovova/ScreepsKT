import constants.CacheCarrier
import mainRoom.MainRoom
import screeps.api.COLOR_YELLOW
import screeps.api.Game
import screeps.api.structures.Structure
import slaveRoom.SlaveRoom

//Test all need information here, if return null, way is impossible
//type mainContainer0, mainContainer1

fun MainContext.getCacheRecordRoom(type: String, mainRoomName: String, slaveRoomName: SlaveRoom? = null) : CacheCarrier? {
    var result: CacheCarrier? = null
    val mainRoom: MainRoom = this.mainRoomCollector.rooms[mainRoomName] ?: return result

    var objectTo : Structure? = null
    if (type == "mainContainer0") objectTo = mainRoom.structureContainerNearSource[0]
    if (type == "mainContainer1") objectTo = mainRoom.structureContainerNearSource[1]
    if (objectTo == null) return null

    val objectFrom : Structure = mainRoom.structureStorage[0] ?: return null

    val keyRecord : String = objectFrom.id + objectTo.id

    var carrierAuto: CacheCarrier? = this.constants.globalConstant.dataCacheCarrierAuto[keyRecord]

    if (carrierAuto == null || carrierAuto.default || (carrierAuto.tickRecalculate + 1000) < Game.time){
        val ret = getWayFromPosToPos(objectFrom.pos, objectTo.pos)
        messenger("TEST", "mainRoomName", "Recalculate ways: $type ${!ret.incomplete}", COLOR_YELLOW)
        if (!ret.incomplete)
            carrierAuto = getCarrierAuto(ret, mainRoom)
        if (carrierAuto == null) carrierAuto = CacheCarrier()
        this.constants.globalConstant.dataCacheCarrierAuto[keyRecord] = carrierAuto
    }


    result = this.constants.globalConstant.dataCacheCarrierAuto[keyRecord]
    return result
}