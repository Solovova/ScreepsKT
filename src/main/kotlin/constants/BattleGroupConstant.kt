package constants

import TypeBattleGroupMode
import BattleGroupStep
import screeps.api.BodyPartConstant
import BattleGroupCreep
import screeps.api.Creep
import screeps.api.Game

class BattleGroupConstant {
    var mode: TypeBattleGroupMode = TypeBattleGroupMode.Defence
    var roomName: String = ""
    var assembleRoom: String = ""
    var step: BattleGroupStep = BattleGroupStep.GetPowerHostileCreep
    var creeps: MutableList<BattleGroupCreep> = mutableListOf()

    fun fromDynamic(d: dynamic) {
        if (d == null) return
        if (d["mode"] != null) this.mode = TypeBattleGroupMode.valueOf(d["mode"] as String)
        if (d["roomName"] != null) this.roomName = d["roomName"] as String
        if (d["assembleRoom"] != null) this.assembleRoom = d["assembleRoom"] as String
        if (d["step"] != null) this.step = BattleGroupStep.valueOf(d["step"] as String)

        if (d["creeps"] != null) {
            var ind = 0
            while (true) {
                if (d["creeps"][ind] == null) break
                val creep: Creep? = Game.getObjectById(d["creeps"][ind]["creep"] as String)
                val spawnID: String = (d["creeps"][ind]["spawnID"] ?: "") as String
                val role: Int = d["creeps"][ind]["role"] as Int
                val body: Array<BodyPartConstant> = d["creeps"][ind]["body"] as Array<BodyPartConstant>
                val upgrade: String = (d["creeps"][ind]["upgrade"] ?: "") as String
                this.creeps.add(ind, BattleGroupCreep(creep = creep, role = role, body = body, upgrade = upgrade, spawnID = spawnID))
                ind++
            }
        }
    }

    fun toDynamic(): dynamic {
        val result: dynamic = object {}
        result["mode"] = this.mode.toString()
        result["roomName"] = this.roomName
        result["assembleRoom"] = this.assembleRoom
        result["step"] = this.step.toString()

        result["creeps"] = object {}
        for ((ind, battleGroupCreep) in creeps.withIndex()) {
            result["creeps"][ind] = object {}

//            if (battleGroupCreep.creep == null) {
//                result["creeps"][ind]["creep"] = ""
//            }else{
//                result["creeps"][ind]["creep"] = battleGroupCreep.creep?.id ?: ""
//            }
            console.log(battleGroupCreep.creep == null)
            console.log(battleGroupCreep.creep?.id ?: "")
            result["creeps"][ind]["creep"] = battleGroupCreep.creep?.id ?: ""
            result["creeps"][ind]["role"] = battleGroupCreep.role
            result["creeps"][ind]["upgrade"] = battleGroupCreep.upgrade
            result["creeps"][ind]["body"] = battleGroupCreep.body
            result["creeps"][ind]["spawnID"] = battleGroupCreep.spawnID
        }
        return result
    }
}