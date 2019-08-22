package accounts.serverTest

import mainRoom.MainRoom
import screeps.api.ResourceConstant
import slaveRoom.SlaveRoom

fun constantMainRoomInitTest(mainRoom: MainRoom) {
    //if (mainRoom.name == "W5N3") mainRoom.needMineral["GH2O".unsafeCast<ResourceConstant>()] = 200000  //M0

}

fun constantSlaveRoomInitTest(slaveRoom: SlaveRoom) {
    if (slaveRoom.parent.name == "W7N3" && slaveRoom.name == "W7N5") {
        slaveRoom.need[0][1] = 2
    }
}