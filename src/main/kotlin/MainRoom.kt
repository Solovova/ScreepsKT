import screeps.api.*
import screeps.api.structures.*

data class QueueSpawnRecord(val role: Int, val mainRoom: String, val slaveRoom: String)

class MainRoom(val name: String, private val describe: String, private val slaveRoomsName: Array<String>) {
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
    private val structureContainer: Map<String, StructureContainer>
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
        this.buildQueue()
        this.showQueue()
        this.spawnCreep()
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
            }

            1 -> {
                //1 harvester ,carrier ,filler , small harvester-filler, small filler
                //1.1 harvester ,carrier
                if (this.source.containsKey(0) && this.structureContainerNearSource.containsKey(0)) {
                    if (this.need[1][1] == 0) this.need[1][1] = 1
                    if (this.need[1][2] == 0) this.need[1][2] = 1 //ToDo auto
                }
                if (this.source.containsKey(1) && this.structureContainerNearSource.containsKey(1)) {
                    if (this.need[1][3] == 0) this.need[1][3] = 1
                    if (this.need[1][4] == 0) this.need[1][4] = 1 //ToDo auto
                }

                //1.2 filler
                if (this.need[0][5] ==0) this.need[0][5]=1 //filler

                this.need[0][0]=1

                //1.3 small filler
                //if ((this.have[5]==0)&&(this.getEnergyInStorage()>2000))  this.need[0][9]=1
                //if ((this.have[5]==0)&&(this.getEnergyInStorage()<=2000))  this.need[0][0]=2
            }
        }

    }

    private fun getEnergyInStorage():Int {
        var result: Int? = null
        if (this.structureStorage.containsKey(0)) result = this.structureStorage[0]?.store?.energy
        return result ?: 0
    }

    private fun buildQueue() {
        for (i in 0 until this.have.size) this.haveForQueue[i] = this.have[i]
        val fPriorityOfRole = if (this.getEnergyInStorage() < 2000) arrayOf(0, 9, 1, 3, 2, 4, 14, 5, 20, 6, 7, 10, 8, 11, 12, 13, 15, 16, 17, 18, 19, 21, 22, 23)
        else arrayOf(0, 9, 5, 20, 1, 3, 2, 4, 14, 6, 7, 10, 8, 11, 12, 13, 15, 16, 17, 18, 19, 21, 22, 23)

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
            for (i in 0 until slaveRoom.need.size) slaveRoom.haveForQueue[i] = slaveRoom.have[i]
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
        //ToDo show creepsRole who building
        var showText = "Queue: (${this.describe}) "
        for (record in this.queue) {
            var prefix = ""
            if (record.mainRoom != record.slaveRoom)
                prefix = "${this.slaveRooms[record.slaveRoom]?.describe ?: "und"} (${this.slaveRooms[record.slaveRoom]?.name ?: "u"})"
            showText += "$prefix ${record.role},"
        }
        messenger("QUEUE",this.name,showText, COLOR_YELLOW)
    }

    private fun getBodyRole(role: Int): Array<BodyPartConstant> {
        var result: Array<BodyPartConstant> = arrayOf()

        when (role) {
            0 -> {
                if (this.have[0] == 0 && this.room.energyAvailable < 800) result = arrayOf(MOVE, MOVE, WORK, CARRY)
                else if (this.room.energyCapacityAvailable < 400) result = arrayOf(MOVE, MOVE, WORK, CARRY)
                else if (this.room.energyCapacityAvailable < 800) result = arrayOf(MOVE, MOVE, WORK, WORK, CARRY, CARRY)
                else result = arrayOf(MOVE, MOVE, MOVE, WORK, WORK, WORK, WORK, CARRY, CARRY, CARRY, CARRY)
            }

            1,3 ->  {
                result = arrayOf(MOVE,MOVE,MOVE,WORK,WORK,WORK,WORK,WORK,WORK,CARRY,CARRY)
            }

            2,4 ->  {
                //return objRoom.ContainersWaysCarrierNeed[indContainer].BodyCarriers; //ToDo auto
                result = arrayOf(MOVE,MOVE,MOVE,MOVE,MOVE,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY)
            }

            5 -> {
                if (this.room.energyCapacityAvailable>=5000) result = arrayOf(MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY)
                else result = arrayOf(MOVE,MOVE,MOVE,CARRY,CARRY,CARRY,CARRY,CARRY,CARRY)
            }
        }
        return result
    }

    private fun spawnCreep() {
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
            if (result == OK) {this.queue.removeAt(0) }
        }
    }

    //ToDo test
    fun getSpawnOrExtensionForFiling(pos: RoomPosition, mainContext: MainContext): Structure? {
        data class StructureData (val need: Int, val priority: Int)
        val needs : MutableMap<Structure,StructureData> = mutableMapOf()

        // Загружаем все extension
        for (extension in this.structureExtension.values)
            if (extension.energyCapacity > extension.energy)
                needs[extension] = StructureData(extension.energyCapacity - extension.energy,2)

        // Загружаем все спавны
        for (spawn in this.structureSpawn.values)
            if (spawn.energyCapacity > spawn.energy)
                needs[spawn] = StructureData(spawn.energyCapacity - spawn.energy,1)


        // Загружаем Tower если енергия меньше 1000
        //ToDo set priority 0 if have hostile creeps and queue < 2
        for (tower in this.structureTower.values)
            if (tower.energy < 400) needs[tower] = StructureData(tower.energyCapacity - tower.energy,2)

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

    fun recalculateWays() {
//        if (this.getLevelOfRoom() != 2) return
//        val Storage:StructureStorage = this.structureStorage[0] ?: return
//
//        for (i in 0 until objRoom.Containers.length) {
//            if (objRoom.ContainersWaysToStorage[i] != null && fNeedRecalculate === 0) continue
//            if (objRoom.Containers[i] == null) continue
//            val fContainer = Game.getObjectById(objRoom.Containers[i]) ?: continue
//
//            val ret = afunc.GetWayFromPosToPos(fContainer.pos, fStorage.pos)
//            if (!ret.incomplete) objRoom.ContainersWaysToStorage[i] = ret.path
//
//            var fEnergyCapacity = SOURCE_ENERGY_CAPACITY + 500
//
//            if (!ret.incomplete) objRoom.ContainersWaysWaight[i] = fEnergyCapacity * ret.path.length * 2 / SPAWN_ENERGY_CAPACITY
//            if (!ret.incomplete) objRoom.ContainersWaysCarrierNeed[i] = afunc.GetInfoCarierNeed(objRoom, i)
//        }
    }

    init {
        constantMainRoomInit(this)

        //SlaveRooms
        this.slaveRoomsName.forEachIndexed { index, nameSlaveRoom ->
            var typeSlaveRoom: Int = 0
            if (Memory["mainRoomsData"] != null
                    && Memory["mainRoomsData"][this.name] != null
                    && Memory["mainRoomsData"][this.name][nameSlaveRoom] != null
                && Memory["mainRoomsData"][this.name][nameSlaveRoom]["type"] != null)
                typeSlaveRoom = Memory["mainRoomsData"][this.name][nameSlaveRoom]["type"] as Int
            slaveRooms[nameSlaveRoom] = SlaveRoom(this, nameSlaveRoom, "${this.describe}S$index", typeSlaveRoom)
        }
    }


}
