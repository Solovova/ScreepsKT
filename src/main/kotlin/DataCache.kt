import screeps.api.*
import screeps.api.structures.Structure

//Class for cashing data, in end of tick it serialize and write to memory, read data from memory only then constructing

class CarrierAuto {
    var default : Boolean
    var needCarriers: Int
    var timeForDeath: Int
    var tickRecalculate: Int
    val needBody: Array<BodyPartConstant>

    constructor(default: Boolean? = null, tickRecalculate:Int? = null, needCarriers: Int? = null, timeForDeath: Int? = null, needBody: Array<BodyPartConstant>? = null) {
        this.default = default ?: true
        this.tickRecalculate = tickRecalculate ?: 0
        this.needCarriers = needCarriers ?: 1
        this.timeForDeath = timeForDeath ?: 100
        this.needBody = needBody ?: arrayOf<BodyPartConstant>(MOVE,MOVE,MOVE,MOVE,MOVE,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY)
    }

    fun toDynamic():dynamic {
        val d : dynamic = object {}
        d["default"] = this.default
        d["needCarriers"] = this.needCarriers
        d["timeForDeath"] = this.timeForDeath
        d["tickRecalculate"] = this.tickRecalculate
        d["needBody"] = this.needBody
        return d
    }

    companion object {
        fun initFromDynamic(d: dynamic): CarrierAuto {
            val default: Boolean? = if (d["default"] != null) d["default"] as Boolean else null
            val needCarriers: Int? = if (d["needCarriers"] != null) d["needCarriers"] as Int else null
            val timeForDeath: Int? = if (d["timeForDeath"] != null) d["timeForDeath"] as Int else null
            val tickRecalculate: Int? = if (d["tickRecalculate"] != null) d["tickRecalculate"] as Int else null
            val needBody: Array<BodyPartConstant>? = if (d["needBody"] != null) d["needBody"] as Array<BodyPartConstant> else null
            return CarrierAuto(default = default, needCarriers = needCarriers, timeForDeath = timeForDeath, tickRecalculate = tickRecalculate, needBody = needBody)
        }
    }
}


class CacheRecordRoom {
    var testData : Int
    fun toDynamic():dynamic {
        val d : dynamic = object {}
        d["testData"] = this.testData
        return d
    }

    constructor(testData : Int) {
        this.testData = testData
    }

    companion object {
        fun initFromDynamic(d: dynamic): CacheRecordRoom {
            val testData: Int = if (d["testData"] != null) d["testData"] as Int else -100
            return CacheRecordRoom(testData)
        }
    }
}

class DataCache(private val parent: MainContext) {
    private val dataCacheRecordRoom: MutableMap<String,CacheRecordRoom> = mutableMapOf()
    private val dataCacheCarrierAuto: MutableMap<String,CarrierAuto> = mutableMapOf()

    fun toMemory() {
        val dHead : dynamic = object {}

        //dataCacheRecordRoom
        dHead["dataCacheRecordRoom"] = object {}
        val dLevelDataCacheRecordRoom = dHead["dataCacheRecordRoom"]

        for (record in dataCacheRecordRoom)
            dLevelDataCacheRecordRoom[record.key] = record.value.toDynamic()

        //dataCacheCarrierAuto
        dHead["dataCacheCarrierAuto"] = object {}
        val dLevelDataCacheCarrierAuto = dHead["dataCacheCarrierAuto"]

        for (record in dataCacheCarrierAuto)
            dLevelDataCacheCarrierAuto[record.key] = record.value.toDynamic()
        //--------------------

        Memory["dataCache"] = dHead
        messenger("TEST","____","Write data is: $dHead", COLOR_GREEN)
        messenger("TEST","____","Write data cache", COLOR_GREEN)
    }

    private fun fromMemory() {
        val dHead : dynamic = Memory["dataCache"] ?: return

        //dataCacheRecordRoom
        val dLevelDataCacheRecordRoom : dynamic = dHead["dataCacheRecordRoom"]
        if (dLevelDataCacheRecordRoom != null)
            for (recordKey in js("Object").keys(dLevelDataCacheRecordRoom).unsafeCast<Array<String>>())
                dataCacheRecordRoom[recordKey] = CacheRecordRoom.initFromDynamic(dLevelDataCacheRecordRoom[recordKey])

        //dataCacheCarrierAuto
        val dLevelDataCacheCarrierAuto : dynamic = dHead["dataCacheCarrierAuto"]
        if (dLevelDataCacheCarrierAuto != null)
            for (recordKey in js("Object").keys(dLevelDataCacheCarrierAuto).unsafeCast<Array<String>>())
                dataCacheCarrierAuto[recordKey] = CarrierAuto.initFromDynamic(dLevelDataCacheCarrierAuto[recordKey])
        //--------------------

        messenger("TEST","____","Read data is: $dHead", COLOR_GREEN)
        messenger("TEST","____","Read data cache", COLOR_GREEN)
    }

    init {
        this.fromMemory()
    }

    //Test all need information here, if return null, way is impossible
    //type mainContainer0, mainContainer1

    fun getCacheRecordRoom(type: String, mainRoomName: String, slaveRoomName: SlaveRoom? = null) : CarrierAuto? {
        var result: CarrierAuto? = null
        val mainRoom:MainRoom = this.parent.mainRooms.rooms[mainRoomName] ?: return result

        var objectTo : Structure? = null
        if (type == "mainContainer0") objectTo = mainRoom.structureContainerNearSource[0]
        if (type == "mainContainer1") objectTo = mainRoom.structureContainerNearSource[1]
        if (objectTo == null) return null

        val objectFrom : Structure = mainRoom.structureStorage[0] ?: return null

        val keyRecord : String = objectFrom.id + objectTo.id

        var carrierAuto: CarrierAuto? = this.dataCacheCarrierAuto[keyRecord]

        if (carrierAuto == null || carrierAuto.default || (carrierAuto.tickRecalculate + 1000) < Game.time){
            val ret = getWayFromPosToPos(objectFrom.pos,objectTo.pos)
            messenger("TEST","mainRoomName","Recalculate ways: $type ${!ret.incomplete}", COLOR_YELLOW)
            if (!ret.incomplete)
                carrierAuto = getCarrierAuto(ret,mainRoom)
            if (carrierAuto == null) carrierAuto = CarrierAuto()
            this.dataCacheCarrierAuto[keyRecord] = carrierAuto
        }


        result = this.dataCacheCarrierAuto[keyRecord]
        return result
    }
}