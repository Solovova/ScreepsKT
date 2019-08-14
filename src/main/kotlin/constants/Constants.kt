package constants

import mainContext.MainContext
import mainContext.messenger
import screeps.api.*
import screeps.utils.unsafe.delete

//Two type constants
//simple - initializing on start of tick, you can change it but data not save in memory and on next tick will be initialized default data
//cashed - initializing in program, at end of tick save to memory, and red on start of next tick

//MainRoom.constant         - simple
//MainRoom.constant.cashed  - cashed

//SlaveRoom.constant         - simple
//SlaveRoom.constant.cashed  - cashed


class Constants(val parent: MainContext) {
    val globalConstant: GlobalConstant = GlobalConstant()  //cashed
    var mainRooms: Array<String> = arrayOf() //simple
    val mainRoomConstantContainer: MutableMap<String, MainRoomConstant> = mutableMapOf() //cashed

    var battleGroups: Array<String> = arrayOf() //cashed
    val battleGroupConstantContainer: MutableMap<String, BattleGroupConstant> = mutableMapOf() //cashed
    val battleGroupContainerConstant: BattleGroupContainerConstant = BattleGroupContainerConstant() //cashed

    init {
        if (Game.rooms["E54N37"] != null) this.initMain()
        else this.initTest()
    }

    fun initMainRoomConstantContainer(names: Array<String>) {
        var resultMainRooms: Array<String> = arrayOf()
        for (name in names)
            if (Game.rooms[name] != null) {
                mainRoomConstantContainer[name] = MainRoomConstant(this)
                resultMainRooms += name
            } else parent.messenger("ERROR", name, "initialization don't see room in Game.rooms", COLOR_RED)
        this.mainRooms = resultMainRooms
    }

    fun getMainRoomConstant(mainRoomName: String) : MainRoomConstant {
        val mainRoomConstant:MainRoomConstant ? = mainRoomConstantContainer[mainRoomName]
        return if (mainRoomConstant == null) {
            parent.messenger("ERROR", mainRoomName, "initialization don't see MainRoomConstant", COLOR_RED)
            MainRoomConstant(this)
        }else mainRoomConstant
    }

    fun m(index: Int) : MainRoomConstant {
        if (index>this.mainRooms.size) {
            parent.messenger("ERROR", "$index", "initialization M out of range main room", COLOR_RED)
            return MainRoomConstant(this)
        }
        return this.getMainRoomConstant(this.mainRooms[index])
    }

    fun s(indexMain: Int, indexSlave: Int) : SlaveRoomConstant {
        if (indexMain>this.mainRooms.size) {
            parent.messenger("ERROR", "$indexSlave", "initialization S out of range main room", COLOR_RED)
            return SlaveRoomConstant()
        }
        val mainRoomConstant:MainRoomConstant = this.getMainRoomConstant(this.mainRooms[indexMain])
        return mainRoomConstant.s(indexSlave)
    }

    private fun toDynamic(): dynamic {
        val result: dynamic = object {}
        result["globalConstant"] = this.globalConstant.toDynamic()
        result["mainRoomConstantContainer"] = object {}
        for (record in this.mainRoomConstantContainer)
                result["mainRoomConstantContainer"][record.key] = record.value.toDynamic()

        result["battleGroups"] = battleGroups
        result["battleGroupConstantContainer"] = object {}
        for (record in this.battleGroupConstantContainer)
            result["battleGroupConstantContainer"][record.key] = record.value.toDynamic()
        result["battleGroupContainerConstant"] = this.battleGroupContainerConstant.toDynamic()
        return result
    }

    fun toMemory() {
        val d: dynamic = this.toDynamic()
        delete(Memory["global"])
        Memory["global"] = d
    }

    private fun fromDynamic(d: dynamic) {
        if (d["mainRoomConstantContainer"] != null)
            for (record in mainRoomConstantContainer)
                record.value.fromDynamic(d["mainRoomConstantContainer"][record.key])

        if (d["globalConstant"] != null)
            globalConstant.fromDynamic(d["globalConstant"] )

        if ((d["battleGroups"] != null))
            this.battleGroups = d["battleGroups"] as Array<String>


        for (record in this.battleGroups) {
            this.battleGroupConstantContainer[record] = BattleGroupConstant()
            if (d["battleGroupConstantContainer"] != null && d["battleGroupConstantContainer"][record] != null)
                this.battleGroupConstantContainer[record]?.fromDynamic(d["battleGroupConstantContainer"][record])
        }

        if (d["battleGroupContainerConstant"] != null)
            battleGroupContainerConstant.fromDynamic(d["battleGroupContainerConstant"] )
    }

    fun fromMemory() {
        val d: dynamic = Memory["global"]
        if (d!= null) this.fromDynamic(d)
    }
}