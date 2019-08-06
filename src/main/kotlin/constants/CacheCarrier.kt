package constants

import screeps.api.BodyPartConstant
import screeps.api.CARRY
import screeps.api.MOVE

class CacheCarrier {
    var default : Boolean
    var needCarriers: Int
    var timeForDeath: Int
    var tickRecalculate: Int
    val needBody: Array<BodyPartConstant>

    constructor(default: Boolean? = null, tickRecalculate:Int? = null, needCarriers: Int? = null, timeForDeath: Int? = null, needBody: Array<BodyPartConstant>? = null) {
        this.default = default ?: true
        this.tickRecalculate = tickRecalculate ?: 0
        this.needCarriers = needCarriers ?: 1
        this.timeForDeath = timeForDeath ?: 100
        this.needBody = needBody ?: arrayOf<BodyPartConstant>(MOVE, MOVE, MOVE, MOVE, MOVE, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY)
    }

    fun toDynamic():dynamic {
        val d : dynamic = object {}
        d["default"] = this.default
        d["needCarriers"] = this.needCarriers
        d["timeForDeath"] = this.timeForDeath
        d["tickRecalculate"] = this.tickRecalculate
        d["needBody"] = this.needBody
        return d
    }

    companion object {
        fun initFromDynamic(d: dynamic): CacheCarrier {
            val default: Boolean? = if (d["default"] != null) d["default"] as Boolean else null
            val needCarriers: Int? = if (d["needCarriers"] != null) d["needCarriers"] as Int else null
            val timeForDeath: Int? = if (d["timeForDeath"] != null) d["timeForDeath"] as Int else null
            val tickRecalculate: Int? = if (d["tickRecalculate"] != null) d["tickRecalculate"] as Int else null
            val needBody: Array<BodyPartConstant>? = if (d["needBody"] != null) d["needBody"] as Array<BodyPartConstant> else null
            return CacheCarrier(default = default, needCarriers = needCarriers, timeForDeath = timeForDeath, tickRecalculate = tickRecalculate, needBody = needBody)
        }
    }
}
