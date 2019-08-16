import mainContext.MainContext
import mainContext.messenger
import screeps.api.RoomPosition
import screeps.api.*
import screeps.api.Memory
import screeps.utils.unsafe.delete

class CreepTask {
    val type: TypeOfTask
    val idObject0: String
    val posObject0: RoomPosition?
    val idObject1: String
    val posObject1: RoomPosition?
    val resource: ResourceConstant
    val quantity: Int
    var come: Boolean
    var take: Boolean

    fun toMemory(): dynamic {
        val d: dynamic = object {}
        d["type"] = this.type.ordinal
        d["idObject0"] = this.idObject0
        d["posObject0"] = this.posObject0
        d["idObject1"] = this.idObject1
        d["posObject1"] = this.posObject1
        d["resource"] = this.resource
        d["quantity"] = this.quantity
        d["come"] = this.come
        d["take"] = this.take
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
        this.come = false
        this.take = false
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
        this.come = d["come"] as Boolean
        this.take = d["take"] as Boolean


    }
}

//1. End of tasks
//2. Assign new tasks
//3. Do tasks

class  Tasks(private val parent: MainContext) {
    // Все держим в памяти, в конце тика записываем в Memory если пропал объект восстанавливаем из памяти
    val tasks: MutableMap<String, CreepTask> = mutableMapOf() //id of creepsRole

    init {
        this.fromMemory()
    }

    fun add(idCreep: String, task: CreepTask) {
        if (task.posObject0!= null) parent.messenger("TASK", task.posObject0.roomName, "New task: $idCreep ${task.type}", COLOR_CYAN)
        tasks[idCreep] = task
        try {
            Game.getObjectById<Creep>(idCreep)?.say(task.type.toString())
        }catch (e: Exception) {
            parent.messenger("ERROR", idCreep , "Task say", COLOR_RED)
        }
    }

    fun toMemory() {
        val dTasks: dynamic = object {}
        for (task in tasks) dTasks[task.key] = task.value.toMemory()
        val d: dynamic = object {}
        d["tasks"] = dTasks
        delete(Memory["task"])
        Memory["task"] = d
    }

    private fun fromMemory() {
        try {
            tasks.clear()
            val d: dynamic = Memory["task"] ?: return
            val dTasks = d["tasks"] ?: return
            for (key in js("Object").keys(dTasks).unsafeCast<Array<String>>()) tasks[key] = CreepTask(dTasks[key])
        } catch (e: Exception) {
            delete(Memory["task"])
        }
    }

    fun isTaskForCreep(creep: Creep): Boolean {
        return (this.tasks[creep.id] != null)
    }

    fun deleteTask(key: String) {
        parent.messenger("TASK", "", "Delete task: $key ", COLOR_CYAN)
        this.tasks.remove(key)
    }

    fun getEnergyCaringTo(id: String): Int {
        var result = 0
        for (task in this.tasks) {
            if (task.value.type != TypeOfTask.TransferTo) continue
            if (task.value.idObject0 != id) continue
            val creep: Creep = Game.getObjectById(task.key) ?: continue
            result += creep.carry.energy
        }
        return result
    }

    fun getSourceHarvestNum(id: String): Int {
        var result = 0
        for (task in this.tasks) {
            if (task.value.type != TypeOfTask.Harvest) continue
            if (task.value.idObject0 != id) continue
            result += 1
        }
        return result
    }

    fun deleteTaskDiedCreep() {
        val idForDelete: MutableList<String> = mutableListOf()
        for (task in this.tasks)
            if (Game.getObjectById<Creep>(task.key) == null) idForDelete.add(task.key)

        for (key in idForDelete) this.tasks.remove(key)
    }
}