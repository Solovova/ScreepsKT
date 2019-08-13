package battleGroup

import constants.BattleGroupContainerConstant
import constants.BattleGroupConstant

import mainContext.MainContext
import mainContext.messenger
import screeps.api.COLOR_RED

class BattleGroupContainer(val parent: MainContext) {
    private val battleGroupContainer: MutableMap<String,BattleGroup> = mutableMapOf()
    val constants: BattleGroupContainerConstant

    init {
        for (record in parent.constants.battleGroups) {
            battleGroupContainer[record] = BattleGroup(this, record)
        }
        this.constants = parent.constants.battleGroupContainerConstant
    }

    fun addBattleGroup(name: String) {
        if (!battleGroupContainer.containsKey(name)){
            parent.constants.battleGroups += name
            parent.constants.battleGroupConstantContainer[name] = BattleGroupConstant()
            battleGroupContainer[name] = BattleGroup(this, name)
        }
        else
            parent.messenger("INFO","Battle group container","Create $name already exist", COLOR_RED)
    }

    fun deleteBattleGroup(name: String) {
        if (battleGroupContainer.containsKey(name)) {
            battleGroupContainer.remove(name)
            parent.constants.battleGroups = parent.constants.battleGroups.filter { it != name }.toTypedArray()
            parent.constants.battleGroupConstantContainer.remove(name)
        }

        else
            parent.messenger("INFO","Battle group container","Delete $name not exist", COLOR_RED)
    }

    fun getBattleGroup(name: String):BattleGroup? {
        return battleGroupContainer[name]
    }
}