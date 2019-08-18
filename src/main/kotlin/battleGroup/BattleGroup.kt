package battleGroup

import constants.BattleGroupConstant
import BattleGroupData
import screeps.api.*
import BattleGroupQueueRecord
import mainContext.messenger
import mainRoom.MainRoom

class BattleGroup {
    val constants: BattleGroupConstant
    val name: String
    val parent: BattleGroupContainer

    constructor(parent: BattleGroupContainer, name: String) {
        this.parent = parent
        this.name = name
        this.constants = parent.parent.constants.battleGroupConstantContainer[this.name]
                ?: BattleGroupConstant()
    }

    constructor(parent: BattleGroupContainer, name: String, battleGroupData: BattleGroupData) : this(parent, name) {
        this.constants.mode = battleGroupData.mode
        this.constants.roomName = battleGroupData.roomName
        this.constants.step = BattleGroupStep.GetPowerHostileCreep
    }

    private fun steps() {
        if (this.constants.step == BattleGroupStep.GetPowerHostileCreep) {
            if (Game.rooms[this.constants.roomName] == null) {
                if (!this.setAssembleRoom()) return
                this.constants.queue.add(this.constants.queue.size, BattleGroupQueueRecord(arrayOf(MOVE)))
                this.constants.step = BattleGroupStep.WaitExploreRoom
                return
            }
            if (!this.setAssembleRoom()) return
            this.getPowerHostileCreep()
            this.setNeedGroupQueue()
            this.constants.step = BattleGroupStep.WaitBuildGroup
        }

        if (this.constants.step == BattleGroupStep.WaitExploreRoom) {
            if (Game.rooms[this.constants.roomName] == null) return
            else {
                if (!this.setAssembleRoom()) return
                this.getPowerHostileCreep()
                this.setNeedGroupQueue()
                this.constants.step = BattleGroupStep.WaitBuildGroup
            }
        }

        if (this.constants.step == BattleGroupStep.WaitBuildGroup) {
            if (!this.isGroupReady()) return
            else this.constants.step = BattleGroupStep.GotoNeedRoom
        }

        if (this.constants.step == BattleGroupStep.GotoNeedRoom) {
            if (!this.isGroupInNeedRoom()) return
            else this.constants.step = BattleGroupStep.Battle
        }
    }

    fun runInStartOfTick() {
        this.steps()
        when (this.constants.step) {
            BattleGroupStep.GetPowerHostileCreep -> {
                return
            }

            BattleGroupStep.WaitExploreRoom -> {
                this.turnExplorer()
                return
            }

            BattleGroupStep.WaitBuildGroup -> {
                this.turnGroupToAssemblePoint()
                return
            }

            BattleGroupStep.GotoNeedRoom -> {
                this.turnGroupToNeedRoom()
                return
            }

            BattleGroupStep.Battle -> {
                this.turnGroupToBattle()
                return
            }

            BattleGroupStep.Sleep -> {
                return
            }
        }
    }

    fun runNotEveryTick() {
    }

    fun runInEndOfTick() {
    }

    private fun getPowerHostileCreep() {
    }

    private fun setNeedGroupQueue() {
    }

    private fun isGroupReady(): Boolean {
        return false
    }

    private fun isGroupInNeedRoom(): Boolean {
        return false
    }

    private fun setAssembleRoom(): Boolean {
        if (this.constants.assembleRoom != "") return true
        //if this.constants.roomName is main, set it room
        //if this.constants.roomName is slave, set it master room
        //set nearest master room who have level >= 7

        for (mainRoom in this.parent.parent.mainRoomCollector.rooms.values) {
            if (mainRoom.name == this.constants.roomName) {
                this.constants.assembleRoom = mainRoom.name
                return true
            }
            for (slaveRoom in mainRoom.slaveRooms.values) {
                if (slaveRoom.name == this.constants.roomName) {
                    this.constants.assembleRoom = mainRoom.name
                    return true
                }
            }
        }

        //
        var nearestMainRoom: MainRoom? = null
        var nearestMainRoomDistance: Int = 1000

        for (mainRoom in this.parent.parent.mainRoomCollector.rooms.values) {
            val controller = mainRoom.structureController[0] ?: continue
            if (controller.level < 7) continue
            val distance: Int = Game.map.getRoomLinearDistance(mainRoom.name, this.constants.roomName)
            if (distance < nearestMainRoomDistance) {
                nearestMainRoomDistance = distance
                nearestMainRoom = mainRoom
            }
        }

        if (nearestMainRoom != null) {
            this.constants.assembleRoom = nearestMainRoom.name
            return true
        } else this.parent.parent.messenger("ERROR", this.constants.roomName, "Battle group ${this.name} can't set assemble room", COLOR_RED)
        return false
    }

    private fun turnExplorer() {
    }

    private fun turnGroupToAssemblePoint() {
    }

    private fun turnGroupToNeedRoom() {
    }

    private fun turnGroupToBattle() {
    }
}