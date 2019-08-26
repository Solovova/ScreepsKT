package battleGroup

import BattleGroupCreep

class BattleGroupCreeps(val parent: BattleGroup) {
    val creeps: MutableList<BattleGroupCreep> = parent.constants.creeps
}