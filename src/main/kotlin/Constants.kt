import screeps.api.Memory
import screeps.api.get
import screeps.api.set
import screeps.utils.unsafe.delete

//In global memory save only information about main rooms and slave rooms (type slave room), all another information fill in constant init
fun setGlobalConstants() {
    Memory["account"] = "main"
    //Memory["account"] = "test"
    if (Memory["account"] == "test") {

        Memory["mainRooms"] = arrayOf("W3N4")

        //fill null mainRoomsData
        Memory["mainRoomsData"] = object {}
        for (mainRoom in Memory["mainRooms"]) {
            Memory["mainRoomsData"][mainRoom] = object {}
            Memory["mainRoomsData"][mainRoom]["slaveRooms"] = arrayOf<String>()
        }

        //FILL DATA MAIN ROOMS

        //test M0
        //var dMemory = Memory["mainRoomsData"]["W3N4"]
        //dMemory["slaveRooms"] = arrayOf("W1N3")



        //fill null slaveRoomsData
        for (mainRoom in Memory["mainRooms"]){
            for (slaveRoom in Memory["mainRoomsData"][mainRoom]["slaveRooms"]) {
                Memory["mainRoomsData"][mainRoom][slaveRoom] = object {}
            }
        }

        //FILL DATA SLAVE ROOMS
        //test M0 S0
        //dMemory = Memory["mainRoomsData"]["W3N4"]["W1N3"]
        //dMemory["type"] = 1
    }



    if (Memory["account"] == "main") {
        Memory["mainRooms"] = arrayOf("E54N37")

        //fill null mainRoomsData
        Memory["mainRoomsData"] = object {}
        for (mainRoom in Memory["mainRooms"]) {
            Memory["mainRoomsData"][mainRoom] = object {}
            Memory["mainRoomsData"][mainRoom]["slaveRooms"] = arrayOf<String>()
        }

        //FILL DATA MAIN ROOMS

        //test M0
        var dMemory = Memory["mainRoomsData"]["E54N37"]
        dMemory["slaveRooms"] = arrayOf("E53N39","E52N37")

        //fill null slaveRoomsData
        for (mainRoom in Memory["mainRooms"]){
            for (slaveRoom in Memory["mainRoomsData"][mainRoom]["slaveRooms"]) {
                Memory["mainRoomsData"][mainRoom][slaveRoom] = object {}
            }
        }

        //FILL DATA SLAVE ROOMS
        //test M0 S0
        dMemory = Memory["mainRoomsData"]["E54N37"]["E53N39"]
        dMemory["type"] = 1

        //test M0 S1
        dMemory = Memory["mainRoomsData"]["E54N37"]["E52N37"]
        dMemory["type"] = 1

    }
}

fun constantMainRoomInit(mainRoom: MainRoom) {
    if (mainRoom.name == "W3N4") {
                                          //0,1,2,3,4,5,6,7,8,9,0,1,2,3,4,5,6,7,8,9
        arrayCopy(mainRoom.need[0], arrayOf(0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0))
        arrayCopy(mainRoom.need[1], arrayOf(0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0))
        arrayCopy(mainRoom.need[2], arrayOf(0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0))
    }

    if (mainRoom.name == "W1N3") {
        //0,1,2,3,4,5,6,7,8,9,0,1,2,3,4,5,6,7,8,9
        arrayCopy(mainRoom.need[0], arrayOf(0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0))
        arrayCopy(mainRoom.need[1], arrayOf(0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0))
        arrayCopy(mainRoom.need[2], arrayOf(0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0))
    }
}

fun constantSlaveRoomInit(slaveRoom: SlaveRoom) {
    if (slaveRoom.parent.name == "W3N4" && slaveRoom.name == "W1N3") {
        //0,1,2,3,4,5,6,7,8,9,0,1,2,3,4,5,6,7,8,9
        arrayCopy(slaveRoom.need[0], arrayOf(0,4,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0))
        arrayCopy(slaveRoom.need[1], arrayOf(0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0))
        arrayCopy(slaveRoom.need[2], arrayOf(0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0))
    }

    //main
    if (slaveRoom.parent.name == "E54N37" && slaveRoom.name == "E53N39") {
        //0,1,2,3,4,5,6,7,8,9,0,1,2,3,4,5,6,7,8,9
        arrayCopy(slaveRoom.need[0], arrayOf(0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0))
        arrayCopy(slaveRoom.need[1], arrayOf(0,7,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0))
        arrayCopy(slaveRoom.need[2], arrayOf(0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0))
    }

    if (slaveRoom.parent.name == "E54N37" && slaveRoom.name == "E52N37") {
        //0,1,2,3,4,5,6,7,8,9,0,1,2,3,4,5,6,7,8,9
        arrayCopy(slaveRoom.need[0], arrayOf(0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0))
        arrayCopy(slaveRoom.need[1], arrayOf(0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0))
        arrayCopy(slaveRoom.need[2], arrayOf(0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0))
    }
}