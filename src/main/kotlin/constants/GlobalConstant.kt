package constants

import screeps.api.*

class GlobalConstant {
    val dataCacheCarrierAuto: MutableMap<String, CacheCarrier> = mutableMapOf() //cashed
    val roomRunNotEveryTickTicksPauseMin: Int = 300
    val roomRunNotEveryTickTicksPauseMax: Int = 400
    val buttleGroupList: MutableList<String> = MutableList(0){""}
    val sentMaxMineralQuantity: Int = 10000


    //Market
    val marketMinCreditForOpenBuyOrder: Double = 200000.0
    val marketBuyPriceEnergy = 0.012



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
        if (d["dataCacheCarrierAuto"] != null)
            for (recordKey in js("Object").keys(d["dataCacheCarrierAuto"]).unsafeCast<Array<String>>())
                dataCacheCarrierAuto[recordKey] = CacheCarrier.initFromDynamic(d["dataCacheCarrierAuto"][recordKey])



    }
}