package constants

import REACTIONS
import screeps.api.*

class GlobalConstant {
    val dataCacheCarrierAuto: MutableMap<String, CacheCarrier> = mutableMapOf() //cashed
    val roomRunNotEveryTickTicksPauseMin: Int = 300
    val roomRunNotEveryTickTicksPauseMax: Int = 400
    val buttleGroupList: MutableList<String> = MutableList(0){""}
    val sentMaxMineralQuantity: Int = 10000


    //Market
    val marketMinCreditForOpenBuyOrder: Double = 200000.0
    val marketBuyPriceEnergy = 0.010



    //INFO
    val showProfitWhenLessWhen: Int = 6000

    val labReactionComponent: MutableMap<ResourceConstant,Array<ResourceConstant>> = mutableMapOf()

    init {
        for (key0 in js("Object").keys(REACTIONS).unsafeCast<Array<ResourceConstant>>())
            for (key1 in js("Object").keys(REACTIONS[key0]).unsafeCast<Array<ResourceConstant>>())
                labReactionComponent[REACTIONS[key0][key1].unsafeCast<ResourceConstant>()] = arrayOf(key0,key1)
    }


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