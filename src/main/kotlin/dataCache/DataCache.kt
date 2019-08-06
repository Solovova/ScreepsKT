package dataCache

import MainContext
import mainRoom.MainRoom
import slaveRoom.SlaveRoom
import getCarrierAuto
import getWayFromPosToPos
import messenger
import screeps.api.*
import screeps.api.structures.Structure

//Class for cashing data, in end of tick it serialize and write to memory, read data from memory only then constructing

class DataCache(private val parent: MainContext) {
    private val dataCacheCarrierAuto: MutableMap<String, CacheCarrier> = mutableMapOf()

    fun toMemory() {
        val dHead : dynamic = object {}

        //dataCacheCarrierAuto
        dHead["dataCacheCarrierAuto"] = object {}
        val dLevelDataCacheCarrierAuto = dHead["dataCacheCarrierAuto"]

        for (record in dataCacheCarrierAuto)
            dLevelDataCacheCarrierAuto[record.key] = record.value.toDynamic()
        //--------------------

        Memory["dataCache"] = dHead
        messenger("TEST", "____", "Write data is: $dHead", COLOR_GREEN)
        messenger("TEST", "____", "Write data cache", COLOR_GREEN)
    }

    private fun fromMemory() {
        val dHead : dynamic = Memory["dataCache"] ?: return


        //dataCacheCarrierAuto
        val dLevelDataCacheCarrierAuto : dynamic = dHead["dataCacheCarrierAuto"]
        if (dLevelDataCacheCarrierAuto != null)
            for (recordKey in js("Object").keys(dLevelDataCacheCarrierAuto).unsafeCast<Array<String>>())
                dataCacheCarrierAuto[recordKey] = CacheCarrier.initFromDynamic(dLevelDataCacheCarrierAuto[recordKey])
        //--------------------

        messenger("TEST", "____", "Read data is: $dHead", COLOR_GREEN)
        messenger("TEST", "____", "Read data cache", COLOR_GREEN)
    }

    init {
        this.fromMemory()
    }

    //Test all need information here, if return null, way is impossible
    //type mainContainer0, mainContainer1

    fun getCacheRecordRoom(type: String, mainRoomName: String, slaveRoomName: SlaveRoom? = null) : CacheCarrier? {
        var result: CacheCarrier? = null
        val mainRoom: MainRoom = this.parent.mainRoomCollector.rooms[mainRoomName] ?: return result

        var objectTo : Structure? = null
        if (type == "mainContainer0") objectTo = mainRoom.structureContainerNearSource[0]
        if (type == "mainContainer1") objectTo = mainRoom.structureContainerNearSource[1]
        if (objectTo == null) return null

        val objectFrom : Structure = mainRoom.structureStorage[0] ?: return null

        val keyRecord : String = objectFrom.id + objectTo.id

        var carrierAuto: CacheCarrier? = this.dataCacheCarrierAuto[keyRecord]

        if (carrierAuto == null || carrierAuto.default || (carrierAuto.tickRecalculate + 1000) < Game.time){
            val ret = getWayFromPosToPos(objectFrom.pos, objectTo.pos)
            messenger("TEST", "mainRoomName", "Recalculate ways: $type ${!ret.incomplete}", COLOR_YELLOW)
            if (!ret.incomplete)
                carrierAuto = getCarrierAuto(ret, mainRoom)
            if (carrierAuto == null) carrierAuto = CacheCarrier()
            this.dataCacheCarrierAuto[keyRecord] = carrierAuto
        }


        result = this.dataCacheCarrierAuto[keyRecord]
        return result
    }
}