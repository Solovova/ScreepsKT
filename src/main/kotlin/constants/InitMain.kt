package constants

fun Constants.initMain() {
    this.initMainRoomConstantContainer( arrayOf("E54N37","E59N36","E52N35","E52N38","E53N39","E52N37") )
    this.getMainRoomConstant("E54N37").initSlaveRoomConstantContainer(arrayOf("E52N37"))
    this.getMainRoomConstant("E53N39").initSlaveRoomConstantContainer(arrayOf("E54N39", "E51N39"))

    //-------------------------------------------------------
    //M0
    var mainRoomConstant = this.getMainRoomConstant("E54N37")
    //M0 S0
    var slaveRoomConstant = mainRoomConstant.getSlaveRoomConstant("E52N37")
    slaveRoomConstant.model = 1


    //M4
    mainRoomConstant = this.getMainRoomConstant("E53N39")

    //M4 S0
    slaveRoomConstant = mainRoomConstant.getSlaveRoomConstant("E54N39")
    slaveRoomConstant.model = 1

    //M4 S1
    slaveRoomConstant = mainRoomConstant.getSlaveRoomConstant("E51N39")
    slaveRoomConstant.model = 1

}