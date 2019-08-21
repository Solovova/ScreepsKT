package mainRoom

import accounts.constantMainRoomInit
import mainContext.MainContext
import constants.MainRoomConstant
import constants.SlaveRoomConstant
import slaveRoom.SlaveRoom
import constants.CacheCarrier
import mainContext.getCacheRecordRoom
import mainContext.messenger
import mainRoomCollector.MainRoomCollector
import screeps.api.*
import screeps.api.structures.*
import screeps.utils.toMap
import kotlin.random.Random

class MainRoom(val parent: MainRoomCollector, val name: String, val describe: String, val constant: MainRoomConstant) {
    val room: Room = Game.rooms[this.name] ?: throw AssertionError("Not room $this.name")
    val slaveRooms: MutableMap<String, SlaveRoom> = mutableMapOf()

    val need = Array(3) { Array(100) { 0 } }
    val have = Array(100) { 0 }
    private val haveForQueue = Array(100) { 0 }
    val queue = mutableListOf<QueueSpawnRecord>()

    //Cash data
    val resStorage: MutableMap<ResourceConstant, Int> = mutableMapOf()  //Stored in Store + Logist
    private val resTerminal: MutableMap<ResourceConstant, Int> = mutableMapOf()

    //StructureSpawn
    private var _structureSpawn: Map<String, StructureSpawn>? = null
    val structureSpawn: Map<String, StructureSpawn>
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
                _structureController = this.room.find(FIND_STRUCTURES).filter { it.structureType == STRUCTURE_CONTROLLER }.withIndex().associate { it.index to it.value as StructureController }
            return _structureController ?: throw AssertionError("Error get StructureController")
        }

    //Source
    private var _source: Map<Int, Source>? = null
    val source: Map<Int, Source>
        get() {
            if (this._source == null)
                _source = this.room.find(FIND_SOURCES).sortedWith(Comparator<Source> { a, b ->
                    when {
                        a.id > b.id -> 1
                        a.id < b.id -> -1
                        else -> 0
                    }
                }).withIndex().associate { it.index to it.value }
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
                _structureTower = this.room.find(FIND_STRUCTURES).filter { it.structureType == STRUCTURE_TOWER }.associate { it.id to it as StructureTower }
            return _structureTower ?: throw AssertionError("Error get StructureTower")
        }

    //StructureContainer
    private var _structureContainer: Map<String, StructureContainer>? = null
    val structureContainer: Map<String, StructureContainer>
        get() {
            if (this._structureContainer == null)
                _structureContainer = this.room.find(FIND_STRUCTURES).filter { it.structureType == STRUCTURE_CONTAINER }.associate { it.id to it as StructureContainer }
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
            return _structureContainerNearSource
                    ?: throw AssertionError("Error get StructureContainerNearSource")
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
            return _structureContainerNearController
                    ?: throw AssertionError("Error get StructureContainerNearController")
        }

    //StructureStorage
    private var _structureStorage: Map<Int, StructureStorage>? = null
    val structureStorage: Map<Int, StructureStorage>
        get() {
            if (this._structureStorage == null)
                _structureStorage = this.room.find(FIND_STRUCTURES).filter { it.structureType == STRUCTURE_STORAGE }.withIndex().associate { it.index to it.value as StructureStorage }
            return _structureStorage ?: throw AssertionError("Error get StructureStorage")
        }

    //StructureLink
    private var _structureLink: Map<String, StructureLink>? = null
    private val structureLink: Map<String, StructureLink>
        get() {
            if (this._structureLink == null)
                _structureLink = this.room.find(FIND_STRUCTURES).filter { it.structureType == STRUCTURE_LINK }.associate { it.id to it as StructureLink }
            return _structureLink ?: throw AssertionError("Error get StructureLink")
        }

    //StructureLinkNearSource
    private var _structureLinkNearSource: Map<Int, StructureLink>? = null
    val structureLinkNearSource: Map<Int, StructureLink>
        get() {
            if (this._structureLinkNearSource == null) {
                val resultLink = mutableMapOf<Int, StructureLink>()
                for (sourceRec in this.source)
                    for (link in this.structureLink.values)
                        if (!resultLink.containsValue(link) && sourceRec.value.pos.inRangeTo(link.pos, 2))
                            resultLink[sourceRec.key] = link
                _structureLinkNearSource = resultLink
            }
            return _structureLinkNearSource
                    ?: throw AssertionError("Error get StructureLinkNearSource")
        }

    //StructureLinkNearController
    private var _structureLinkNearController: Map<Int, StructureLink>? = null
    val structureLinkNearController: Map<Int, StructureLink>
        get() {
            if (this._structureLinkNearController == null) {
                val resultLink = mutableMapOf<Int, StructureLink>()
                for (link in this.structureLink.values) {
                    val protectStructureController: StructureController? = this.structureController[0]
                    if (protectStructureController != null && !this.structureLinkNearSource.containsValue(link) && protectStructureController.pos.inRangeTo(link.pos, 3))
                        resultLink[0] = link
                }
                _structureLinkNearController = resultLink
            }
            return _structureLinkNearController
                    ?: throw AssertionError("Error get StructureLinkNearController")
        }

    //StructureLinkNearStorage
    private var _structureLinkNearStorage: Map<Int, StructureLink>? = null
    val structureLinkNearStorage: Map<Int, StructureLink>
        get() {
            if (this._structureLinkNearStorage == null) {
                val resultLink = mutableMapOf<Int, StructureLink>()
                for (link in this.structureLink.values) {
                    val protectStructureStorage: StructureStorage? = this.structureStorage[0]
                    if (protectStructureStorage != null
                            && !this.structureLinkNearSource.containsValue(link)
                            && !this.structureLinkNearController.containsValue(link)
                            && protectStructureStorage.pos.inRangeTo(link.pos, 3))
                        resultLink[0] = link
                }
                _structureLinkNearStorage = resultLink
            }
            return _structureLinkNearStorage
                    ?: throw AssertionError("Error get StructureLinkNearStorage")
        }

    //StructureTerminal
    private var _structureTerminal: Map<Int, StructureTerminal>? = null
    val structureTerminal: Map<Int, StructureTerminal>
        get() {
            if (this._structureTerminal == null)
                _structureTerminal = this.room.find(FIND_STRUCTURES).filter { it.structureType == STRUCTURE_TERMINAL }.withIndex().associate { it.index to it.value as StructureTerminal }
            return _structureTerminal ?: throw AssertionError("Error get StructureTerminal")
        }

    //Mineral
    private var _mineral: Mineral? = null
    val mineral: Mineral
        get() {
            if (this._mineral == null)
                _mineral = this.room.find(FIND_MINERALS).first()
            return _mineral ?: throw AssertionError("Error get Mineral")
        }

    //StructureExtractor
    private var _structureExtractor: Map<Int, StructureExtractor>? = null
    val structureExtractor: Map<Int, StructureExtractor>
        get() {
            if (this._structureExtractor == null)
                _structureExtractor = this.room.find(FIND_STRUCTURES).filter { it.structureType == STRUCTURE_EXTRACTOR }.withIndex().associate { it.index to it.value as StructureExtractor }
            return _structureExtractor ?: throw AssertionError("Error get StructureExtractor")
        }

    //StructureContainerNearMineral
    private var _structureContainerNearMineral: Map<Int, StructureContainer>? = null
    val structureContainerNearMineral: Map<Int, StructureContainer>
        get() {
            if (this._structureContainerNearMineral == null) {
                val resultContainer = mutableMapOf<Int, StructureContainer>()
                for (container in this.structureContainer.values) {
                    if (!this.structureContainerNearSource.containsValue(container) && this.mineral.pos.inRangeTo(container.pos, 1))
                        resultContainer[0] = container
                }
                _structureContainerNearMineral = resultContainer
            }
            return _structureContainerNearMineral
                    ?: throw AssertionError("Error get StructureContainerNearMineral")
        }

    //StructureLabs
    private var _structureLab: Map<String, StructureLab>? = null
    val structureLab: Map<String, StructureLab>
        get() {
            if (this._structureLab == null)
                _structureLab = this.room.find(FIND_STRUCTURES).filter { it.structureType == STRUCTURE_LAB }.associate { it.id to it as StructureLab }
            return _structureLab ?: throw AssertionError("Error get StructureLab")
        }

    //StructureLabsSort
    private var _structureLabSort: Map<Int, StructureLab>? = null
    val structureLabSort: Map<Int, StructureLab>
        get() {
            if (this._structureLabSort == null) {
                val result: MutableMap<Int, StructureLab> = mutableMapOf()

                val minX: Int = this.structureLab.values.minBy { it.pos.x }?.pos?.x
                        ?: return result
                val minY: Int = this.structureLab.values.minBy { it.pos.y }?.pos?.y
                        ?: return result
                if (this.structureLab.size == 3) {
                    val arrDx = arrayOf(0, 1, 1)
                    val arrDy = arrayOf(2, 1, 0)
                    for (ind in 0..2) {
                        val tmpLab: StructureLab? = this.structureLab.values.firstOrNull {
                            it.pos.x == (minX + arrDx[ind]) && it.pos.y == (minY + arrDy[ind])
                        }
                        if (tmpLab != null) result[ind] = tmpLab
                    }
                }
                if (this.structureLab.size == 6) {
                    val arrDx = arrayOf(0, 1, 1, 2, 2, 2)
                    val arrDy = arrayOf(2, 1, 0, 0, 1, 2)
                    for (ind in 0..5) {
                        val tmpLab: StructureLab? = this.structureLab.values.firstOrNull {
                            it.pos.x == (minX + arrDx[ind]) && it.pos.y == (minY + arrDy[ind])
                        }
                        if (tmpLab != null) result[ind] = tmpLab
                    }
                }

                if (this.structureLab.size == 10) {
                    val arrDx = arrayOf(1, 2, 2, 3, 3, 3, 0, 0, 0, 1)
                    val arrDy = arrayOf(2, 1, 0, 0, 1, 2, 1, 2, 3, 3)
                    for (ind in 0..9) {
                        val tmpLab: StructureLab? = this.structureLab.values.firstOrNull {
                            it.pos.x == (minX + arrDx[ind]) && it.pos.y == (minY + arrDy[ind])
                        }
                        if (tmpLab != null) result[ind] = tmpLab
                    }
                }

                this._structureLabSort = result.toMap()
            }
            return _structureLabSort ?: throw AssertionError("Error get StructureLabSort")
        }

    private fun buildCreeps() {
        this.needCorrection()
        for (slaveRoomRecord in this.slaveRooms) slaveRoomRecord.value.needCorrection()
        this.buildQueue()

        this.spawnCreep()
    }

    private fun needCorrection() {
        val nowLevelOfRoom: Int = this.getLevelOfRoom()

        if (nowLevelOfRoom < this.constant.levelOfRoom) {
            if (this.getResourceInStorage() > 40000) {
                if (this.need[0][5] == 0) this.need[0][5] = 1 //filler
                if (this.need[1][5] == 0) this.need[1][5] = 1 //filler
                this.need[0][8] = 2
                this.restoreSnapShot()
                return
            } else {
                this.restoreSnapShot()
                this.constant.levelOfRoom = nowLevelOfRoom
            }
        } else this.constant.levelOfRoom = nowLevelOfRoom

        when (this.constant.levelOfRoom) {
            0 -> this.needCorrection0()
            1 -> this.needCorrection1()
            2 -> this.needCorrection2()
        }
    }

    fun getResourceInStorage(resource: ResourceConstant = RESOURCE_ENERGY): Int {
        return this.resStorage[resource] ?: 0
    }

    fun getResourceInTerminal(resource: ResourceConstant = RESOURCE_ENERGY): Int {
        return this.resTerminal[resource] ?: 0
    }

    fun getResource(resource: ResourceConstant = RESOURCE_ENERGY): Int {
        return (this.resTerminal[resource] ?: 0) + (this.resStorage[resource] ?: 0)
    }

    private fun getTimeDeath(fRole: Int): Int {
        return when (fRole) {
            2 -> parent.parent.getCacheRecordRoom("mainContainer0", this)?.timeForDeath ?: 0
            4 -> parent.parent.getCacheRecordRoom("mainContainer1", this)?.timeForDeath ?: 0
            else -> 0
        }
    }


    private fun buildQueue() {
        for (i in 0 until this.have.size) this.haveForQueue[i] = this.have[i]
        val fPriorityOfRole = if (this.getResourceInStorage() < 2000) arrayOf(0, 9, 1, 3, 2, 4, 14, 5, 6, 20, 21, 22, 7, 10, 8, 11, 12, 13, 15, 16, 17, 18)
        else arrayOf(0, 9, 5, 14, 1, 3, 2, 4, 20, 21, 22, 6, 7, 10, 8, 11, 12, 13, 15, 16, 17, 18)

        //Main 0..1
        for (priority in 0..1) {
            for (fRole in fPriorityOfRole) {
                var fNeed = this.need[0][fRole]
                if (priority >= 1) fNeed += this.need[1][fRole]
                if (priority >= 2) fNeed += this.need[2][fRole]
                while (this.haveForQueue[fRole] < fNeed) {
                    this.haveForQueue[fRole]++
                    this.queue.add(QueueSpawnRecord(fRole, this.name, this.name, this.getTimeDeath(fRole)))
                }
            }
        }

        //Slave 0
        for (slaveRoom in this.slaveRooms.values) {
            for (i in 0 until slaveRoom.have.size) slaveRoom.haveForQueue[i] = slaveRoom.have[i]
            slaveRoom.buildQueue(this.queue, 0)
        }

        //Slave 1
        for (slaveRoom in this.slaveRooms.values) {
            slaveRoom.buildQueue(this.queue, 1)
        }

        //Main 2
        for (priority in 2..2) {
            for (fRole in fPriorityOfRole) {
                var fNeed = this.need[0][fRole]
                if (priority >= 1) fNeed += this.need[1][fRole]
                if (priority >= 2) fNeed += this.need[2][fRole]
                while (this.haveForQueue[fRole] < fNeed) {
                    this.haveForQueue[fRole]++
                    this.queue.add(QueueSpawnRecord(fRole, this.name, this.name, this.getTimeDeath(fRole)))
                }
            }
        }

        //Slave 2
        for (slaveRoom in slaveRooms.values)
            slaveRoom.buildQueue(this.queue, 2)
    }

    private fun getBodyRole(role: Int): Array<BodyPartConstant> {
        var result: Array<BodyPartConstant> = arrayOf()

        when (role) {
            0 -> {
                if (this.have[0] == 0 && this.room.energyAvailable < 800) result = arrayOf(MOVE, MOVE, WORK, CARRY)
                else if (this.room.energyCapacityAvailable < 400) result = arrayOf(MOVE, MOVE, WORK, CARRY)
                else if (this.room.energyCapacityAvailable < 800) result = arrayOf(MOVE, MOVE, WORK, WORK, CARRY, CARRY)
                else result = arrayOf(MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, WORK, WORK, CARRY, CARRY, CARRY, CARRY)
            }

            1, 3 -> {
                result = arrayOf(MOVE, MOVE, WORK, WORK, WORK, WORK, WORK, WORK, CARRY, CARRY)
            }

            2 -> {
                val carrierAuto: CacheCarrier? = parent.parent.getCacheRecordRoom("mainContainer0", this)
                if (carrierAuto == null) {
                    parent.parent.messenger("ERROR", this.name, "Auto not exists mainContainer0", COLOR_RED)
                    result = arrayOf(MOVE, MOVE, MOVE, MOVE, MOVE, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY)
                } else {
                    result = carrierAuto.needBody
                }
            }

            4 -> {
                val carrierAuto: CacheCarrier? = parent.parent.getCacheRecordRoom("mainContainer1", this)
                if (carrierAuto == null) {
                    parent.parent.messenger("ERROR", this.name, "Auto not exists mainContainer1", COLOR_RED)
                    result = arrayOf(MOVE, MOVE, MOVE, MOVE, MOVE, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY)
                } else {
                    result = carrierAuto.needBody
                }
            }

            5 -> {
                if (this.room.energyCapacityAvailable >= 5000) result = arrayOf(MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY)
                else if (this.room.energyCapacityAvailable >= 1300) result = arrayOf(MOVE, MOVE, MOVE, MOVE, MOVE, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY)
                else result = arrayOf(MOVE, MOVE, MOVE, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY)
            }

            6 -> {
                if (this.room.energyCapacityAvailable < 1800) result = arrayOf(MOVE, MOVE, MOVE, MOVE, MOVE, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY)
                else if (this.room.energyCapacityAvailable <= 5600) result = arrayOf(CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE)
                else result = arrayOf(MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY)
            }

            7 -> {
                //ToDO remove after 3 level room
                val controller = this.structureController[0]
                if (controller != null && controller.level == 8) result = arrayOf(MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, WORK, WORK, WORK, WORK, WORK, WORK, WORK, WORK, WORK, WORK, WORK, WORK, WORK, WORK, WORK, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY)
                else if (this.room.energyCapacityAvailable < 1800) result = arrayOf(MOVE, MOVE, MOVE, MOVE, WORK, WORK, WORK, WORK, WORK, WORK, WORK, WORK, CARRY, CARRY)
                else if (this.room.energyCapacityAvailable < 2300) result = arrayOf(MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, WORK, WORK, WORK, WORK, WORK, WORK, WORK, WORK, WORK, WORK, WORK, WORK, CARRY, CARRY, CARRY)
                else if (this.room.energyCapacityAvailable < 3000) result = arrayOf(MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, WORK, WORK, WORK, WORK, WORK, WORK, WORK, WORK, WORK, WORK, WORK, WORK, WORK, WORK, WORK, CARRY, CARRY, CARRY)
                else if (this.room.energyCapacityAvailable <= 5600) result = arrayOf(MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, WORK, WORK, WORK, WORK, WORK, WORK, WORK, WORK, WORK, WORK, WORK, WORK, WORK, WORK, WORK, WORK, WORK, WORK, WORK, WORK, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY)
                else result = arrayOf(MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, WORK, WORK, WORK, WORK, WORK, WORK, WORK, WORK, WORK, WORK, WORK, WORK, WORK, WORK, WORK, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY)
            }

            8 -> {
                result = arrayOf(MOVE, MOVE, MOVE, MOVE, MOVE, WORK, WORK, WORK, WORK, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY)
            }

            9 -> {
                result = arrayOf(CARRY, CARRY, MOVE)
            }

            10 -> {
                if (this.room.energyCapacityAvailable >= 3500) result = arrayOf(MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, WORK, WORK, WORK, WORK, WORK, WORK, WORK, WORK, WORK, WORK, WORK, WORK, WORK, WORK, WORK, WORK, WORK, WORK, WORK, WORK, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY)
                else result = arrayOf(MOVE, MOVE, MOVE, MOVE, MOVE, WORK, WORK, WORK, WORK, WORK, WORK, WORK, WORK, WORK, WORK, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY)
            }

            11 -> {
                result = arrayOf(MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY)
            }

            13 -> {
                result = arrayOf(WORK, CARRY, MOVE)
            }

            14 -> {
                result = arrayOf(MOVE, MOVE, MOVE, MOVE, MOVE, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY)
            }

            15 -> {
                if (this.room.energyCapacityAvailable >= 2300) result = arrayOf(MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, WORK, WORK, WORK, WORK, WORK, WORK, WORK, WORK, WORK, WORK, WORK, WORK, WORK, WORK, WORK, WORK, WORK, WORK, WORK, WORK)
            }

            16 -> {
                result = arrayOf(MOVE, MOVE, MOVE, MOVE, MOVE, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY)
            }

            17 -> {
                result = arrayOf(MOVE, CARRY)
            }

            18 -> {
                result = arrayOf(MOVE, MOVE, MOVE, MOVE, MOVE, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY, CARRY)
            }

            20 -> {
                //Min
                //result = arrayOf(MOVE)
                //Middle
                //result = arrayOf(MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,ATTACK,ATTACK,ATTACK,ATTACK,ATTACK,ATTACK,ATTACK,ATTACK,ATTACK,ATTACK,ATTACK,ATTACK,ATTACK,ATTACK,ATTACK)
                //Max
                result = arrayOf(MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, ATTACK, ATTACK, ATTACK, ATTACK, ATTACK, ATTACK, ATTACK, ATTACK, ATTACK, ATTACK, ATTACK, ATTACK, ATTACK, ATTACK, ATTACK, ATTACK, ATTACK, ATTACK, ATTACK, ATTACK, ATTACK, ATTACK, ATTACK, ATTACK, ATTACK)
            }

            21 -> { //RA
                //Min
                //result = arrayOf(MOVE, MOVE)
                //Middle
                //result = arrayOf(MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,RANGED_ATTACK,RANGED_ATTACK,RANGED_ATTACK,RANGED_ATTACK,RANGED_ATTACK,RANGED_ATTACK,RANGED_ATTACK,RANGED_ATTACK,RANGED_ATTACK,RANGED_ATTACK,RANGED_ATTACK,RANGED_ATTACK,RANGED_ATTACK,RANGED_ATTACK,RANGED_ATTACK)
                //Max
                result = arrayOf(MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, RANGED_ATTACK, RANGED_ATTACK, RANGED_ATTACK, RANGED_ATTACK, RANGED_ATTACK, RANGED_ATTACK, RANGED_ATTACK, RANGED_ATTACK, RANGED_ATTACK, RANGED_ATTACK, RANGED_ATTACK, RANGED_ATTACK, RANGED_ATTACK, RANGED_ATTACK, RANGED_ATTACK, RANGED_ATTACK, RANGED_ATTACK, RANGED_ATTACK, RANGED_ATTACK, RANGED_ATTACK, RANGED_ATTACK, RANGED_ATTACK, RANGED_ATTACK, RANGED_ATTACK, RANGED_ATTACK)
            }

            22 -> {
                //Min
                //result = arrayOf(MOVE, MOVE, MOVE)
                //Middle
                //result = arrayOf(TOUGH,TOUGH,TOUGH,TOUGH,TOUGH,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,MOVE,HEAL,HEAL,HEAL,HEAL,HEAL,HEAL,HEAL,HEAL,HEAL,HEAL)
                //Max
                result = arrayOf(TOUGH, TOUGH, TOUGH, TOUGH, TOUGH, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, MOVE, HEAL, HEAL, HEAL, HEAL, HEAL, HEAL, HEAL, HEAL, HEAL, HEAL, HEAL, HEAL, HEAL, HEAL, HEAL)
            }
        }
        return result
    }

    private fun spawnCreep() {
        if (!this.constant.creepSpawn) return
        for (spawn in this.structureSpawn.values) {
            if (this.queue.size == 0) return
            if (!spawn.isActive() || spawn.spawning != null) continue
            var result: ScreepsReturnCode = OK

            val d: dynamic = object {}
            d["role"] = this.queue[0].role
            d["slaveRoom"] = this.queue[0].slaveRoom
            d["mainRoom"] = this.queue[0].mainRoom
            d["tickDeath"] = this.queue[0].timeDeath
            val spawnOptions: dynamic = object {}
            spawnOptions["memory"] = d

            if (this.queue[0].slaveRoom == this.queue[0].mainRoom)
                result = spawn.spawnCreep(getBodyRole(this.queue[0].role), "mst_${this.queue[0].mainRoom}_${this.queue[0].slaveRoom}_${Game.time} ", spawnOptions.unsafeCast<SpawnOptions>())
            else {
                val slaveRoom = this.slaveRooms[this.queue[0].slaveRoom]
                if (slaveRoom != null)
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

    fun getSpawnOrExtensionForFiling(pos: RoomPosition, mainContext: MainContext): Structure? {
        data class StructureData(val need: Int, val priority: Int)

        val needs: MutableMap<Structure, StructureData> = mutableMapOf()

        // Загружаем все extension
        for (extension in this.structureExtension.values)
            if (extension.energyCapacity > extension.energy)
                needs[extension] = StructureData(extension.energyCapacity - extension.energy, 1)

        // Загружаем все спавны
        for (spawn in this.structureSpawn.values)
            if (spawn.energyCapacity > spawn.energy)
                needs[spawn] = StructureData(spawn.energyCapacity - spawn.energy, 1)

        // Загружаем Tower если енергия меньше 1000
        //ToDo set priority 0 if have hostile creeps and queue < 2
        for (tower in this.structureTower.values)
            if (tower.energy < 400) needs[tower] = StructureData(tower.energyCapacity - tower.energy, 3)

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

    fun getSourceForHarvest(pos: RoomPosition, mainContext: MainContext): Source {
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
    fun getLevelOfRoom(): Int {
        if (this.room.energyCapacityAvailable >= 1800
                && this.structureLinkNearSource.size == this.source.size
                && this.structureContainerNearController.size == 1
                && this.structureStorage.size == 1
                && this.structureTerminal.size == 1
                && this.structureLinkNearStorage.size == 1
        ) return 2

        if (this.room.energyCapacityAvailable >= 1300 &&
                this.structureContainerNearSource.size == this.source.size &&
                this.structureContainerNearController.size == 1 &&
                this.structureStorage.size == 1)
            return 1

        return 0
    }

    fun getTicksToDowngrade(): Int {
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

    private fun setNextTickRun(): Boolean {
        if (this.constant.roomRunNotEveryTickNextTickRun > Game.time) return false
        this.constant.roomRunNotEveryTickNextTickRun = Game.time + Random.nextInt(parent.parent.constants.globalConstant.roomRunNotEveryTickTicksPauseMin,
                parent.parent.constants.globalConstant.roomRunNotEveryTickTicksPauseMax)
        parent.parent.messenger("TEST", this.name, "Main room not every tick run. Next tick: ${this.constant.roomRunNotEveryTickNextTickRun}", COLOR_GREEN)
        return true
    }

    fun runInStartOfTick() {
        this.manualDefenceInStartOfTick()
        this.fillCash()
        for (room in this.slaveRooms.values) {
            try {
                room.runInStartOfTick()
            } catch (e: Exception) {
                parent.parent.messenger("ERROR", "Slave room in start", room.name, COLOR_RED)
            }
        }
        this.setMineralNeed()
    }

    fun runInEndOfTick() {
        if (this.constant.levelOfRoom > 1) this.runLinkTransfers()
        this.runTower()
        this.buildCreeps()
        this.directControl()

        for (room in this.slaveRooms.values) {
            try {
                room.runInEndOfTick()
            } catch (e: Exception) {
                parent.parent.messenger("ERROR", "Slave room in end", room.name, COLOR_RED)
            }
        }

        val cpuStartLab = Game.cpu.getUsed()
        this.runReactions()
        Memory["CPULab"] = Memory["CPULab"] + Game.cpu.getUsed() - cpuStartLab
    }

    fun runNotEveryTick() {
        for (record in this.slaveRooms) {
            try {
                record.value.runNotEveryTick()
            } catch (e: Exception) {
                parent.parent.messenger("ERROR", "Slave not every tick", record.value.name, COLOR_RED)
            }
        }


        if (!this.setNextTickRun()) return
        this.restoreSnapShot()
        this.building()
        this.marketCreateBuyOrders()
        this.marketCreateBuyOrdersMineral()
        this.needCleanerCalculate()
        this.needDefenceUpgradeCalculate()
    }

    fun missingStructures(): String {
        if (this.constructionSite.isNotEmpty()) return ""
        val controller: StructureController = this.structureController[0] ?: return ""


        if (controller.level == 2) {
            if (this.room.energyCapacityAvailable != 550) return " Missing extension"
        }

        if (controller.level == 3) {
            if (this.room.energyCapacityAvailable != 800) return " Missing extension"
            if (this.structureTower.size != 1) return " Missing tower"
        }

        if (controller.level == 4) {
            if (this.room.energyCapacityAvailable != 1300) return " Missing extension"
            if (this.structureTower.size != 1) return " Missing tower"
            if (this.source.isNotEmpty() && !this.structureContainerNearSource.containsKey(0)) return " Missing container near source 0"
            if (this.source.size > 1 && !this.structureContainerNearSource.containsKey(1)) return " Missing container near source 1"
            if (!this.structureContainerNearController.containsKey(0)) return " Missing container near controller"
            if (!this.structureStorage.containsKey(0)) return " Missing storage"
        }

        if (controller.level == 5) {
            if (this.room.energyCapacityAvailable != 1800) return " Missing extension"
            if (this.structureTower.size != 2) return " Missing tower"
            if (this.source.isNotEmpty() && !this.structureContainerNearSource.containsKey(0)) return " Missing container near source 0"
            if (this.source.size > 1 && !this.structureContainerNearSource.containsKey(1)) return " Missing container near source 1"
            if (!this.structureContainerNearController.containsKey(0)) return " Missing container near controller"
            if (!this.structureStorage.containsKey(0)) return " Missing storage"
        }

        if (controller.level == 6) {
            if (this.room.energyCapacityAvailable != 2300) return " Missing extension"
            if (this.structureTower.size != 2) return " Missing tower"
            if (this.source.isNotEmpty() && !this.structureLinkNearSource.containsKey(0)) return " Missing link near source 0"
            if (this.source.size > 1 && !this.structureLinkNearSource.containsKey(1)) return " Missing link near source 1"
            if (!this.structureContainerNearController.containsKey(0)) return " Missing container near controller"
            if (!this.structureStorage.containsKey(0)) return " Missing storage"
            if (this.structureTerminal.size != 1) return " Missing terminal"
            if (this.structureLinkNearStorage.size != 1) return " Missing link near storage"
            if (this.structureContainerNearSource.containsKey(0)) return " Not need container near source 0"
            if (this.structureContainerNearSource.containsKey(1)) return " Not need container near source 1"
            if (this.structureExtractor.size != 1) return " Missing extractor"
            if (this.structureContainerNearMineral.size != 1) return " Missing container near mineral"
            if (this.structureLab.size < 3) return " Missing lab"
        }

        if (controller.level == 7) {
            if (this.structureSpawn.size != 2) return " Missing spawn"
            if (this.room.energyCapacityAvailable != 5600) return " Missing extension"
            if (this.structureTower.size != 3) return " Missing tower"
            if (this.source.isNotEmpty() && !this.structureLinkNearSource.containsKey(0)) return " Missing link near source 0"
            if (this.source.size > 1 && !this.structureLinkNearSource.containsKey(1)) return " Missing link near source 1"
            if (!this.structureContainerNearController.containsKey(0)) return " Missing container near controller"
            if (!this.structureStorage.containsKey(0)) return " Missing storage"
            if (this.structureTerminal.size != 1) return " Missing terminal"
            if (this.structureLinkNearStorage.size != 1) return " Missing link near storage"
            if (this.structureContainerNearSource.containsKey(0)) return " Not need container near source 0"
            if (this.structureContainerNearSource.containsKey(1)) return " Not need container near source 1"
            if (this.structureExtractor.size != 1) return " Missing extractor"
            if (this.structureContainerNearMineral.size != 1) return " Missing container near mineral"
            if (this.structureLab.size < 6) return " Missing lab"
        }

        if (controller.level == 8) {
            if (this.room.energyCapacityAvailable != 10850) return " Missing extension"
        }
        return ""
    }

    private fun runLinkTransfers() {
        val fLinkTo: StructureLink = this.structureLinkNearStorage[0] ?: return
        if (fLinkTo.energy != 0) return

        for (link in this.structureLinkNearSource.values)
            if (link.energy >= 700 && link.cooldown == 0) {
                link.transferEnergy(fLinkTo, link.energy)
                break
            }
    }

    private fun fillCash() {
        val store: StructureStorage? = this.structureStorage[0]
        if (store != null)
            for (record in store.store.toMap()) this.resStorage[record.key] = (this.resStorage[record.key]
                    ?: 0) + record.value

        val terminal: StructureTerminal? = this.structureTerminal[0]
        if (terminal != null)
            for (record in terminal.store.toMap()) this.resTerminal[record.key] = (this.resTerminal[record.key]
                    ?: 0) + record.value
    }

    fun needCleanWhat(store: StoreDefinition?, resource: ResourceConstant): ResourceConstant? {
        if (store == null) return null
        for (record in store.keys)
            if (store[record] != null && store[record] != 0 && record != resource) return record
        return null
    }

    private fun needClean(store: StoreDefinition?, resource: ResourceConstant): Boolean {
        if (store == null) return false
        return (store[resource] ?: 0) != (store.toMap().map { it.value }.sum())
    }

    fun needCleanerCalculate() {
        var result = false
        if (!result) result = needClean(this.structureContainerNearSource[0]?.store, RESOURCE_ENERGY)
        if (!result) result = needClean(this.structureContainerNearSource[1]?.store, RESOURCE_ENERGY)
        if (!result) result = needClean(this.structureContainerNearSource[2]?.store, RESOURCE_ENERGY)
        if (!result) result = needClean(this.structureContainerNearController[0]?.store, RESOURCE_ENERGY)
        if (!result) result = needClean(this.structureContainerNearMineral[0]?.store, this.mineral.mineralType)
        this.constant.needCleaner = result
    }

    private fun needDefenceUpgradeCalculate() {
        val structure: Structure? = this.room.find(FIND_STRUCTURES).filter {
            (it.structureType == STRUCTURE_RAMPART || it.structureType == STRUCTURE_WALL)
                    && it.hits < this.constant.defenceHits
                    && it.pos.x != 49 && it.pos.x != 0 && it.pos.y != 49 && it.pos.y != 0
        }.firstOrNull()
        this.constant.defenceNeedUpgrade = (structure != null)
    }

    private fun runReactions() {
        if (this.constant.reactionActive == "") return
        if (this.structureLabSort.size !in arrayOf(3, 6, 10)) return
        val lab0 = this.structureLabSort[0] ?: return
        val lab1 = this.structureLabSort[1] ?: return
        val reaction = this.constant.reactionActive.unsafeCast<ResourceConstant>()
        val reactionComponent = this.parent.parent.constants.globalConstant.labReactionComponent[reaction]
                ?: return
        if (reactionComponent.size != 2) return
        if (lab0.mineralAmount != 0 && lab0.mineralType != reactionComponent[0]) return
        if (lab1.mineralAmount != 0 && lab1.mineralType != reactionComponent[1]) return
        for (ind in 2 until this.structureLabSort.size) {
            val lab = this.structureLabSort[ind] ?: continue
            if (lab.cooldown != 0) continue
            lab.runReaction(lab0, lab1)
        }
    }

    private fun setMineralNeed() {
        if (this.constant.reactionActive == "") return
        val reaction = this.constant.reactionActive.unsafeCast<ResourceConstant>()
        if (this.structureLabSort.size !in arrayOf(3, 6, 10)) return
        val reactionComponent = this.parent.parent.constants.globalConstant.labReactionComponent[reaction]
                ?: return
        this.constant.needMineral[reactionComponent[0]] = 2000
        this.constant.needMineral[reactionComponent[1]] = 2000
    }
}


