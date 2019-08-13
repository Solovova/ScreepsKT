package battleGroup

import constants.BattleGroupConstant

class BattleGroup(val parent: BattleGroupContainer, val name: String) {
    val constants:BattleGroupConstant


    init {
        this.constants = parent.parent.constants.battleGroupConstantContainer[this.name] ?: BattleGroupConstant()
    }
}