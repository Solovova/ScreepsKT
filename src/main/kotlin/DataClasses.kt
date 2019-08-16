import screeps.api.ColorConstant
import screeps.api.Market

enum class TypeOfTask {
    GoToRoom,
    Take,
    Transport,
    Build,
    Repair,
    Drop,
    Harvest,
    TransferTo,
    Upgrade,
    Claim,
    Reserve,
    GoToPos,
    TakeDropped,
    AttackRange,
    AttackMile,
    SignRoom,
    SignSlaveRoom,
    HarvestMineral,
    EraserAttack,
    EraserGoToKL,
    UpgradeStructure,
    TransferToCreep,
    HealCreep,
    GoToRescueFlag
}

enum class TypeOfMainRoomInfo {
    infoQueue,
    infoController,
    infoConstructionSites,
    infoNeedBuild,
    infoNeedSnapshot,
    infoReaction,
    infoRoomName,
    infoRoomDescribe,
    infoRoomLevel,
    infoRoomEnergy,
    infoPlaceInStorage,
    infoPlaceInTerminal
}

data class OrderRecord(val order: Market.Order, val realPrice: Double)
data class MainRoomInfoRecord(val text: String, val alarm: Boolean)
data class MainRoomInfoSetup(val type: TypeOfMainRoomInfo,
                             val describe: String,
                             val color: ColorConstant,
                             val colorAlarm: ColorConstant,
                             val width: Int,
                             val prefix: String = "",
                             val suffix: String = "")