package constants

import arrayCopy
import mainRoom.MainRoom
import slaveRoom.SlaveRoom

fun constantMainRoomInit(mainRoom: MainRoom) {

}

fun constantSlaveRoomInit(slaveRoom: SlaveRoom) {

    if (slaveRoom.parent.name == "E59N38" && slaveRoom.name == "E58N39") {
        slaveRoom.need[1][2] = 3
    }

    if (slaveRoom.parent.name == "E51N33" && slaveRoom.name == "E52N33") {
        slaveRoom.need[1][2] = 2
    }

}