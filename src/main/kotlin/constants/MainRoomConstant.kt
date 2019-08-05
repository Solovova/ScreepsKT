package constants

import messenger
import screeps.api.COLOR_RED

class MainRoomConstant {
    var slaveRooms : Array<String> = arrayOf()
    val slaveRoomConstantContainer: MutableMap<String,SlaveRoomConstant> = mutableMapOf()
    //Upgrader
    var energyUpgradable : Int = 40000 //how much energy must be in storage for start upgrade controller

    //Builder
    var energyBuilder : Int = 20000 //how much energy must be in storage for start building

    fun getSlaveRoomConstant(slaveRoomName: String) : SlaveRoomConstant {
        val slaveRoomConstant:SlaveRoomConstant ? = this.slaveRoomConstantContainer[slaveRoomName]
        return if (slaveRoomConstant == null) {
            messenger("ERROR", slaveRoomName, "initialization don't see SlaveRoomConstant", COLOR_RED)
            SlaveRoomConstant()
        }else slaveRoomConstant
    }

    fun s(index: Int) : SlaveRoomConstant {
        return this.getSlaveRoomConstant(this.slaveRooms[index])
    }

    fun initSlaveRoomConstantContainer(names: Array<String>) {
        var resultSlaveRooms: Array<String> = arrayOf()
        for (name in names){
            slaveRoomConstantContainer[name] = SlaveRoomConstant()
            resultSlaveRooms += name
        }
        this.slaveRooms = resultSlaveRooms
    }
}