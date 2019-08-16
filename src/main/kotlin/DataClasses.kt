import screeps.api.*
import screeps.api.structures.Structure

external val LAB_MINERAL_CAPACITY : IntConstant
external val REACTION_TIME : Record<ResourceConstant, Int>
external val RESOURCES_ALL : Array<ResourceConstant>
external val REACTIONS: dynamic

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

data class LabFillerTask(val StructureFrom: Structure,
                         val StructureTo: Structure,
                         val resource: ResourceConstant,
                         val quantity: Int,
                         val priority: Int)