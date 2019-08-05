package constants

import arrayCopy
import mainRoom.MainRoom
import screeps.api.Memory
import screeps.api.get
import screeps.api.set
import slaveRoom.SlaveRoom

//In global memory save only information about main rooms and slave rooms (type slave room), all another information fill in constant init
fun setGlobalConstants() {
    Memory["account"] = "main"
    //Memory["account"] = "test"
    if (Memory["account"] == "test") {

        Memory["mainRoomCollector"] = arrayOf("W3N4")

        //fill null mainRoomsData
        Memory["mainRoomsData"] = object {}
        for (mainRoom in Memory["mainRoomCollector"]) {
            Memory["mainRoomsData"][mainRoom] = object {}
            Memory["mainRoomsData"][mainRoom]["slaveRooms"] = arrayOf<String>()
        }

        //FILL DATA MAIN ROOMS

        //test M0
        //var dMemory = Memory["mainRoomsData"]["W3N4"]
        //dMemory["slaveRooms"] = arrayOf("W1N3")
    }



    if (Memory["account"] == "main") {
        Memory["mainRoomCollector"] = arrayOf("E54N37","E59N36","E52N35","E52N38","E53N39")

        //fill null mainRoomsData
        Memory["mainRoomsData"] = object {}
        for (mainRoom in Memory["mainRoomCollector"]) {
            Memory["mainRoomsData"][mainRoom] = object {}
            Memory["mainRoomsData"][mainRoom]["slaveRooms"] = arrayOf<String>()
        }

        //FILL DATA MAIN ROOMS

        //test M0
        var dMemory = Memory["mainRoomsData"]["E54N37"]
        dMemory["slaveRooms"] = arrayOf("E52N37")
    }
}

fun constantMainRoomInit(mainRoom: MainRoom) {
    if (mainRoom.name == "W3N4") {
                                          //0,1,2,3,4,5,6,7,8,9,0,1,2,3,4,5,6,7,8,9
        arrayCopy(mainRoom.need[0], arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0))
        arrayCopy(mainRoom.need[1], arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0))
        arrayCopy(mainRoom.need[2], arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0))
    }

    if (mainRoom.name == "W1N3") {
        //0,1,2,3,4,5,6,7,8,9,0,1,2,3,4,5,6,7,8,9
        arrayCopy(mainRoom.need[0], arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0))
        arrayCopy(mainRoom.need[1], arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0))
        arrayCopy(mainRoom.need[2], arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0))
    }

    if (mainRoom.name == "E54N37") {
        //0,1,2,3,4,5,6,7,8,9,0,1,2,3,4,5,6,7,8,9
        arrayCopy(mainRoom.need[0], arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0))
        arrayCopy(mainRoom.need[1], arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0))
        arrayCopy(mainRoom.need[2], arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0))
    }

    if (mainRoom.name == "E59N36") {
        //0,1,2,3,4,5,6,7,8,9,0,1,2,3,4,5,6,7,8,9
        arrayCopy(mainRoom.need[0], arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0))
        arrayCopy(mainRoom.need[1], arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0))
        arrayCopy(mainRoom.need[2], arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0))
    }
}

fun constantSlaveRoomInit(slaveRoom: SlaveRoom) {
    if (slaveRoom.parent.name == "W3N4" && slaveRoom.name == "W1N3") {
        //0,1,2,3,4,5,6,7,8,9,0,1,2,3,4,5,6,7,8,9
        arrayCopy(slaveRoom.need[0], arrayOf(0, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0))
        arrayCopy(slaveRoom.need[1], arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0))
        arrayCopy(slaveRoom.need[2], arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0))
    }

    //main
    if (slaveRoom.parent.name == "E54N37" && slaveRoom.name == "E53N39") {
        //0,1,2,3,4,5,6,7,8,9,0,1,2,3,4,5,6,7,8,9
        arrayCopy(slaveRoom.need[0], arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0))
        arrayCopy(slaveRoom.need[1], arrayOf(0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0))
        arrayCopy(slaveRoom.need[2], arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0))
    }

    //main
    if (slaveRoom.parent.name == "E54N37" && slaveRoom.name == "E52N37") {
        //0,1,2,3,4,5,6,7,8,9,0,1,2,3,4,5,6,7,8,9
        arrayCopy(slaveRoom.need[0], arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0))
        arrayCopy(slaveRoom.need[1], arrayOf(0, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0))
        arrayCopy(slaveRoom.need[2], arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0))
    }




}