package battleGroup

import constants.BattleGroupContainerConstant
import constants.BattleGroupConstant

import mainContext.MainContext
import mainContext.messenger
import screeps.api.COLOR_RED
import BattleGroupData
import BgSpawnResult
import mainRoom.MainRoom
import screeps.api.structures.StructureSpawn

class BattleGroupContainer(val parent: MainContext) {
    val battleGroupContainer: MutableMap<String, BattleGroup> = mutableMapOf()
    val constants: BattleGroupContainerConstant

    init {
        for (record in parent.constants.battleGroups) {
            battleGroupContainer[record] = BattleGroup(this, record)
        }
        this.constants = parent.constants.battleGroupContainerConstant
    }

    private fun addBattleGroup(name: String, battleGroupData: BattleGroupData) {
        if (!battleGroupContainer.containsKey(name)) {
            parent.constants.battleGroups += name
            parent.constants.battleGroupConstantContainer[name] = BattleGroupConstant()
            battleGroupContainer[name] = BattleGroup(this, name, battleGroupData)
        } else
            parent.messenger("INFO", "Battle group container", "Create $name already exist", COLOR_RED)
    }

    fun deleteBattleGroup(name: String) {
        if (battleGroupContainer.containsKey(name)) {
            battleGroupContainer.remove(name)
            parent.constants.battleGroups = parent.constants.battleGroups.filter { it != name }.toTypedArray()
            parent.constants.battleGroupConstantContainer.remove(name)
        } else
            parent.messenger("INFO", "Battle group container", "Delete $name not exist", COLOR_RED)
    }

    fun getBattleGroup(name: String): BattleGroup? {
        return battleGroupContainer[name]
    }

    fun defenceRoom(roomName: String) {
        this.addBattleGroup("defenceRoom$roomName",
                BattleGroupData(TypeBattleGroupMode.Defence, roomName = roomName))
    }

    fun addTestGroup(roomName: String) {
        this.addBattleGroup("testRoom$roomName",
                BattleGroupData(TypeBattleGroupMode.Test, roomName = roomName))
    }

    fun runInStartOfTick() {
        for (bg in battleGroupContainer.values) {
            try {
                bg.runInStartOfTick()
            } catch (e: Exception) {
                parent.messenger("ERROR", "Battle group runInStartOfTick", bg.name, COLOR_RED)
            }
        }
    }

    fun runNotEveryTick() {
        for (bg in battleGroupContainer.values) {
            try {
                bg.runNotEveryTick()
            } catch (e: Exception) {
                parent.messenger("ERROR", "Battle group runNotEveryTick", bg.name, COLOR_RED)
            }
        }
    }

    fun runInEndOfTick() {
        for (bg in battleGroupContainer.values) {
            try {
                bg.runInEndOfTick()
            } catch (e: Exception) {
                parent.messenger("ERROR", "Battle group runInEndOfTick", bg.name, COLOR_RED)
            }
        }
    }

    fun getBattleGroupsForAssembleInRoom(roomName: String):List<BattleGroup> {
        val listBg: MutableList<BattleGroup> = mutableListOf()
        for (battleGroup in battleGroupContainer.values)
            if (battleGroup.constants.assembleRoom == roomName)
                listBg.add(listBg.size,battleGroup)
        return listBg
    }


    fun spawnCreep(mainRoom: MainRoom, spawn: StructureSpawn): BgSpawnResult {
        var result = BgSpawnResult.QueueEmpty
        for (battleGroup in battleGroupContainer.values) {
            if (battleGroup.constants.step == BattleGroupStep.WaitBuildGroup &&
                    mainRoom.name == battleGroup.constants.assembleRoom) {
                result = battleGroup.spawnCreep(mainRoom,spawn)
                if (result == BgSpawnResult.StartSpawn) return result
            }
        }
        return result
    }
}