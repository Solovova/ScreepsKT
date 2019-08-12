package constants

class GlobalConstant {
    val dataCacheCarrierAuto: MutableMap<String, CacheCarrier> = mutableMapOf() //cashed
    val roomRunNotEveryTickTicksPauseMin: Int = 10
    val roomRunNotEveryTickTicksPauseMax: Int = 20


    val clearProfit: Boolean = false

    //INFO
    val showProfitWhenLessWhen: Int = 6000

    fun toDynamic(): dynamic {
        val result: dynamic = object {}
        //dataCacheCarrierAuto
        result["dataCacheCarrierAuto"] = object {}
        for (record in dataCacheCarrierAuto)
            result["dataCacheCarrierAuto"][record.key] = record.value.toDynamic()
        //--------------------
        return result
    }

    fun fromDynamic(d: dynamic) {
        //dataCacheCarrierAuto
        if (d != null)
            for (recordKey in js("Object").keys(d).unsafeCast<Array<String>>())
                dataCacheCarrierAuto[recordKey] = CacheCarrier.initFromDynamic(d[recordKey])

    }
}