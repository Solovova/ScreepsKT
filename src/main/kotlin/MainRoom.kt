import screeps.api.*
import screeps.api.structures.*

class QueueSpawnRecord(val role: Int, val srcRoom: String, val dstRoom: String)

class MainRoom(val name: String, private val describe: String) {
    val room: Room = Game.rooms[this.name] ?: throw AssertionError("Not room $this.name")

    val need  = Array(3) {Array(20) {0}}
    val have  = Array(20) {0}
    private val haveForQueue = Array(20) {0}
    private val queue = mutableListOf<QueueSpawnRecord>()

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

    //ConstructionSite
    private var _constructionSite: Map<String, ConstructionSite>? = null
    val constructionSite: Map<String, ConstructionSite>
        get() {
            if (this._constructionSite == null) {
                messenger("RECALCULATE", name, "ConstructionSite", COLOR_YELLOW)
                _constructionSite = this.room.find(FIND_CONSTRUCTION_SITES).associateBy { it.id }
            }
            return _constructionSite ?: throw AssertionError("Error get ConstructionSite")
        }

    //StructureTower
    private var _structureTower: Map<String, StructureTower>? = null
    val structureTower: Map<String, StructureTower>
        get() {
            if (this._structureTower == null) {
                messenger("RECALCULATE", name, "StructureTower", COLOR_YELLOW)
                _structureTower = this.room.find(FIND_STRUCTURES).filter { it.structureType == STRUCTURE_TOWER }.associate { it.id to it as StructureTower}
            }
            return _structureTower ?: throw AssertionError("Error get StructureTower")
        }

    //StructureContainer //ToDo test
    private var _structureContainer: Map<String, StructureContainer>? = null
    val structureContainer: Map<String, StructureContainer>
        get() {
            if (this._structureContainer == null) {
                messenger("RECALCULATE", name, "StructureContainer", COLOR_YELLOW)
                _structureContainer = this.room.find(FIND_STRUCTURES).filter { it.structureType == STRUCTURE_CONTAINER }.associate { it.id to it as StructureContainer}
            }
            return _structureContainer ?: throw AssertionError("Error get StructureContainer")
        }

    //StructureContainerNearSource //ToDo test
    private var _structureContainerNearSource: Map<String, StructureContainer>? = null //id source
    val structureContainerNearSource: Map<String, StructureContainer>
        get() {
            if (this._structureContainerNearSource == null) {
                messenger("RECALCULATE", name, "StructureContainerNearSource", COLOR_YELLOW)
                val resultContainer = mutableMapOf<String, StructureContainer>()
                for (source in this.source.values)
                    for (container in this.structureContainer.values)
                        if (!resultContainer.containsValue(container) && source.pos.inRangeTo(container.pos, 2))
                            resultContainer[source.id] = container
                _structureContainerNearSource = resultContainer
            }
            return _structureContainerNearSource ?: throw AssertionError("Error get StructureContainerNearSource")
        }

    //StructureContainerNearController  //ToDo test
    private var _structureContainerNearController: Map<String, StructureContainer>? = null //id source
    val structureContainerNearController: Map<String, StructureContainer>
        get() {
            if (this._structureContainerNearController == null) {
                messenger("RECALCULATE", name, "StructureContainerNearController", COLOR_YELLOW)
                val resultContainer = mutableMapOf<String, StructureContainer>()

                for (container in this.structureContainer.values)
                    if (!this.structureContainerNearSource.containsValue(container) && this.structureController.pos.inRangeTo(container.pos, 3))
                        resultContainer[this.structureController.id] = container
                _structureContainerNearController = resultContainer
            }
            return _structureContainerNearController ?: throw AssertionError("Error get StructureContainerNearController")
        }

    //StructureStorage //ToDo test
    private var _structureStorage: Map<String, StructureStorage>? = null
    val structureStorage: Map<String, StructureStorage>
        get() {
            if (this._structureStorage == null) {
                messenger("RECALCULATE", name, "StructureStorage", COLOR_YELLOW)
                _structureStorage = this.room.find(FIND_HOSTILE_STRUCTURES).filter { it.structureType == STRUCTURE_STORAGE }.associate { it.id to it as StructureStorage}
            }
            return _structureStorage ?: throw AssertionError("Error get StructureStorage")
        }


    fun buildCreeps() {
        this.needCorrection()
        this.buildQueue()
        this.showQueue()
        this.spawnCreep()
    }

    private fun needCorrection() {
        when (this.getLevelOfRoom()){
            0 -> {
                when {
                    this.room.energyCapacityAvailable >= 800 -> this.need[0][0] = 10
                    this.room.energyCapacityAvailable >= 400 -> this.need[0][0] = 14
                    else -> this.need[0][0] = 16
                }

                if (this.source.size == 1) this.need[0][0] = this.need[0][0] % 2 + 1
            }

            1 -> {

            }
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
        //ToDo show creepsRole who building
        var showText = "Queue: (${this.describe}) "
        for (record in this.queue) {
            var prefix = ""
            if (record.dstRoom != record.srcRoom) prefix = "S0" //ToDo to slave room prefix
            showText += "$prefix ${record.role},"
        }
        console.log(showText)
    }

    private fun getBodyRole(role: Int): Array<BodyPartConstant> {
        var result: Array<BodyPartConstant> = arrayOf()

        when (role) {
            0 -> {
                if (this.room.energyCapacityAvailable < 400 || this.have[0] < 1) result = arrayOf(MOVE, MOVE, WORK, CARRY)
                else if (this.room.energyCapacityAvailable < 800) result = arrayOf(MOVE, MOVE, WORK, WORK, CARRY, CARRY)
                else result = arrayOf(MOVE, MOVE, MOVE, WORK, WORK, WORK, WORK, CARRY, CARRY, CARRY, CARRY)
            }
        }
        return result
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

    fun getSpawnOrExtensionForFiling(pos: RoomPosition, mainContext: MainContext): Structure? {
        val needs : MutableMap<Structure,Int> = mutableMapOf()

        // Загружаем все спавны
        for (spawn in this.structureSpawn.values)
            if (spawn.energyCapacity > spawn.energy) needs[spawn] = spawn.energyCapacity - spawn.energy

        // Загружаем все extension
        for (extension in this.structureExtension.values)
            if (extension.energyCapacity > extension.energy) needs[extension] = extension.energyCapacity - extension.energy

        // Загружаем Tower если енергия меньше 1000
        for (tower in this.structureTower.values)
            if (tower.energy < 400) needs[tower] = 1000 - tower.energy

        if (needs.isEmpty()) return null
        // Производим коррекцию с учетем заданий которые делаются и ищем ближайший
        var tObject: Structure? = null
        var tMinRange = 1000
        for (need in needs) {
            //console.log("Test ${need.key} need ${need.value}")
            if (need.value > mainContext.tasks.getEnergyCaringTo(need.key.id)) {
                val tTmpRange = pos.getRangeTo(need.key.pos)
                if (tTmpRange < tMinRange) {
                    tMinRange = tTmpRange
                    tObject = need.key
                }
            }
        }
        return tObject
    }

    fun getConstructionSite(pos: RoomPosition): ConstructionSite? {
        var tObject: ConstructionSite? = null
        var tMinRange = 1000
        for (construct in this.constructionSite.values) {
            val tTmpRange = pos.getRangeTo(construct.pos)
            if (tTmpRange < tMinRange) {
                tMinRange = tTmpRange
                tObject = construct
            }
        }
        return tObject
    }

    fun getSourceForHarvest(pos: RoomPosition, mainContext: MainContext) : Source {
        var tSource: Source = this.source.values.first()
        var tDistance = 1000
        for (source in this.source.values) {
            val tRangeTmp = pos.getRangeTo(source.pos)
            console.log(mainContext.tasks.getSourceHarvestNum(source.id))
            if (tRangeTmp < tDistance && mainContext.tasks.getSourceHarvestNum(source.id) < 5 && source.energy > 100) {
                tDistance = tRangeTmp
                tSource = source
            }
        }
        return tSource
    }
    //0 - only role 0 creep
    //1 - Storage, 3 container, energy >300+20*50 1300
    fun getLevelOfRoom(): Int {
        if (this.room.energyCapacityAvailable >= 1300 &&
                this.structureContainerNearSource.size == this.source.size &&
                this.structureContainerNearController.size == 1 &&
                this.structureStorage.size == 1)
            return 1

        return 0
    }

    init {
        constantInit(this)
    }
}
