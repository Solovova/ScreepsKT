package accounts.serverMain

import mainRoom.MainRoom
import screeps.api.ResourceConstant
import slaveRoom.SlaveRoom

fun constantMainRoomInitMain(mainRoom: MainRoom) {
    val controller = mainRoom.structureController[0]
    if (controller != null) {
        if (controller.level == 8) {
            mainRoom.constant.creepUseBigBuilder = true
            mainRoom.constant.defenceHits = 300000
            mainRoom.constant.energyExcessSent = 90000

        }

        if (controller.level == 7) {
            mainRoom.constant.creepUseBigBuilder = true
            mainRoom.constant.defenceHits = 300000
            mainRoom.constant.energyExcessSent = 90000
        }

        if (controller.level == 6) {
            mainRoom.constant.creepUseBigBuilder = true
            mainRoom.constant.defenceHits = 100000
            mainRoom.constant.energyExcessSent = 90000
        }
    }

//    if (mainRoom.name == "E57N34") mainRoom.constant.energyExcessSent = 220000
//    if (mainRoom.name == "E57N35") mainRoom.constant.energyExcessSent = 220000
//    if (mainRoom.name == "E52N33") mainRoom.constant.energyExcessSent = 220000
//    if (mainRoom.name == "E58N31") mainRoom.constant.energyExcessSent = 220000


    if (mainRoom.name == "E54N39") mainRoom.needMineral["G".unsafeCast<ResourceConstant>()] = 10000  //M6
    if (mainRoom.name == "E54N39") mainRoom.needMineral["GH2O".unsafeCast<ResourceConstant>()] = 10000  //M6
    if (mainRoom.name == "E52N38") mainRoom.needMineral["XGH2O".unsafeCast<ResourceConstant>()] = 10000  //M3
    if (mainRoom.name == "E52N37") mainRoom.needMineral["L".unsafeCast<ResourceConstant>()] = 10000  //M3
    if (mainRoom.name == "E54N37") mainRoom.needMineral["O".unsafeCast<ResourceConstant>()] = 10000  //M3
    if (mainRoom.name == "E58N39") mainRoom.needMineral["H".unsafeCast<ResourceConstant>()] = 10000  //M3
    if (mainRoom.name == "E59N36") mainRoom.needMineral["Z".unsafeCast<ResourceConstant>()] = 10000
    if (mainRoom.name == "E57N39") mainRoom.needMineral["U".unsafeCast<ResourceConstant>()] = 10000







    //if(mainRoom.name == "E54N39") mainRoom.constant.defenceHits = 4000000
}

fun constantSlaveRoomInitMain(slaveRoom: SlaveRoom) {
    if (slaveRoom.parent.name == "E51N39" && slaveRoom.name == "E49N39") {
        slaveRoom.need[0][0] = 0
        slaveRoom.need[0][2] = 4
    }


}