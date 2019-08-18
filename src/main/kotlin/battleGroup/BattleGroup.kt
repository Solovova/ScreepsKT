package battleGroup

import constants.BattleGroupConstant
import BattleGroupData
import screeps.api.*

class BattleGroup {
    val constants:BattleGroupConstant
    val name: String
    val parent: BattleGroupContainer

    constructor(parent: BattleGroupContainer,name: String) {
        this.parent = parent
        this.name = name
        this.constants = parent.parent.constants.battleGroupConstantContainer[this.name] ?: BattleGroupConstant()
    }

    constructor(parent: BattleGroupContainer,name: String,battleGroupData: BattleGroupData):this (parent,name) {
        this.constants.mode = battleGroupData.mode
        this.constants.roomName = battleGroupData.roomName
        this.constants.step = BattleGroupStep.getPowerHostileCreep
    }

    fun runInStartOfTick() {
        if (this.constants.step == BattleGroupStep.getPowerHostileCreep) {
            if (Game.rooms[this.constants.roomName] == null) {
                //build explorer in nearest room
            }
        }
    }

    fun runNotEveryTick() {
    }

    fun runInEndOfTick() {
    }
}