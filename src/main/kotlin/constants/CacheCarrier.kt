package constants

import screeps.api.BodyPartConstant
import screeps.api.CARRY
import screeps.api.MOVE
import screeps.api.*


class CacheCarrier {
    var default : Boolean
    var needCarriers: Int
    var timeForDeath: Int
    var tickRecalculate: Int
    val needBody: Array<BodyPartConstant>
    var mPath: Array<RoomPosition> = arrayOf()

    constructor(default: Boolean? = null, tickRecalculate:Int? = null, needCarriers: Int? = null, timeForDeath: Int? = null, needBody: Array<BodyPartConstant>? = null, mPath: Array<RoomPosition>? = null) {
        this.default = default ?: true
        this.tickRecalculate = tickRecalculate ?: 0
        this.needCarriers = needCarriers ?: 1
        this.timeForDeath = timeForDeath ?: 0
        this.needBody = needBody ?: arrayOf<BodyPartConstant>(MOVE, MOVE, MOVE, MOVE, MOVE, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY)
        this.mPath = mPath ?: arrayOf()
    }

    fun toDynamic():dynamic {
        val d : dynamic = object {}
        d["default"] = this.default
        d["needCarriers"] = this.needCarriers
        d["timeForDeath"] = this.timeForDeath
        d["tickRecalculate"] = this.tickRecalculate
        d["needBody"] = this.needBody
        if (this.mPath.isNotEmpty()) d["mPath"] = this.mPath
        return d
    }

    companion object {
        fun initFromDynamic(d: dynamic): CacheCarrier {
            val default: Boolean? = if (d["default"] != null) d["default"] as Boolean else null
            val needCarriers: Int? = if (d["needCarriers"] != null) d["needCarriers"] as Int else null
            val timeForDeath: Int? = if (d["timeForDeath"] != null) d["timeForDeath"] as Int else null
            val tickRecalculate: Int? = if (d["tickRecalculate"] != null) d["tickRecalculate"] as Int else null
            val needBody: Array<BodyPartConstant>? = if (d["needBody"] != null) d["needBody"] as Array<BodyPartConstant> else null

            var mPath: Array<RoomPosition>? = null
            if (d["mPath"] != null) {
                mPath = arrayOf()
                for (ind in 0..1000) {
                    if (d["mPath"][ind] == null) break
                    mPath += RoomPosition(d["mPath"][ind]["x"] as Int, d["mPath"][ind]["y"] as Int, d["mPath"][ind]["roomName"] as String)
                }
            }

            return CacheCarrier(default = default, needCarriers = needCarriers, timeForDeath = timeForDeath, tickRecalculate = tickRecalculate, needBody = needBody, mPath = mPath)
        }
    }
}
