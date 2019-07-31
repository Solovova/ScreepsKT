import screeps.api.RoomPosition
import screeps.api.*
import screeps.api.Memory

//ToDo Функция которая определяэт откуда харвестить, на основе сколько енергии есть, сколько уже харвестит и куда ближе

enum class TypeOfTask {
    GoTo,
    Take,
    Transport,
    Build,
    Repair,
    Drop,
    Harvest,
    Transfer,
    Upgrade
}

enum class TypeOfInteractionsObjects {
    Source,
    Container,
    DroppedResource,
    Tombstone,
    Terminal,
    Storage,
    Extension,
    Spawn,
    Creep
}

//interface MemoryTask {
//    task: TypeOfTask = TypeOfTask.goto
//    idFrom: String = ""
//    PosFrom: RoomPosition
//    idTo: string;
//    PosTo: RoomPosition;
//    resource: string;
//    quantity: number;
//    come: boolean;
//}

class CreepTask {
    val type: TypeOfTask
    val idObject0: String
    val posObject0: RoomPosition?
    val idObject1: String
    val posObject1: RoomPosition?
    val resource: ResourceConstant
    val quantity: Int

    fun toMemory(): dynamic {
        val d: dynamic = object {}
        d["type"] = this.type.ordinal
        d["idObject0"] = this.idObject0
        d["posObject0"] = this.posObject0
        d["idObject1"] = this.idObject1
        d["posObject1"] = this.posObject1
        d["resource"] = this.resource
        d["quantity"] = this.quantity
        return d
    }

    constructor (type: TypeOfTask, idObject0: String, posObject0: RoomPosition? = null, idObject1: String = "", posObject1: RoomPosition? = null, resource: ResourceConstant = RESOURCE_ENERGY, quantity: Int = 0) {
        this.type = type
        this.idObject0 = idObject0
        this.posObject0 = posObject0
        this.idObject1 = idObject1
        this.posObject1 = posObject1
        this.resource = resource
        this.quantity = quantity
    }

    constructor(d: dynamic) {
        this.type = TypeOfTask.values()[d["type"] as Int]
        this.idObject0 = d["idObject0"] as String
        if (d["posObject0"] != null) {
            this.posObject0 = RoomPosition(d["posObject0"]["x"] as Int, d["posObject0"]["y"] as Int, d["posObject0"]["roomName"] as String)
        }else{
            this.posObject0 = null
        }

        this.idObject1 = d["idObject1"] as String
        if (d["posObject1"] != null) {
            this.posObject1 = RoomPosition(d["posObject1"]["x"] as Int, d["posObject1"]["y"] as Int, d["posObject1"]["roomName"] as String)
        }else{
            this.posObject1 = null
        }

        this.resource = d["resource"].unsafeCast<ResourceConstant>()
        this.quantity = d["quantity"] as Int
    }
}

class  Tasks {
    // Все держим в памяти, в конце тика записываем в Memory если пропал объект восстанавливаем из памяти
    val tasks: MutableMap<String, CreepTask> = mutableMapOf() //id of creep
    fun add(idCreep: String, task: CreepTask) {
        tasks[idCreep] = task
    }

    fun toMemory() {
        val dTasks: dynamic = object{}
        for (task in tasks) dTasks[task.key] = task.value.toMemory()
        val d: dynamic = object{}
        d["tasks"] = dTasks
        Memory["task"] = d
    }

    fun fromMemory() {
        tasks.clear()
        val d: dynamic = Memory["task"] ?: return
        val dTasks = d["tasks"] ?: return
        for (key in js("Object").keys(dTasks).unsafeCast<Array<String>>()) tasks[key] = CreepTask(dTasks[key])
    }
}