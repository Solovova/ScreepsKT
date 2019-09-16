package accounts.serverMain

import constants.Constants
import screeps.api.ResourceConstant

//Call before init of constants
fun Constants.initMainHead() {                   //M0       M1       M2       M3       M4       M5       M6        M7        M8        M9
    this.initMainRoomConstantContainer( arrayOf("E54N37","E59N36","E52N35","E52N38","E53N39","E52N37","E54N39", "E51N39", "E53N38", "E51N37",
                                                "E59N38","E57N34","E51N33","E51N35","E58N37","E52N36","E57N32", "E58N39", "E57N39", "E57N35",
                                                "E57N37","E52N33","E58N31") )
    //Colonization E49N39
    this.getMainRoomConstant("E54N37").initSlaveRoomConstantContainer(arrayOf("E53N37","E54N36"))                                //M0
    this.getMainRoomConstant("E59N36").initSlaveRoomConstantContainer(arrayOf("E58N36"))                                //M1
    this.getMainRoomConstant("E52N35").initSlaveRoomConstantContainer(arrayOf("E52N34"))                                //M2
    this.getMainRoomConstant("E53N39").initSlaveRoomConstantContainer(arrayOf("E52N39"))                                //M4
    this.getMainRoomConstant("E54N39").initSlaveRoomConstantContainer(arrayOf("E54N38"))                                //M6
    this.getMainRoomConstant("E51N39").initSlaveRoomConstantContainer(arrayOf("E51N38","E49N39"))                                //M7
    this.getMainRoomConstant("E59N38").initSlaveRoomConstantContainer(arrayOf("E59N37","E59N39"))                       //M10
    this.getMainRoomConstant("E57N34").initSlaveRoomConstantContainer(arrayOf("E57N33","E56N33","E56N34"))              //M11
    this.getMainRoomConstant("E51N33").initSlaveRoomConstantContainer(arrayOf("E51N34"))                        //M12
    this.getMainRoomConstant("E58N37").initSlaveRoomConstantContainer(arrayOf("E58N38"))                                //M14
    this.getMainRoomConstant("E52N36").initSlaveRoomConstantContainer(arrayOf("E51N36","E53N36"))                       //M15
    this.getMainRoomConstant("E57N32").initSlaveRoomConstantContainer(arrayOf("E56N32","E56N31","E57N31"))              //M16
    this.getMainRoomConstant("E57N39").initSlaveRoomConstantContainer(arrayOf("E56N39","E57N38","E56N38"))              //M18
    this.getMainRoomConstant("E57N35").initSlaveRoomConstantContainer(arrayOf("E58N35","E59N35","E56N35"))              //M19
    this.getMainRoomConstant("E57N37").initSlaveRoomConstantContainer(arrayOf("E57N36"))                                //M20

    this.getMainRoomConstant("E58N31").initSlaveRoomConstantContainer(arrayOf("E59N31"))                                //M22

}

//Call after init constants and load from memory
fun Constants.initMainBody() {

    //Far transfer E51N33 -> E52N33 wait
    //Far transfer E59N38 -> E58N39
    //E57N35

//    m(3).sentEnergyToRoom = "E54N37"
//    m(4).sentEnergyToRoom = "E54N37"
//    m(6).sentEnergyToRoom = "E54N37"

    m(0).marketBuyEnergy = true
    s(7,1).model = 1
    //m(7).creepUpgradeRole[7] = true
    //m(6).creepUpgradeRole[7] = true
    //m(3).creepUpgradeRole[7] = true

    m(0).reactionActive = "ZK"  //ZK
    m(1).reactionActive = "OH"
    m(2).reactionActive = "GH2O"
    m(3).reactionActive = "G" //XGH2O
    m(4).reactionActive = "GH" //UL
    m(5).reactionActive = "OH"
    m(6).reactionActive = "GH2O"
    m(7).reactionActive = "" //XGH2O
    m(8).reactionActive = "OH"
    m(9).reactionActive = "OH"
    m(10).reactionActive = "OH"
    m(11).reactionActive = "XGH2O"
    m(12).reactionActive = "XGH2O"
    m(13).reactionActive = ""
    m(14).reactionActive = "XGH2O"
    m(15).reactionActive = "XGH2O"
    m(16).reactionActive = "XGH2O"
    m(18).reactionActive = ""



    m(10).defenceHits = 3000000



}



