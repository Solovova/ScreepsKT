package constants

import TypeBattleGroupMode
import BattleGroupStep
import BattleGroupQueueRecord
import screeps.api.BodyPartConstant
import BattleGroupCreeps
import screeps.api.Creep
import screeps.api.Game

class BattleGroupConstant {
    var mode: TypeBattleGroupMode = TypeBattleGroupMode.defence
    var roomName: String = ""
    var step: BattleGroupStep = BattleGroupStep.getPowerHostileCreep
    var queue: MutableList<BattleGroupQueueRecord> = mutableListOf()
    var creeps: MutableList<BattleGroupCreeps> = mutableListOf()

    fun fromDynamic(d: dynamic) {
        if (d == null) return
        if (d["mode"] != null) this.mode = TypeBattleGroupMode.valueOf(d["mode"] as String)
        if (d["roomName"] != null) this.roomName = d["roomName"] as String
        if (d["step"] != null) this.step = BattleGroupStep.valueOf(d["step"] as String)

        if (d["queue"] != null) {
            var ind = 0
            while (true) {
                if (d["queue"][ind] == null) break
                val battleGroupQueueRecord = BattleGroupQueueRecord()
                battleGroupQueueRecord.body = d["queue"][ind]["body"] as Array<BodyPartConstant>
                battleGroupQueueRecord.build = d["queue"][ind]["build"] as Boolean
                battleGroupQueueRecord.upgrade = d["queue"][ind]["upgrade"] as String
                this.queue.add(ind, battleGroupQueueRecord)
                ind++
            }
        }

        if (d["creeps"] != null) {
            var ind = 0
            var indList = 0
            while (true) {
                if (d["creeps"][ind] == null) break
                val creep: Creep? = Game.getObjectById(d["creeps"][ind]["creep"] as String)
                val role: Int = d["creeps"][ind]["role"] as Int
                if (creep == null) {
                    ind++
                    continue
                }
                this.creeps.add(indList, BattleGroupCreeps(creep, role))
                ind++
                indList++
            }
        }
    }

    fun toDynamic(): dynamic {
        val result: dynamic = object {}
        result["mode"] = this.mode.toString()
        result["roomName"] = this.roomName
        result["step"] = this.step.toString()

        result["queue"] = object {}
        for ((ind, battleGroupQueueRecord) in queue.withIndex()) {
            result["queue"][ind] = object {}
            result["queue"][ind]["body"] = battleGroupQueueRecord.body
            result["queue"][ind]["build"] = battleGroupQueueRecord.build
            result["queue"][ind]["upgrade"] = battleGroupQueueRecord.upgrade
        }

        result["creeps"] = object {}
        for ((ind, battleGroupCreeps) in creeps.withIndex()) {
            result["creeps"][ind] = object {}
            result["creeps"][ind]["creep"] = battleGroupCreeps.creep.id
            result["creeps"][ind]["role"] = battleGroupCreeps.role
        }
        return result
    }
}