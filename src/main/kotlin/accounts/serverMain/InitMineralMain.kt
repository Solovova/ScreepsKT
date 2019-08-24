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
            marketSellExcess = 200000,
            marketBuyLack = 6000000,
            sellFromRoom = "E54N37"
    )

    mineralData["L".unsafeCast<ResourceConstant>()] = MineralDataRecord(
            priceMax = 0.100,
            priceMin = 0.080,
            marketSellExcess = 200000,
            marketBuyLack = 6000000,
            sellFromRoom = "E52N37"
    )

    mineralData["XGH2O".unsafeCast<ResourceConstant>()] = MineralDataRecord(
            priceMax = 2.000,
            priceMin = 1.050,
            marketSellExcess = 0,
            sellFromRoom = "E52N38"
    )

    mineralData["GH2O".unsafeCast<ResourceConstant>()] = MineralDataRecord(
            priceMax = 1.200,
            priceMin = 0.900,
            marketSellExcess = 40000,
            sellFromRoom = "E54N39"
    )
}