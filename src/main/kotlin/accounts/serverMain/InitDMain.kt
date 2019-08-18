package accounts.serverMain

import mainRoom.MainRoom
import slaveRoom.SlaveRoom

fun constantMainRoomInitMain(mainRoom: MainRoom) {
    val controller = mainRoom.structureController[0]
    if (controller != null) {
        if (controller.level == 7) {
            mainRoom.constant.creepUseBigBuilder = true
            mainRoom.constant.defenceHits = 300000
        }

        if (controller.level == 6) {
            mainRoom.constant.creepUseBigBuilder = true
            mainRoom.constant.defenceHits = 100000
        }
    }

    //if(mainRoom.name == "E54N39") mainRoom.constant.defenceHits = 4000000
}

fun constantSlaveRoomInitMain(slaveRoom: SlaveRoom) {
    if (slaveRoom.parent.name == "E59N38" && slaveRoom.name == "E58N39") {
        slaveRoom.need[1][2] = 3
    }

    if (slaveRoom.parent.name == "E51N33" && slaveRoom.name == "E52N33") {
        slaveRoom.need[1][2] = 2
    }
}