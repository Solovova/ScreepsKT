package constants

import arrayCopy
import mainRoom.MainRoom
import slaveRoom.SlaveRoom

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


    if (slaveRoom.parent.name == "E53N39" && slaveRoom.name == "E54N39") {
        //0,1,2,3,4,5,6,7,8,9,0,1,2,3,4,5,6,7,8,9
        arrayCopy(slaveRoom.need[0], arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0))
        arrayCopy(slaveRoom.need[1], arrayOf(0, 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0))
        arrayCopy(slaveRoom.need[2], arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0))
    }

    if (slaveRoom.parent.name == "E53N39" && slaveRoom.name == "E51N39") {
        //0,1,2,3,4,5,6,7,8,9,0,1,2,3,4,5,6,7,8,9
        arrayCopy(slaveRoom.need[0], arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0))
        arrayCopy(slaveRoom.need[1], arrayOf(0, 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0))
        arrayCopy(slaveRoom.need[2], arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0))
    }


}