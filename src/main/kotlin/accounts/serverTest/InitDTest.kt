package accounts.serverTest

import mainRoom.MainRoom
import slaveRoom.SlaveRoom

fun constantMainRoomInitTest(mainRoom: MainRoom) {

}

fun constantSlaveRoomInitTest(slaveRoom: SlaveRoom) {
    if (slaveRoom.parent.name == "W7N3" && slaveRoom.name == "W7N5") {
        slaveRoom.need[0][1] = 2
    }
}