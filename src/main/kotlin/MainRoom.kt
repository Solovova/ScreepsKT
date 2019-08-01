import screeps.api.*
import screeps.api.structures.SpawnOptions
import screeps.api.structures.StructureController
import screeps.api.structures.StructureSpawn
import screeps.api.structures.StructureExtension

class QueueSpawnRecord(val role: Int, val srcRoom: String, val dstRoom: String)

class MainRoom {
    val name: String
    private val describe: String
    private val room: Room

    val need  = Array(3) {Array(20) {0}}
    val have  = Array(20) {0}
    private val haveForQueue = Array(20) {0}
    private val queue = mutableListOf<QueueSpawnRecord>()

    constructor(name: String, describe: String) {
        this.name = name
        this.describe = describe
        this.room = Game.rooms[this.name] ?: throw AssertionError("Not room $this.name")
        constantInit(this)
    }

    //StructureSpawn
    private var _structureSpawn: Map<String, StructureSpawn>? = null
    val structureSpawn: Map<String, StructureSpawn>
        get() {
            if (this._structureSpawn == null) {
                messenger("RECALCULATE", name, "StructureSpawn", COLOR_YELLOW)
                _structureSpawn = this.room.find(FIND_STRUCTURES).filter { it.structureType == STRUCTURE_SPAWN }.associate { it.id to it as StructureSpawn }
            }
            return _structureSpawn ?: throw AssertionError("Error get StructureSpawn")
        }

    //StructureExtension
    private var _structureExtension: Map<String, StructureExtension>? = null
    val structureExtension: Map<String, StructureExtension>
        get() {
            if (this._structureExtension == null) {
                messenger("RECALCULATE", name, "StructureExtension", COLOR_YELLOW)
                _structureExtension = this.room.find(FIND_STRUCTURES).filter { it.structureType == STRUCTURE_EXTENSION }.associate { it.id to it as StructureExtension }
            }
            return _structureExtension ?: throw AssertionError("Error get StructureExtension")
        }

    //StructureController
    private var _structureController: StructureController? = null
    val structureController: StructureController
        get() {
            if (this._structureController == null) {
                messenger("RECALCULATE", name, "StructureController", COLOR_YELLOW)
                _structureController = this.room.find(FIND_STRUCTURES).firstOrNull { it.structureType == STRUCTURE_CONTROLLER } as StructureController
            }
            return _structureController ?: throw AssertionError("Error get StructureController")
        }

    //Source
    private var _source: Map<String, Source>? = null
    val source: Map<String, Source>
        get() {
            if (this._source == null) {
                messenger("RECALCULATE", name, "Source", COLOR_YELLOW)
                _source = this.room.find(FIND_SOURCES).associateBy { it.id }
            }
            return _source ?: throw AssertionError("Error get Source")
        }

    fun buildCreeps() {
        this.needCorrection()
        this.buildQueue()
        this.showQueue()
        this.spawnCreep()
    }

    private fun needCorrection() {
        if (this.source.size > 1) {
            if (this.room.energyCapacityAvailable >= 400) this.need[0][0] = 8
            else this.need[0][0] = 10
        } else {
            if (this.room.energyCapacityAvailable >= 400) this.need[0][0] = 4
            else this.need[0][0] = 5
        }
    }

    private fun buildQueue() {
        for (i in 0 until this.need.size) this.haveForQueue[i] = this.have[i]
        val fPriorityOfRole = arrayOf(0)
        for (priority in 0..2) {
            for (fRole in fPriorityOfRole) {
                var fNeed = this.need[0][fRole]
                if (priority >= 1) fNeed += this.need[1][fRole]
                if (priority >= 2) fNeed += this.need[2][fRole]
                while (this.haveForQueue[fRole] < fNeed) {
                    this.haveForQueue[fRole]++
                    this.queue.add(QueueSpawnRecord(fRole, this.name, this.name))
                }
            }
        }
    }

    private fun showQueue() {
        //ToDo show creep who building
        var showText = "Queue: (${this.describe}) "
        for (record in this.queue) {
            var prefix = ""
            if (record.dstRoom != record.srcRoom) prefix = "S0" //ToDo to slave room prefix
            showText += "$prefix ${record.role},"
        }
        console.log(showText)
    }

    private fun getBodyRole(role: Int): Array<BodyPartConstant> {
        when (role) {
            0 -> {
                if (this.room.energyCapacityAvailable < 400 || this.have[0] < 1) return arrayOf(MOVE, MOVE, WORK, CARRY)
                return arrayOf(MOVE, MOVE, WORK, WORK, CARRY, CARRY)
            }
            else -> return arrayOf(MOVE, MOVE, WORK, CARRY)
        }
    }

    private fun spawnCreep() {
        for (spawn in this.structureSpawn.values) {
            if (this.queue.size == 0) return
            if (!spawn.isActive() || spawn.spawning != null) continue
            var result:ScreepsReturnCode = OK
            if (this.queue[0].role == 0) {
                val d: dynamic = object{}
                d["role"] = this.queue[0].role
                d["srcRoom"] = this.queue[0].srcRoom
                d["dstRoom"] = this.queue[0].dstRoom
                val spawnOptions: dynamic = object{}
                spawnOptions["memory"] = d
                result = spawn.spawnCreep(getBodyRole(this.queue[0].role), "mst_${this.queue[0].dstRoom}_${this.queue[0].dstRoom}_${Game.time} ", spawnOptions.unsafeCast<SpawnOptions>())
            }
            if (result == OK) {this.queue.removeAt(0) }
        }
    }
}
