package constants

import arrayCopy
import mainRoom.MainRoom
import slaveRoom.SlaveRoom

fun constantMainRoomInit(mainRoom: MainRoom) {
    if (mainRoom.name == "W7N3") {
                                          //0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9
        arrayCopy(mainRoom.need[0], arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0))
        arrayCopy(mainRoom.need[1], arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0))
        arrayCopy(mainRoom.need[2], arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0))
    }




    if (mainRoom.name == "E57N34") {
                                          //0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9
        arrayCopy(mainRoom.need[0], arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0))
        arrayCopy(mainRoom.need[1], arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0))
        arrayCopy(mainRoom.need[2], arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0))
    }
}

fun constantSlaveRoomInit(slaveRoom: SlaveRoom) {
    if (slaveRoom.parent.name == "W5N3" && slaveRoom.name == "W7N3") {
        //0,1,2,3,4,5,6,7,8,9,0,1,2,3,4,5,6,7,8,9
        arrayCopy(slaveRoom.need[0], arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0))
        arrayCopy(slaveRoom.need[1], arrayOf(0, 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0))
        arrayCopy(slaveRoom.need[2], arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0))
    }

    if (slaveRoom.parent.name == "E57N34" && slaveRoom.name == "E57N35") {
        //0,1,2,3,4,5,6,7,8,9,0,1,2,3,4,5,6,7,8,9
        arrayCopy(slaveRoom.need[0], arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0))
        arrayCopy(slaveRoom.need[1], arrayOf(0, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0))
        arrayCopy(slaveRoom.need[2], arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0))
    }



}