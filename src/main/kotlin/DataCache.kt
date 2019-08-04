import screeps.api.*

//Class for cashing data, in end of tick it serialize and write to memory, read data from memory only then constructing

class CarrierAuto (val wayLength: Int , val wayWeight: Int ,val needCarriers: Int, val needCapacity: Int, val timeForDeath: Int, val needBody: Array<BodyPartConstant>)

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

class DataCache {
    var dataCacheRecordRoom: MutableMap<String,CacheRecordRoom> = mutableMapOf()

    fun toMemory() {
        val dHead : dynamic = object {}

        //dataCacheRecordRoom
        dHead["dataCacheRecordRoom"] = object {}
        val dLevelDataCacheRecordRoom = dHead["dataCacheRecordRoom"]

        for (record in dataCacheRecordRoom)
            dLevelDataCacheRecordRoom[record.key] = record.value.toDynamic()
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
        //--------------------

        messenger("TEST","____","Read data is: $dHead", COLOR_GREEN)
        messenger("TEST","____","Read data cache", COLOR_GREEN)
    }

    init {
        this.fromMemory()
    }
}