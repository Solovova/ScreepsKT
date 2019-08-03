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

    //StructureContainer //ToDo test
    private var _structureContainer: Map<String, StructureContainer>? = null
    private val structureContainer: Map<String, StructureContainer>
        get() {
            if (this._structureContainer == null)
                _structureContainer = this.room.find(FIND_STRUCTURES).filter { it.structureType == STRUCTURE_CONTAINER }.associate { it.id to it as StructureContainer}
            return _structureContainer ?: throw AssertionError("Error get StructureContainer")
        }

    //StructureContainerNearSource //ToDo test
    private var _structureContainerNearSource: Map<Int, StructureContainer>? = null //id source
    private val structureContainerNearSource: Map<Int, StructureContainer>
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

    //StructureContainerNearController  //ToDo test
    private var _structureContainerNearController: Map<String, StructureContainer>? = null //id source
    private val structureContainerNearController: Map<String, StructureContainer>
        get() {
            if (this._structureContainerNearController == null) {
                val resultContainer = mutableMapOf<String, StructureContainer>()
                for (container in this.structureContainer.values) {
                    val protectStructureController: StructureController? = this.structureController[0]
                    if (protectStructureController != null && !this.structureContainerNearSource.containsValue(container) && protectStructureController.pos.inRangeTo(container.pos, 3))
                        resultContainer[protectStructureController.id] = container
                }
                _structureContainerNearController = resultContainer
            }
            return _structureContainerNearController ?: throw AssertionError("Error get StructureContainerNearController")
        }

    //StructureStorage //ToDo test
    private var _structureStorage: Map<Int, StructureStorage>? = null
    private val structureStorage: Map<Int, StructureStorage>
        get() {
            if (this._structureStorage == null)
                _structureStorage = this.room.find(FIND_HOSTILE_STRUCTURES).filter { it.structureType == STRUCTURE_STORAGE }.withIndex().associate { it.index to it.value as StructureStorage}
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
                // 0 - room 1 level
                // 1 - harvester source 0
                // 2 - transport source 0
                // 3 - harvester source 1
                // 4 - transport source 1
                // 5 - filler
                // 9 - small filler

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
                if (this.need[1][5] ==0) this.need[1][5]=1 //filler


//                    //1.3 если filler = 0 и Енергии если выпускаем смалл филлер
//                    if ((this.have[5]==0)&&(this.structureStorage.isNotEmpty() >2000))  objRoom.Need0[9]=1;
//                    if ((objRoom.CalcRoleCreeps[5]==0)&&(fEnergyInStorage<=2000)) objRoom.Need0[0]=2;

//
//                    //2 Builder
//                    //2.1
//                    objRoom.Need1[8]=0;                             //208 - Builder
//                    //2.2 если есть что строить то выпускаем строителя
//                    // if ((objRoom.IsBuild == 1)&&(fEnergyInStorage>20000)&&(objRoom.BoostBuilder != 1)) {
//                    //     objRoom.Need1[8]=2;
//                    // }
//
//                    //3 Mineral harvesting
//                    if ((objRoom.Extractor !='')&&(objRoom.Containers[4] != null)){
//                        fMineral = Game.getObjectById(objRoom.Mineral);
//                        fMineralsInStorage = _.sum(fStorage.store) - fStorage.store.energy;
//                        fHarvestMineralsInStorage = fStorage.store[fMineral.mineralType];
//                        if (fHarvestMineralsInStorage == null) fHarvestMineralsInStorage = 0;
//                        if (fMineralsInStorage<objRoom.StorageMineralAllMax && fHarvestMineralsInStorage<objRoom.StorageMineralExtractionMax && fMineral.mineralAmount!=0){
//                            objRoom.Need1[15]=1;
//                            objRoom.Need1[16]=1;
//                        }
//                    }
//
//                    //4 Если есть Link(2) то запускаем переносчика
//                    if (objRoom.Links[2]!='') objRoom.Need1[14]=1;
//
//                    //5 LabFiller
//                    if (objRoom.LabReaction != '')  objRoom.Need1[18]=1;
//
//
//
//                    //6 Upgrader
//                    if (objRoom.SentEnergyToRoom == '') {
//                        if (objRoom.MaxSpawnEnergy>=1800) {
//                            objRoom.Need1[6]=1;
//                            objRoom.Need1[7]=1;
//                            objRoom.Need2[6]=2;
//                            objRoom.Need2[7]=3;
//                            if (objRoom.Name == 'W47N3') objRoom.Need2[6]=4;
//                        }else{
//                            objRoom.Need1[6]=2;
//                            objRoom.Need1[7]=2;
//                            objRoom.Need2[6]=1;
//                            objRoom.Need2[7]=2;
//
//                        }
//                    }else{
//                        objRoom.Need1[6]=0;
//                        objRoom.Need1[7]=0;
//                        objRoom.Need2[6]=0;
//                        objRoom.Need2[7]=0;
//                        if (objRoom.oController < 20000) objRoom.Need1[13]=1;
//                    }
//
//                    if (fEnergyInStorage<30000) {
//                        objRoom.Need1[6]=0;
//                        objRoom.Need1[7]=0;
//                        objRoom.Need2[6]=0;
//                        objRoom.Need2[7]=0;
//                    }
//
//
//                    if (objRoom.oController.level == 8){
//                        objRoom.Need1[6]=0;
//                        objRoom.Need1[7]=1;
//                        objRoom.Need2[6]=0;
//                        objRoom.Need2[7]=0;
//                        if (objRoom.Links[3] == null) objRoom.Need1[6]=2;
//                        if (objRoom.oController < 100000) objRoom.Need1[13]=1;
//                    }
//
//                    if (objRoom.Name == 'W47N3') objRoom.Need1[6]=2;
//
//
//
//                    if (fEnergyInStorage<objRoom.StorageEnergyForce) {
//                        objRoom.Need2[6]=0;
//                        objRoom.Need2[7]=0;
//                    }
//
//
//                    //8 FillerTower
//                    if (objRoom.oController.level == 8) {
//                        objRoom.Need0[20]=1;
//                    }
//
//                    //if (objRoom.Name == 'W4N3')
//                    //9 Logist
//                    objRoom.Need0[17]=1;
//
//                    //10 Far carrier
//                    if (fEnergyInStorage>=objRoom.StorageEnergyForce && objRoom.SentEnergyToRoom != '') {
//                        var TerminalTo = Game.getObjectById(Memory.Data[objRoom.SentEnergyToRoom].Terminal);
//                        if (TerminalTo == null) objRoom.Need1[19]=3;
//                    }
//
//                    //9 NukeFiller если сума G в терминале и нике >=5000 и есть енергия
//
//
//                    //10 Builder Boorted
//                    if (((objRoom.BoostBuilder == 1 && objRoom.IsBuild == 1) || (objRoom.BoostBuilder == 2)) && fEnergyInStorage>=80000){
//                        objRoom.Need1[21]=1;
//                        objRoom.Need1[22]=2;
//                    }
//
//
//                    //11 Defender
//                    //if (objRoom.Name == 'W46N7') objRoom.Need1[23]=2;
//
//                    //if (objRoom.Name == 'W6N3') objRoom.Need1[23]=2;
//
//





            }
        }

    }

    private fun buildQueue() {
        //need0
        //need1
        //slave0
        //slave1
        //need2
        //slave2

        for (i in 0 until this.need.size) this.haveForQueue[i] = this.have[i]
        val fPriorityOfRole = arrayOf(0)

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

        //Slave 0..1
        for (slaveRoom in this.slaveRooms.values) {
            for (i in 0 until slaveRoom.need.size) slaveRoom.haveForQueue[i] = slaveRoom.have[i]
            slaveRoom.buildQueue(this.queue,0)
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
        console.log(showText)
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
                console.log("Build: ${slaveRoom?.name}")
                if (slaveRoom!=null)
                    result = spawn.spawnCreep(slaveRoom.getBodyRole(this.queue[0].role), "mst_${this.queue[0].mainRoom}_${this.queue[0].slaveRoom}_${Game.time} ", spawnOptions.unsafeCast<SpawnOptions>())
                console.log("Build: $result")
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
        console.log("Queue before filling:${this.queue.size}")
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
    private fun getLevelOfRoom(): Int {
        if (this.room.energyCapacityAvailable >= 1300 &&
                this.structureContainerNearSource.size == this.source.size &&
                this.structureContainerNearController.size == 1 &&
                this.structureStorage.size == 1)
            return 1

        return 0
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
