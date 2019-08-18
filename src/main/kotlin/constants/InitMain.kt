package constants

import screeps.api.RESOURCE_KEANIUM
import screeps.api.RESOURCE_ZYNTHIUM

fun Constants.initMainHead() {                   //M0       M1       M2       M3       M4       M5       M6        M7        M8        M9
    this.initMainRoomConstantContainer( arrayOf("E54N37","E59N36","E52N35","E52N38","E53N39","E52N37","E54N39", "E51N39", "E53N38", "E51N37",
                                                "E59N38","E57N34","E51N33","E51N35","E58N37","E52N36","E57N32", "E58N39", "E57N39", "E57N35",
                                                "E57N37","E52N33") )
    //Colonization E49N39
    this.getMainRoomConstant("E54N37").initSlaveRoomConstantContainer(arrayOf("E53N37","E54N36"))                                //M0
    this.getMainRoomConstant("E59N36").initSlaveRoomConstantContainer(arrayOf("E58N36"))                                //M1
    this.getMainRoomConstant("E52N35").initSlaveRoomConstantContainer(arrayOf("E52N34"))                                //M2
    this.getMainRoomConstant("E53N39").initSlaveRoomConstantContainer(arrayOf("E52N39"))                                //M4
    this.getMainRoomConstant("E54N39").initSlaveRoomConstantContainer(arrayOf("E54N38"))                                //M6
    this.getMainRoomConstant("E51N39").initSlaveRoomConstantContainer(arrayOf("E51N38"))                                //M7
    this.getMainRoomConstant("E59N38").initSlaveRoomConstantContainer(arrayOf("E59N37","E59N39","E58N39"))                       //M10
    this.getMainRoomConstant("E57N34").initSlaveRoomConstantContainer(arrayOf("E57N33","E56N33"))              //M11
    this.getMainRoomConstant("E51N33").initSlaveRoomConstantContainer(arrayOf("E51N34","E52N33"))                        //M12
    this.getMainRoomConstant("E58N37").initSlaveRoomConstantContainer(arrayOf("E58N38"))                                //M14
    this.getMainRoomConstant("E52N36").initSlaveRoomConstantContainer(arrayOf("E51N36","E53N36"))                       //M15
    this.getMainRoomConstant("E57N32").initSlaveRoomConstantContainer(arrayOf("E56N32","E56N31","E57N31"))              //M16
    this.getMainRoomConstant("E57N39").initSlaveRoomConstantContainer(arrayOf("E56N39","E57N38","E56N38"))              //M18
    this.getMainRoomConstant("E57N35").initSlaveRoomConstantContainer(arrayOf("E58N35","E59N35"))              //M19
    this.getMainRoomConstant("E57N37").initSlaveRoomConstantContainer(arrayOf("E57N36"))                                //M20
}

fun Constants.initMain() {

    //Far transfer E51N33 -> E52N33 wait
    //Far transfer E59N38 -> E58N39
    s(10,2).model = 1
    s(12,1).model = 1
    //E57N35


//    m(3).sentEnergyToRoom = "E54N37"
//    m(4).sentEnergyToRoom = "E54N37"
//    m(6).sentEnergyToRoom = "E54N37"


    m(0).marketBuyEnergy = true
    m(1).marketBuyEnergy = true
    m(2).marketBuyEnergy = true
    m(3).marketBuyEnergy = true
    m(4).marketBuyEnergy = true
    m(5).marketBuyEnergy = true
    m(6).marketBuyEnergy = true
    m(7).marketBuyEnergy = true
    m(8).marketBuyEnergy = true
    m(9).marketBuyEnergy = true
    m(10).marketBuyEnergy = true
    m(11).marketBuyEnergy = true
    m(12).marketBuyEnergy = true
    m(14).marketBuyEnergy = true
    m(15).marketBuyEnergy = true
    m(16).marketBuyEnergy = true
    m(18).marketBuyEnergy = true

    m(0).reactionActive = "ZK"
    m(4).reactionActive = "GH" //UL
    m(6).reactionActive = "GH2O"

    m(1).reactionActive = "GH2O"
    m(2).reactionActive = "OH"
    m(3).reactionActive = "OH"
    m(5).reactionActive = "OH"
    m(7).reactionActive = "OH"
    m(8).reactionActive = "OH"
    m(9).reactionActive = "OH"
    m(10).reactionActive = "OH"
    m(11).reactionActive = "OH"
    m(12).reactionActive = "OH"
    m(14).reactionActive = "GH"
    m(15).reactionActive = "GH"
    m(16).reactionActive = "OH"


    m(6).creepUseBigBuilder = true
    m(6).defenceHits = 3000000

    m(0).creepUseBigBuilder = true
    m(0).defenceHits = 1000000

    m(1).creepUseBigBuilder = true
    m(1).defenceHits = 1000000

    m(2).creepUseBigBuilder = true
    m(2).defenceHits = 1000000












}
