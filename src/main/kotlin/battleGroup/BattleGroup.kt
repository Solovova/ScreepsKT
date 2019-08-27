package battleGroup

import constants.BattleGroupConstant
import BattleGroupData
import screeps.api.*
import mainContext.messenger
import mainRoom.MainRoom
import BattleGroupCreep
import BgSpawnResult
import screeps.api.structures.SpawnOptions
import screeps.api.structures.StructureSpawn
import screeps.utils.copy

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
                //this.constants.queue.add(this.constants.queue.size, BattleGroupQueueRecord(arrayOf(MOVE)))
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
        //reInitial creeps
        for (ind in this.constants.creeps.indices)
            if (this.constants.creeps[ind].creep != null)
                this.constants.creeps[ind].creep = Game.getObjectById(this.constants.creeps[ind].creep?.id)

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
                this.setPointerAssembleRoomToBG()
                return
            }
            else -> {}
        }
    }

    fun runNotEveryTick() {
    }

    fun runInEndOfTick() {
        when (this.constants.step) {
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

            else -> {
            }
        }
    }

    private fun getPowerHostileCreep() {
    }

    private fun setNeedGroupQueue() {
        //0 - explorer
        //1 - mile
        //2 - range
        //3 - healer
        this.constants.creeps.add(0, BattleGroupCreep(creep = null, role = 2, body = arrayOf(MOVE)))
        this.constants.creeps.add(1, BattleGroupCreep(creep = null, role = 2, body = arrayOf(MOVE)))
        this.constants.creeps.add(2, BattleGroupCreep(creep = null, role = 3, body = arrayOf(MOVE, MOVE)))
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
        var nearestMainRoomDistance = 1000

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

    private fun setPointerAssembleRoomToBG() {

        if (this.constants.creeps.any { it.creep == null && it.spawnID == "" }) {
            val mainRoom: MainRoom? = this.parent.parent.mainRoomCollector.rooms[this.constants.assembleRoom]
            if (mainRoom != null && mainRoom.spawnForBattleGroup == null) mainRoom.spawnForBattleGroup = this
        }

        for (spawnCreep in this.constants.creeps.filter { it.creep == null && it.spawnID != "" }) {
            val spawn: StructureSpawn = Game.getObjectById(spawnCreep.spawnID) ?: continue
            val creep: Creep = Game.creeps[spawn.spawning?.name ?: "not spawn"] ?: continue
            spawnCreep.creep = creep
            spawnCreep.spawnID = ""
        }
    }

    private fun turnGroupToAssemblePoint() {
        if (this.constants.assembleRoom == "") return
        val flag = this.parent.parent.mainRoomCollector.flags.firstOrNull { it.color == COLOR_BROWN
                && it.secondaryColor == COLOR_BROWN
                && it.pos.roomName == this.constants.assembleRoom } ?: return

        this.moveToFree(flag.pos)
        if (this.allInTheirPos(flag.pos)) this.constants.step = BattleGroupStep.GotoNeedRoom
    }



    private fun turnGroupToNeedRoom() {
        if (this.constants.assembleRoom == "") return
        val flag = this.parent.parent.mainRoomCollector.flags.firstOrNull { it.color == COLOR_BROWN
                && it.secondaryColor == COLOR_BROWN
                && it.pos.roomName == this.constants.assembleRoom } ?: return

        this.moveToChain(flag.pos)
    }

    private fun turnGroupToBattle() {
    }

    fun spawnCreep(mainRoom: MainRoom, spawn: StructureSpawn): BgSpawnResult {
        val queueBg = this.constants.creeps.firstOrNull { it.creep == null && it.spawnID == "" }
                ?: return BgSpawnResult.QueueEmpty
        val d: dynamic = object {}
        d["role"] = queueBg.role + 300
        d["slaveRoom"] = this.name
        d["mainRoom"] = mainRoom.name
        d["tickDeath"] = 0
        val spawnOptions: dynamic = object {}
        spawnOptions["memory"] = d
        return if (spawn.spawnCreep(queueBg.body, "bg_${queueBg.role}_${this.name}_${Game.time}_${spawn.id} ", spawnOptions.unsafeCast<SpawnOptions>()) == OK) {
            queueBg.spawnID = spawn.id
            BgSpawnResult.StartSpawn
        }else BgSpawnResult.CantSpawn
    }
}