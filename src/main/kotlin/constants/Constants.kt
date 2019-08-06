package constants

import messenger
import screeps.api.*
import screeps.utils.unsafe.delete

//Two type constants
//simple - initializing on start of tick, you can change it but data not save in memory and on next tick will be initialized default data
//cashed - initializing in program, at end of tick save to memory, and red on start of next tick

//MainRoom.constant         - simple
//MainRoom.constant.cashed  - cashed

//SlaveRoom.constant         - simple
//SlaveRoom.constant.cashed  - cashed


class Constants {
    val globalConstant: GlobalConstant = GlobalConstant()  //cashed
    var mainRooms: Array<String> = arrayOf() //simple
    val mainRoomConstantContainer: MutableMap<String, MainRoomConstant> = mutableMapOf() //cashed


    init {
        //this.initMain()
        this.initTest()
    }

    fun initMainRoomConstantContainer(names: Array<String>) {
        var resultMainRooms: Array<String> = arrayOf()
        for (name in names)
            if (Game.rooms[name] != null) {
                mainRoomConstantContainer[name] = MainRoomConstant()
                resultMainRooms += name
            } else messenger("ERROR", name, "initialization don't see room in Game.rooms", COLOR_RED)
        this.mainRooms = resultMainRooms
    }

    fun getMainRoomConstant(mainRoomName: String) : MainRoomConstant {
        val mainRoomConstant:MainRoomConstant ? = mainRoomConstantContainer[mainRoomName]
        return if (mainRoomConstant == null) {
            messenger("ERROR", mainRoomName, "initialization don't see MainRoomConstant", COLOR_RED)
            MainRoomConstant()
        }else mainRoomConstant
    }

    fun m(index: Int) : MainRoomConstant {
        return this.getMainRoomConstant(this.mainRooms[index])
    }

    fun s(indexMain: Int, indexSlave: Int) : SlaveRoomConstant {
        val mainRoomConstant:MainRoomConstant = this.getMainRoomConstant(this.mainRooms[indexMain])
        return mainRoomConstant.s(indexSlave)
    }

    fun toDynamic(): dynamic {
        val result: dynamic = object {}
        result["globalConstant"] = this.globalConstant.toDynamic()
        result["mainRoomConstantContainer"] = object {}
        for (record in this.mainRoomConstantContainer)
                result["mainRoomConstantContainer"][record.key] = record.value.toDynamic()
        return result
    }

    fun toMemory() {
        val d: dynamic = this.toDynamic()
        delete(Memory["global"])
        Memory["global"] = d
    }

    fun fromDynamic(d: dynamic) {
        if (d["mainRoomConstantContainer"] != null)
            for (record in mainRoomConstantContainer)
                record.value.fromDynamic(d["mainRoomConstantContainer"][record.key])

        if (d["globalConstant"] != null)
            globalConstant.fromDynamic(d["globalConstant"] )
    }

    fun fromMemory() {
        val d: dynamic = Memory["global"]
        if (d!= null) this.fromDynamic(d)
    }
}