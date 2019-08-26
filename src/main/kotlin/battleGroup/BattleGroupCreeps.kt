package battleGroup

import BattleGroupCreep
import mainContext.messenger
import mainRoom.MainRoom
import screeps.api.COLOR_YELLOW
import screeps.api.Game
import screeps.api.OK
import screeps.api.structures.SpawnOptions
import screeps.api.structures.StructureSpawn

class BattleGroupCreeps(val parent: BattleGroup) {
    val creeps: MutableList<BattleGroupCreep> = parent.constants.creeps

    fun spawnCreep(mainRoom: MainRoom, spawn: StructureSpawn): Boolean { //true - queue is empty
        this.parent.parent.parent.messenger("INFO", mainRoom.name, "Need spawn creep for bg: ${this.parent.name}", COLOR_YELLOW)
        val queueBg = this.creeps.firstOrNull { it.creep == null && it.spawnID == "" }
                ?: return true
        val d: dynamic = object {}
        d["role"] = queueBg.role + 300
        d["slaveRoom"] = this.parent.name
        d["mainRoom"] = mainRoom.name
        d["tickDeath"] = 0
        val spawnOptions: dynamic = object {}
        spawnOptions["memory"] = d
        spawn.spawnCreep(queueBg.body, "bg_${queueBg.role}_${parent.name}_${Game.time}_${spawn.id} ", spawnOptions.unsafeCast<SpawnOptions>())
        queueBg.spawnID = spawn.id
        return false
    }
}