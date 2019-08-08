package constants

fun Constants.initMain() {                         //M0       M1       M2       M3       M4       M5       M6        M7        M8        M9
    this.initMainRoomConstantContainer( arrayOf("E54N37","E59N36","E52N35","E52N38","E53N39","E52N37","E54N39", "E51N39", "E53N38", "E51N37",
                                                "E59N38","E57N34") )
    this.getMainRoomConstant("E54N37").initSlaveRoomConstantContainer(arrayOf("E53N37", "E52N36"))  //M0
    this.getMainRoomConstant("E59N36").initSlaveRoomConstantContainer(arrayOf("E58N36", "E57N34","E57N37"))   //M1
    this.getMainRoomConstant("E52N35").initSlaveRoomConstantContainer(arrayOf("E52N33"))   //M2
    this.getMainRoomConstant("E53N39").initSlaveRoomConstantContainer(arrayOf("E52N39"))            //M4
    this.getMainRoomConstant("E54N39").initSlaveRoomConstantContainer(arrayOf("E54N38"))            //M6
    this.getMainRoomConstant("E51N39").initSlaveRoomConstantContainer(arrayOf("E51N38"))            //M7




    m(0).note  = "O ready 2 move Storage"
    m(1).note  = "Z ready 2 labs ready"
    m(2).note  = "L ready 2 move storage"
    m(3).note  = "H ready 2 labs ready"
    m(4).note  = "Z ready 2 labs ready"
    m(5).note  = "L ready 2 labs ready"
    m(6).note  = "H ready 2 labs ready"
    m(7).note  = "O ready 2 move storage"
    m(8).note  = "O wait 2 move tower storage road"
    m(9).note  = "L wait 2 labs ready road"
    m(10).note = "H wait 2 labs ready"
    m(11).note = "O wait 2 labs ready"
    //E52N36 labs ready
    //E52N33 labs ready
    //E57N37 labs ready

    m(0).energyUpgradable = 70000
    m(2).energyUpgradable = 70000

    m(11).creepSpawn = false


    s(0,0).autoBuildRoad = true
    s(1,0).autoBuildRoad = true
    s(4,0).autoBuildRoad = true
    s(6,0).autoBuildRoad = true
    s(7,0).autoBuildRoad = true

    s(0,1).model = 1
    s(2,0).model = 1



    s(1,1).model = 1
    s(1,2).model = 1

}