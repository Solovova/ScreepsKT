package accounts.serverMain

import mainContext.MineralDataRecord
import screeps.api.ResourceConstant

fun initMineralMain(mineralData: MutableMap<ResourceConstant, MineralDataRecord>) {
    mineralData["energy".unsafeCast<ResourceConstant>()] = MineralDataRecord(
            priceMax = 0.014,
            priceMin = 0.010,
            marketSellExcess = 10000000,
            marketBuyLack = 6000000,
            buyToRoom = "E54N37"
    )

    mineralData["O".unsafeCast<ResourceConstant>()] = MineralDataRecord(
            priceMax = 0.080,
            priceMin = 0.050,
            marketSellExcess = 300000,
            marketBuyLack = 6000000
    )

    mineralData["L".unsafeCast<ResourceConstant>()] = MineralDataRecord(
            priceMax = 0.100,
            priceMin = 0.080,
            marketSellExcess = 200000,
            marketBuyLack = 6000000
    )
}