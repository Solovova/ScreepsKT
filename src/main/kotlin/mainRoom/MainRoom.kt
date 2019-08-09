package mainRoom

import mainContext.MainContext
import constants.MainRoomConstant
import constants.SlaveRoomConstant
import slaveRoom.SlaveRoom
import constants.constantMainRoomInit
import creep.getDescribeForQueue
import constants.CacheCarrier
import mainContext.getCacheRecordRoom
import screeps.api.*
import screeps.api.structures.*
import kotlin.random.Random

class MainRoom(val parent: MainRoomCollector, val name: String, private val describe: String, val constant: MainRoomConstant) {
    val room: Room = Game.rooms[this.name] ?: throw AssertionError("Not room $this.name")
    val slaveRooms: MutableMap<String, SlaveRoom> = mutableMapOf()

    val need  = Array(3) {Array(20) {0}}
    val have  = Array(20) {0}
    private val haveForQueue = Array(20) {0}
    private val queue = mutableListOf<QueueSpawnRecord>()

    //StructureSpawn
    private var _structureSpawn: Map<String, StructureSpawn>? = null
    private val structureSpawn: Map<String, StructureSpawn>
        get() {
            if (this._structureSpawn == null)
                _structureSpawn = this.room.find(FIND_STRUCTURES).filter { it.structureType == STRUCTURE_SPAWN }.associate { it.id to it as StructureSpawn }
            return _structureSpawn ?: throw AssertionError("Error get StructureSpawn")
        }

    //StructureExtension
    private var _structureExtension: Map<String, StructureExtension>? = null
    private val structureExtension: Map<String, StructureExtension>
        get() {
            if (this._structureExtension == null)
                _structureExtension = this.room.find(FIND_STRUCTURES).filter { it.structureType == STRUCTURE_EXTENSION }.associate { it.id to it as StructureExtension }
            return _structureExtension ?: throw AssertionError("Error get StructureExtension")
        }

    //StructureController
    private var _structureController: Map<Int, StructureController>? = null
    val structureController: Map<Int, StructureController>
        get() {
            if (this._structureController == null)
                _structureController = this.room.find(FIND_STRUCTURES).filter { it.structureType == STRUCTURE_CONTROLLER }.withIndex().associate { it.index to it.value as StructureController}
            return _structureController ?: throw AssertionError("Error get StructureController")
        }

    //Source
    private var _source: Map<Int, Source>? = null
    val source: Map<Int, Source>
        get() {
            if (this._source == null)
                _source = this.room.find(FIND_SOURCES).sortedWith(Comparator<Source>{ a, b ->
                    when {
                        a.id > b.id -> 1
                        a.id < b.id -> -1
                        else -> 0
                    }}).withIndex().associate {it.index to it.value}
            return _source ?: throw AssertionError("Error get Source")
        }

    //ConstructionSite
    private var _constructionSite: Map<String, ConstructionSite>? = null
    val constructionSite: Map<String, ConstructionSite>
        get() {
            if (this._constructionSite == null)
                _constructionSite = this.room.find(FIND_CONSTRUCTION_SITES).associateBy { it.id }
            return _constructionSite ?: throw AssertionError("Error get ConstructionSite")
        }

    //StructureTower
    private var _structureTower: Map<String, StructureTower>? = null
    val structureTower: Map<String, StructureTower>
        get() {
            if (this._structureTower == null)
                _structureTower = this.room.find(FIND_STRUCTURES).filter { it.structureType == STRUCTURE_TOWER }.associate { it.id to it as StructureTower}
            return _structureTower ?: throw AssertionError("Error get StructureTower")
        }

    //StructureContainer
    private var _structureContainer: Map<String, StructureContainer>? = null
    val structureContainer: Map<String, StructureContainer>
        get() {
            if (this._structureContainer == null)
                _structureContainer = this.room.find(FIND_STRUCTURES).filter { it.structureType == STRUCTURE_CONTAINER }.associate { it.id to it as StructureContainer}
            return _structureContainer ?: throw AssertionError("Error get StructureContainer")
        }

    //StructureContainerNearSource
    private var _structureContainerNearSource: Map<Int, StructureContainer>? = null //id source
    val structureContainerNearSource: Map<Int, StructureContainer>
        get() {
            if (this._structureContainerNearSource == null) {
                val resultContainer = mutableMapOf<Int, StructureContainer>()
                for (sourceRec in this.source)
                    for (container in this.structureContainer.values)
                        if (!resultContainer.containsValue(container) && sourceRec.value.pos.inRangeTo(container.pos, 2))
                            resultContainer[sourceRec.key] = container
                _structureContainerNearSource = resultContainer
            }
            return _structureContainerNearSource ?: throw AssertionError("Error get StructureContainerNearSource")
        }

    //StructureContainerNearController
    private var _structureContainerNearController: Map<Int, StructureContainer>? = null //id source
    val structureContainerNearController: Map<Int, StructureContainer>
        get() {
            if (this._structureContainerNearController == null) {
                val resultContainer = mutableMapOf<Int, StructureContainer>()
                for (container in this.structureContainer.values) {
                    val protectStructureController: StructureController? = this.structureController[0]
                    if (protectStructureController != null && !this.structureContainerNearSource.containsValue(container) && protectStructureController.pos.inRangeTo(container.pos, 3))
                        resultContainer[0] = container
                }
                _structureContainerNearController = resultContainer
            }
            return _structureContainerNearController ?: throw AssertionError("Error get StructureContainerNearController")
        }

    //StructureStorage
    private var _structureStorage: Map<Int, StructureStorage>? = null
    val structureStorage: Map<Int, StructureStorage>
        get() {
            if (this._structureStorage == null)
                _structureStorage = this.room.find(FIND_STRUCTURES).filter { it.structureType == STRUCTURE_STORAGE }.withIndex().associate { it.index to it.value as StructureStorage}
            return _structureStorage ?: throw AssertionError("Error get StructureStorage")
        }

    fun buildCreeps() {
        this.needCorrection()
        for (slaveRoomRecord in this.slaveRooms) slaveRoomRecord.value.needCorrection()
        this.buildQueue()

        this.spawnCreep()
        this.showQueue()
    }

    private fun needCorrection() {
        when (this.getLevelOfRoom()){
            0 -> {
                when {
                    this.room.energyCapacityAvailable >= 800 ->
                        if (this.need[0][0] == 0) {
                            this.need[0][0] = 10
                            if (this.source.size == 1) this.need[0][0] = this.need[0][0] % 2 + 1
                        }
                    this.room.energyCapacityAvailable >= 400 ->
                        if (this.need[0][0] == 0) {
                            this.need[0][0] = 14
                            if (this.source.size == 1) this.need[0][0] = this.need[0][0] % 2 + 1
                        }
                    else -> if (this.need[0][0] == 0) {
                        this.need[0][0] = 16
                        if (this.source.size == 1) this.need[0][0] = this.need[0][0] % 2 + 1
                    }
                }

                if (this.source.containsKey(0) && this.structureContainerNearSource.containsKey(0)) {
                    if (this.need[1][1] == 0) this.need[1][1] = 1
                    //this.need[0][0] = this.need[0][0] - 2
                }

                if (this.source.containsKey(1) && this.structureContainerNearSource.containsKey(1))
                    if (this.need[1][3] == 0) this.need[1][3] = 1
            }

            1 -> {
                //1 harvester ,carrier ,filler , small harvester-filler, small filler
                //1.1 harvester ,carrier
                val carrierAuto0: CacheCarrier? = parent.parent.getCacheRecordRoom("mainContainer0",this)
                if (carrierAuto0!=null) {
                    if (this.need[1][1] == 0) this.need[1][1] = 1
                    if (this.need[1][2] == 0) this.need[1][2] = carrierAuto0.needCarriers
                }

                val carrierAuto1: CacheCarrier? = parent.parent.getCacheRecordRoom("mainContainer1",this)
                if (carrierAuto1!=null) {
                    if (this.need[1][3] == 0) this.need[1][3] = 1
                    if (this.need[1][4] == 0) this.need[1][4] = carrierAuto1.needCarriers
                }

                //1.2 filler
                if (this.need[0][5] ==0) this.need[0][5] = 1 //filler
                if (this.need[1][5] ==0) this.need[1][5] = 1 //filler

                //1.3 small filler
                if ((this.have[5]==0)&&(this.getEnergyInStorage()>2000))  this.need[0][9]=1
                if ((this.have[5]==0)&&(this.getEnergyInStorage()<=2000))  this.need[0][0]=2

                //2 Upgrader
                if (this.constant.SentEnergyToRoom == "") {
                    if (this.room.energyCapacityAvailable>=1800) {
                        this.need[1][6]=1
                        this.need[1][7]=1
                        this.need[2][6]=2
                        this.need[2][7]=3
                    }else{
                        this.need[1][6]=2
                        this.need[1][7]=2
                        this.need[2][6]=1
                        this.need[2][7]=2
                    }
                }else{
                    this.need[1][6]=0
                    this.need[1][7]=0
                    this.need[2][6]=0
                    this.need[2][7]=0
                }

                if (this.getEnergyInStorage()<this.constant.energyUpgradable) {
                    this.need[1][6]=0
                    this.need[1][7]=0
                    this.need[2][6]=0
                    this.need[2][7]=0
                }

                //2.1 Small upgrader
                if (this.need[0][6] == 0 && this.need[1][6] == 0 && this.need[2][6] == 0 &&
                        this.need[0][7] == 0 && this.need[1][7] == 0 && this.need[2][7] == 0 &&
                        this.have[6] == 0 && this.have[7] == 0 && this.getTicksToDowngrade() < 20000)
                    this.need[0][13]=1

                //8 Builder
                if ((this.constructionSite.isNotEmpty()) && (this.getEnergyInStorage() > this.constant.energyBuilder)) {
                    this.need[1][8]=1
                }
            }
        }

    }

    fun getEnergyInStorage():Int {
        var result: Int? = null
        if (this.structureStorage.containsKey(0)) result = this.structureStorage[0]?.store?.energy
        return result ?: 0
    }

    private fun buildQueue() {
        for (i in 0 until this.have.size) this.haveForQueue[i] = this.have[i]
        val fPriorityOfRole = if (this.getEnergyInStorage() < 2000) arrayOf(0, 9, 1, 3, 2, 4, 14, 5, 20, 7, 6, 10, 8, 11, 12, 13, 15, 16, 17, 18, 19, 21, 22, 23)
        else arrayOf(0, 9, 5, 20, 1, 3, 2, 4, 14, 7, 6, 10, 8, 11, 12, 13, 15, 16, 17, 18, 19, 21, 22, 23)

        //Main 0..1
        for (priority in 0..1) {
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


        //Slave 0
        for (slaveRoom in this.slaveRooms.values) {
            for (i in 0 until slaveRoom.have.size) slaveRoom.haveForQueue[i] = slaveRoom.have[i]
            slaveRoom.buildQueue(this.queue,0)
        }

        //Slave 1
        for (slaveRoom in this.slaveRooms.values) {
            slaveRoom.buildQueue(this.queue,1)
        }

        //Main 2
        for (priority in 2..2) {
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

        //Slave 2
        for (slaveRoom in slaveRooms.values)
            slaveRoom.buildQueue(this.queue,2)
    }


    private fun showQueue() {
        //ToDo show creepsRole who mainRoom.building
        var showText = "(${this.describe}):".padEnd(8)
        var textSpawning  = ""

        for (spawn in this.structureSpawn) {
            val recordSpawning = spawn.value.spawning
            if (recordSpawning != null) {
                val creep: Creep? = Game.creeps[recordSpawning.name]
                textSpawning += creep?.getDescribeForQueue(parent.parent) ?: ""
            }
        }

        showText += textSpawning
        showText = showText.padEnd(45) + ":"


        for (record in this.queue) {
            var prefix = ""
            if (record.mainRoom != record.slaveRoom)
                prefix = this.slaveRooms[record.slaveRoom]?.describe ?: "und"
            showText += "$prefix ${record.role},"
        }

        parent.parent.messenger("QUEUE", this.name, showText, COLOR_YELLOW, testBefore = "(${this.constant.note}".padEnd(30) + ")")
    }

    private fun getBodyRole(role: Int): Array<BodyPartConstant> {
        var result: Array<BodyPartConstant> = arrayOf()

        when (role) {
            0 -> {
                if (this.have[0] == 0 && this.room.energyAvailable < 800) result = arrayOf(MOVE, MOVE, WORK, CARRY)
                else if (this.room.energyCapacityAvailable < 400) result = arrayOf(MOVE, MOVE, WORK, CARRY)
                else if (this.room.energyCapacityAvailable < 800) result = arrayOf(MOVE, MOVE, WORK, WORK, CARRY, CARRY)
                else result = arrayOf(MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,WORK,WORK,CARRY,CARRY,CARRY,CARRY)
            }

            1,3 ->  {
                result = arrayOf(MOVE,MOVE,WORK,WORK,WORK,WORK,WORK,WORK,CARRY,CARRY)
            }

            2 ->  {
                val carrierAuto: CacheCarrier? = parent.parent.getCacheRecordRoom("mainContainer0",this)
                if (carrierAuto==null) {
                    parent.parent.messenger("ERROR", this.name, "Auto not exists mainContainer0", COLOR_RED)
                    result = arrayOf(MOVE,MOVE,MOVE,MOVE,MOVE,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY)
                }else{
                    result = carrierAuto.needBody
                }
            }

            4 ->  {
                val carrierAuto: CacheCarrier? = parent.parent.getCacheRecordRoom("mainContainer1",this)
                if (carrierAuto==null) {
                    parent.parent.messenger("ERROR", this.name, "Auto not exists mainContainer1", COLOR_RED)
                    result = arrayOf(MOVE,MOVE,MOVE,MOVE,MOVE,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY)
                }else{
                    result = carrierAuto.needBody
                }
            }

            5 -> {
                if (this.room.energyCapacityAvailable>=5000) result = arrayOf(MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY)
                else result = arrayOf(MOVE,MOVE,MOVE,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY)
            }

            6 -> {
                if (this.room.energyCapacityAvailable<1800) result = arrayOf(MOVE,MOVE,MOVE,MOVE,MOVE,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY)
                else if (this.room.energyCapacityAvailable<=5600) result = arrayOf(CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE)
                else result = arrayOf(MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY)
            }

            7 -> {
                if (this.room.energyCapacityAvailable<1800) result = arrayOf(MOVE,MOVE,MOVE,MOVE,WORK,WORK,WORK,WORK,WORK,WORK,WORK,WORK,CARRY,CARRY)
                else if (this.room.energyCapacityAvailable<3000) result = arrayOf(WORK, WORK, WORK, WORK, WORK, WORK, WORK, WORK, WORK, WORK, WORK, WORK, CARRY, CARRY, CARRY, CARRY, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE)
                else if (this.room.energyCapacityAvailable<=5600) result = arrayOf(MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,WORK,WORK,WORK,WORK,WORK,WORK,WORK,WORK,WORK,WORK,WORK,WORK,WORK,WORK,WORK,WORK,WORK,WORK,WORK,WORK,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY)
                else result = arrayOf(MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,WORK,WORK,WORK,WORK,WORK,WORK,WORK,WORK,WORK,WORK,WORK,WORK,WORK,WORK,WORK,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY)
            }

            8 -> {
                result = arrayOf(MOVE,MOVE,MOVE,MOVE,MOVE,WORK,WORK,WORK,WORK,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY)
            }

            9 -> {
                result = arrayOf(CARRY, CARRY, MOVE)
            }

            13 -> {
                result = arrayOf(WORK, CARRY, MOVE)
            }
        }
        return result
    }

    private fun spawnCreep() {
        if (!this.constant.creepSpawn) return
        for (spawn in this.structureSpawn.values) {
            if (this.queue.size == 0) return
            if (!spawn.isActive() || spawn.spawning != null) continue
            var result:ScreepsReturnCode = OK

            val d: dynamic = object{}
            d["role"] = this.queue[0].role
            d["slaveRoom"] = this.queue[0].slaveRoom
            d["mainRoom"] = this.queue[0].mainRoom
            val spawnOptions: dynamic = object{}
            spawnOptions["memory"] = d

            if (this.queue[0].slaveRoom == this.queue[0].mainRoom)
                result = spawn.spawnCreep(getBodyRole(this.queue[0].role), "mst_${this.queue[0].mainRoom}_${this.queue[0].slaveRoom}_${Game.time} ", spawnOptions.unsafeCast<SpawnOptions>())
            else {
                val slaveRoom = this.slaveRooms[this.queue[0].slaveRoom]
                if (slaveRoom!=null)
                    result = spawn.spawnCreep(slaveRoom.getBodyRole(this.queue[0].role), "mst_${this.queue[0].mainRoom}_${this.queue[0].slaveRoom}_${Game.time} ", spawnOptions.unsafeCast<SpawnOptions>())
            }
            if (result == OK) {
                this.queue[0].slaveRoom != this.queue[0].mainRoom
                val slaveRoom: SlaveRoom? = this.slaveRooms[this.queue[0].slaveRoom]
                slaveRoom?.profitMinus(this.queue[0].role)

                this.queue.removeAt(0)
            }
        }
    }

    //ToDo test
    fun getSpawnOrExtensionForFiling(pos: RoomPosition, mainContext: MainContext): Structure? {
        data class StructureData (val need: Int, val priority: Int)
        val needs : MutableMap<Structure,StructureData> = mutableMapOf()

        // Загружаем все extension
        for (extension in this.structureExtension.values)
            if (extension.energyCapacity > extension.energy)
                needs[extension] = StructureData(extension.energyCapacity - extension.energy,1)

        // Загружаем все спавны
        for (spawn in this.structureSpawn.values)
            if (spawn.energyCapacity > spawn.energy)
                needs[spawn] = StructureData(spawn.energyCapacity - spawn.energy,1)


        // Загружаем Tower если енергия меньше 1000
        //ToDo set priority 0 if have hostile creeps and queue < 2
        for (tower in this.structureTower.values)
            if (tower.energy < 400) needs[tower] = StructureData(tower.energyCapacity - tower.energy,3)

        if (needs.isEmpty()) return null
        // Производим коррекцию с учетем заданий которые делаются и ищем ближайший
        var resultObject: Structure? = null
        var resultRange = 1000
        var resultPriority = 1000

        for (need in needs) {
            if (need.value.need > mainContext.tasks.getEnergyCaringTo(need.key.id)) {
                val tTmpRange = pos.getRangeTo(need.key.pos)
                val tTmpPriority = need.value.priority
                if ((tTmpPriority < resultPriority) || (tTmpRange < resultRange && tTmpPriority == resultPriority)) {
                    resultRange = tTmpRange
                    resultPriority = tTmpPriority
                    resultObject = need.key
                }
            }
        }
        return resultObject
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
            if (tRangeTmp < tDistance && mainContext.tasks.getSourceHarvestNum(source.id) < 5 && source.energy > 100) {
                tDistance = tRangeTmp
                tSource = source
            }
        }
        return tSource
    }
    //0 - only role 0 creep
    //1 - Storage, 3 container, energy >300+20*50 1300
    private fun getLevelOfRoom(): Int {
        if (this.room.energyCapacityAvailable >= 1300 &&
                this.structureContainerNearSource.size == this.source.size &&
                this.structureContainerNearController.size == 1 &&
                this.structureStorage.size == 1)
            return 1

        return 0
    }

    private fun getTicksToDowngrade(): Int {
        var result = 0
        val protectedStructureController = this.structureController[0]
        if (protectedStructureController != null) result = protectedStructureController.ticksToDowngrade
        return result
    }


    init {
        constantMainRoomInit(this)

        this.constant.slaveRooms.forEachIndexed { index, slaveName ->
            val slaveRoomConstant: SlaveRoomConstant? = this.constant.slaveRoomConstantContainer[slaveName]
            if (slaveRoomConstant != null)
                slaveRooms[slaveName] = SlaveRoom(this, slaveName, "${this.describe}S$index", slaveRoomConstant)
            else parent.parent.messenger("ERROR", "${this.name} $slaveName", "initialization don't see constant", COLOR_RED)
        }
    }

    fun runNotEveryTick() {
        for (record in this.slaveRooms) {
            try {
                record.value.runNotEveryTick()
            }catch (e: Exception) {
                parent.parent.messenger("ERROR", "Slave not every tick", record.value.name, COLOR_RED)
            }
        }
        this.building()

        //Test
        if (!this.setNextTickRun()) return

    }

    private fun setNextTickRun(): Boolean {
        if (this.constant.roomRunNotEveryTickNextTickRun > Game.time) return false
        this.constant.roomRunNotEveryTickNextTickRun = Game.time + Random.nextInt(parent.parent.constants.globalConstant.roomRunNotEveryTickTicksPauseMin,
                parent.parent.constants.globalConstant.roomRunNotEveryTickTicksPauseMax)
        parent.parent.messenger("TEST", this.name, "Main room not every tick run. Next tick: ${this.constant.roomRunNotEveryTickNextTickRun}", COLOR_GREEN)
        return true
    }

    fun runInStartOfTick() {
        this.runTower()
        this.buildCreeps()
        this.doSnapShot()

        for (room in this.slaveRooms.values) {
            try {
                room.runInStartOfTick()
            }catch (e: Exception) {
                parent.parent.messenger("ERROR", "Slave room in start", room.name, COLOR_RED)
            }
        }
    }
}
