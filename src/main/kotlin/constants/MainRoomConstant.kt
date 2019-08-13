package constants

import mainContext.messenger
import screeps.api.COLOR_RED

class MainRoomConstant(val parent: Constants) {
    var slaveRooms : Array<String> = arrayOf() //simple
    val slaveRoomConstantContainer: MutableMap<String,SlaveRoomConstant> = mutableMapOf() //cashed
    //Upgrader


    //Builder

    var note : String = ""

    var towerLastTarget: String = ""        //cashed



    //Creep commands
    var energyBuilder : Int = 20000 //simple //how much energy must be in storage for start building
    var energyUpgradable : Int = 70000 //simple //how much energy must be in storage for start upgrade controller
    var energyForceUpgrade: Int = 100000 //simple //how much energy must be in storage for start upgrade controller
    var creepSpawn: Boolean = true
    var needCleaner: Boolean = false //cashed

    //Room algorithm
    var roomRunNotEveryTickNextTickRun: Int = 0
    var levelOfRoom: Int = 0 //cashed

    var sentEnergyToRoom: String = ""       //simple
    var energyMinStorage: Int = 30000
    var energyMaxStorage: Int = 500000
    var energyMinTerminal: Int = 10000
    var energyMaxTerminal: Int = 60000

    //Market
    var marketBuyEnergy: Boolean = false

    //Mineral
    var mineralMaxInRoom: Int = 200000



    private fun getSlaveRoomConstant(slaveRoomName: String) : SlaveRoomConstant {
        val slaveRoomConstant:SlaveRoomConstant ? = this.slaveRoomConstantContainer[slaveRoomName]
        return if (slaveRoomConstant == null) {
            parent.parent.messenger("ERROR", slaveRoomName, "initialization don't see SlaveRoomConstant", COLOR_RED)
            SlaveRoomConstant()
        }else slaveRoomConstant
    }

    fun s(index: Int) : SlaveRoomConstant {
        if (index>this.slaveRooms.size) {
            parent.parent.messenger("ERROR", "$index", "initialization S out of range slave room", COLOR_RED)
            return SlaveRoomConstant()
        }
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
        result["towerLastTarget"] = this.towerLastTarget
        result["roomRunNotEveryTickNextTickRun"] = this.roomRunNotEveryTickNextTickRun
        result["levelOfRoom"] = this.levelOfRoom
        result["needCleaner"] = this.needCleaner



        if (this.slaveRooms.isNotEmpty()) {
            result["slaveRoomConstantContainer"] = object {}
            for (record in this.slaveRoomConstantContainer)
                    result["slaveRoomConstantContainer"][record.key] = record.value.toDynamic()
        }
        return result
    }

    fun fromDynamic(d: dynamic) {
        if (d == null) return
        if (d["towerLastTarget"] != null) this.towerLastTarget = d["towerLastTarget"] as String
        if (d["roomRunNotEveryTickNextTickRun"] != null) this.roomRunNotEveryTickNextTickRun = d["roomRunNotEveryTickNextTickRun"] as Int
        if (d["levelOfRoom"] != null) this.levelOfRoom = d["levelOfRoom"] as Int
        if (d["needCleaner"] != null) this.needCleaner = d["needCleaner"] as Boolean

        if (d["slaveRoomConstantContainer"] != null)
            for (record in slaveRoomConstantContainer)
                record.value.fromDynamic(d["slaveRoomConstantContainer"][record.key])
    }
}