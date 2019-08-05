package constants

import messenger
import screeps.api.COLOR_RED
import screeps.api.Game
import screeps.api.get


class Constants {
    val globalConstant: GlobalConstant = GlobalConstant()
    var mainRooms: Array<String> = arrayOf()
    val mainRoomConstantContainer: MutableMap<String, MainRoomConstant> = mutableMapOf()

    init {
        this.initMain()
        //this.initTest()
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
}