package constants

import screeps.api.COLOR_RED

class MainRoomConstant(parent: Constants) {
    val parent: Constants = parent
    var slaveRooms : Array<String> = arrayOf() //simple
    val slaveRoomConstantContainer: MutableMap<String,SlaveRoomConstant> = mutableMapOf() //cashed
    //Upgrader
    var energyUpgradable : Int = 40000 //simple //how much energy must be in storage for start upgrade controller

    //Builder
    var energyBuilder : Int = 20000 //simple //how much energy must be in storage for start building
    var note : String = ""

    var TowerLastTarget: String = ""        //cashed

    var SentEnergyToRoom: String = ""       //simple

    var testCashed: Int = 0        //cashed
    var testUnCashed: Int = 0        //simple

    private fun getSlaveRoomConstant(slaveRoomName: String) : SlaveRoomConstant {
        val slaveRoomConstant:SlaveRoomConstant ? = this.slaveRoomConstantContainer[slaveRoomName]
        return if (slaveRoomConstant == null) {
            parent.parent.messenger("ERROR", slaveRoomName, "initialization don't see SlaveRoomConstant", COLOR_RED)
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

    fun toDynamic(): dynamic {
        val result: dynamic = object {}
        result["TowerLastTarget"] = this.TowerLastTarget
        result["testCashed"] = this.testCashed

        if (this.slaveRooms.isNotEmpty()) {
            result["slaveRoomConstantContainer"] = object {}
            for (record in this.slaveRoomConstantContainer)
                    result["slaveRoomConstantContainer"][record.key] = record.value.toDynamic()
        }
        return result
    }

    fun fromDynamic(d: dynamic) {
        if (d == null) return
        if (d["TowerLastTarget"] != null) this.TowerLastTarget = d["TowerLastTarget"] as String
        if (d["testCashed"] != null) this.testCashed = d["testCashed"] as Int
        if (d["slaveRoomConstantContainer"] != null)
            for (record in slaveRoomConstantContainer)
                record.value.fromDynamic(d["slaveRoomConstantContainer"][record.key])
    }
}