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

    mineralData["H".unsafeCast<ResourceConstant>()] = MineralDataRecord(
            priceMax = 0.090,
            priceMin = 0.070,
            marketSellExcess = 200000,
            marketBuyLack = 6000000,
            sellFromRoom = "E58N39"
    )

    mineralData["L".unsafeCast<ResourceConstant>()] = MineralDataRecord(
            priceMax = 0.100,
            priceMin = 0.080,
            marketSellExcess = 200000,
            marketBuyLack = 6000000,
            sellFromRoom = "E52N37"
    )

    mineralData["Z".unsafeCast<ResourceConstant>()] = MineralDataRecord(
            priceMax = 0.055,
            priceMin = 0.027,
            marketSellExcess = 200000,
            marketBuyLack = 6000000,
            sellFromRoom = "E59N36"
    )

    mineralData["U".unsafeCast<ResourceConstant>()] = MineralDataRecord(
            priceMax = 0.035,
            priceMin = 0.030,
            marketSellExcess = 200000,
            marketBuyLack = 6000000,
            sellFromRoom = "E57N39"
    )

    mineralData["XGH2O".unsafeCast<ResourceConstant>()] = MineralDataRecord(
            priceMax = 2.000,
            priceMin = 0.920,
            marketSellExcess = 0,
            sellFromRoom = "E52N38"
    )

    mineralData["GH2O".unsafeCast<ResourceConstant>()] = MineralDataRecord(
            priceMax = 1.200,
            priceMin = 0.900,
            marketSellExcess = 40000,
            sellFromRoom = "E54N39"
    )

    mineralData["OH".unsafeCast<ResourceConstant>()] = MineralDataRecord(
            priceMax = 0.060,
            priceMin = 0.060,
            storeMax = 100000,
            buyToRoom = "E52N35",
            onlyDirectBuy = true
    )

    mineralData["ZK".unsafeCast<ResourceConstant>()] = MineralDataRecord(
            priceMax = 0.050,
            priceMin = 0.050,
            storeMax = 100000,
            buyToRoom = "E54N37",
            onlyDirectBuy = true
    )

    mineralData["UL".unsafeCast<ResourceConstant>()] = MineralDataRecord(
            priceMax = 0.050,
            priceMin = 0.050,
            storeMax = 100000,
            buyToRoom = "E53N39",
            onlyDirectBuy = true
    )
}