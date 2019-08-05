package constants

fun Constants.initMain() {
    this.initMainRoomConstantContainer( arrayOf("E54N37","E59N36","E52N35","E52N38","E53N39","E52N37") )
    this.getMainRoomConstant("E54N37").initSlaveRoomConstantContainer(arrayOf("E52N37"))  //M0
    this.getMainRoomConstant("E53N39").initSlaveRoomConstantContainer(arrayOf("E54N39", "E51N39"))  //M4


    s(0,0).model= 1
    s(4,0).model= 1
    s(4,1).model= 1
}