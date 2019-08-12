package constants

fun Constants.initMain() {                         //M0       M1       M2       M3       M4       M5       M6        M7        M8        M9
    this.initMainRoomConstantContainer( arrayOf("E54N37","E59N36","E52N35","E52N38","E53N39","E52N37","E54N39", "E51N39", "E53N38", "E51N37",
                                                "E59N38","E57N34","E51N33","E51N35","E58N37","E52N36","E57N32", "E58N39", "E57N39", "E57N35",
                                                "E57N37","E52N33") )
    this.getMainRoomConstant("E54N37").initSlaveRoomConstantContainer(arrayOf("E53N37"))  //M0
    this.getMainRoomConstant("E59N36").initSlaveRoomConstantContainer(arrayOf("E58N36","E57N35"))   //M1
    this.getMainRoomConstant("E52N35").initSlaveRoomConstantContainer(arrayOf("E52N34"))   //M2
    this.getMainRoomConstant("E53N39").initSlaveRoomConstantContainer(arrayOf("E52N39"))            //M4
    this.getMainRoomConstant("E54N39").initSlaveRoomConstantContainer(arrayOf("E54N38"))            //M6
    this.getMainRoomConstant("E51N39").initSlaveRoomConstantContainer(arrayOf("E51N38"))            //M7
    this.getMainRoomConstant("E59N38").initSlaveRoomConstantContainer(arrayOf("E59N37","E59N39"))            //M10
    this.getMainRoomConstant("E57N34").initSlaveRoomConstantContainer(arrayOf("E57N33","E56N33"))            //M11
    this.getMainRoomConstant("E51N33").initSlaveRoomConstantContainer(arrayOf("E51N34"))            //M12
    this.getMainRoomConstant("E58N37").initSlaveRoomConstantContainer(arrayOf("E58N38"))            //M14
    this.getMainRoomConstant("E52N36").initSlaveRoomConstantContainer(arrayOf("E51N36","E53N36"))            //M15
    this.getMainRoomConstant("E57N32").initSlaveRoomConstantContainer(arrayOf("E56N32","E56N31"))            //M16



    m(0).note  = "ready 2"
    m(1).note  = "ready 2"
    m(2).note  = "ready 2"
    m(3).note  = "ready 2"
    m(4).note  = "ready 2"
    m(5).note  = "ready 2"
    m(6).note  = "ready 2"
    m(7).note  = "ready 2"
    m(8).note  = "wait 2"
    m(9).note  = "wait 2"
    m(10).note = "wait 2"
    m(11).note = "wait 2"
    m(12).note = "wait 2"
    m(13).note = "wait 2"
    m(14).note = "wait 2"
    m(15).note = "wait 2"
    m(16).note = "wait 2"
    m(17).note = "wait 2"
    m(18).note = "wait 2"
    m(19).note = "wait 2"
    m(20).note = "wait 2"
    m(21).note = "wait 2"


    m(19).creepSpawn = false  //long

    m(3).sentEnergyToRoom = "E54N37"
    m(4).sentEnergyToRoom = "E54N37"
    m(6).sentEnergyToRoom = "E54N37"



    s(1,1).model = 1 //to tower   88/135

    s(15,0).autoBuildRoad = true
    s(15,1).autoBuildRoad = true
    s(16,0).autoBuildRoad = true
    s(16,1).autoBuildRoad = true


}
