package constants

fun Constants.initMain() {                         //M0       M1       M2       M3       M4       M5       M6        M7        M8        M9      M10
    this.initMainRoomConstantContainer( arrayOf("E54N37","E59N36","E52N35","E52N38","E53N39","E52N37","E54N39", "E51N39", "E53N38", "E51N37","E59N38",
                                                "E57N34") )
    this.getMainRoomConstant("E54N37").initSlaveRoomConstantContainer(arrayOf("E53N37"))  //M0
    this.getMainRoomConstant("E59N36").initSlaveRoomConstantContainer(arrayOf("E58N36", "E57N34"))   //M1
    this.getMainRoomConstant("E53N39").initSlaveRoomConstantContainer(arrayOf("E52N39"))            //M4
    this.getMainRoomConstant("E54N39").initSlaveRoomConstantContainer(arrayOf("E54N38"))            //M6
    this.getMainRoomConstant("E51N39").initSlaveRoomConstantContainer(arrayOf("E51N38"))            //M7




    m(0).note = "ready 2"
    m(1).note = "ready 2"
    m(2).note = "wait  2 build roads"
    m(3).note = "wait  2 build roads"
    m(4).note = "ready 2"
    m(5).note = "wait  2 build roads"
    m(6).note = "wait  2 build roads"
    m(7).note = "wait  2 build roads"

    m(11).creepSpawn = false


    s(0,0).autoBuildRoad = true
    s(1,0).autoBuildRoad = true
    s(4,0).autoBuildRoad = true
    s(6,0).autoBuildRoad = true
    s(7,0).autoBuildRoad = true



    s(1,1).model = 1

}