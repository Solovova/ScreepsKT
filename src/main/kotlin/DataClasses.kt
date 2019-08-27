import screeps.api.*
import screeps.api.structures.Structure

external val LAB_MINERAL_CAPACITY: IntConstant
external val REACTION_TIME: Record<ResourceConstant, Int>
external val RESOURCES_ALL: Array<ResourceConstant>
external val REACTIONS: dynamic
external val BOOSTS: dynamic

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
    GoToRescueFlag,
    TransferFromCreep,
    GoToMainRoomRescueFlag,
    UpgradeCreep
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
    infoPlaceInTerminal,
    infoNeedUpgrade
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


enum class TypeBattleGroupMode {
    Defence,
    Test
}

data class BattleGroupData(var mode: TypeBattleGroupMode,
                           var roomName: String = ""
)

enum class BattleGroupStep {
    GetPowerHostileCreep,
    WaitExploreRoom,
    WaitBuildGroup,
    GotoNeedRoom,
    Battle,
    Sleep
}

enum class BgSpawnResult {
    StartSpawn,
    QueueEmpty,
    CantSpawn
}

data class BattleGroupCreep(var creep: Creep? = null,
                            var role: Int = 0,
                            var pos: RoomPosition? = null,
                            var body: Array<BodyPartConstant> = arrayOf(),
                            var upgrade: String = "",
                            var spawnID: String = "",
                            var posChainMove: RoomPosition? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class.js != other::class.js) return false

        other as BattleGroupCreep

        if (creep != other.creep) return false
        if (role != other.role) return false
        if (pos != other.pos) return false
        if (posChainMove != other.posChainMove) return false
        if (spawnID != other.spawnID) return false
        if (!body.contentEquals(other.body)) return false
        if (upgrade != other.upgrade) return false

        return true
    }

    override fun hashCode(): Int {
        var result = creep?.hashCode() ?: 0
        result = 31 * result + role
        result = 31 * result + (pos?.hashCode() ?: 0)
        result = 31 * result + body.contentHashCode()
        result = 31 * result + upgrade.hashCode()
        result = 31 * result + spawnID.hashCode()
        result = 31 * result + (posChainMove?.hashCode() ?: 0)
        return result
    }
}


