package constants

fun Constants.initTest() {
    this.initMainRoomConstantContainer( arrayOf("W3N4") )
    this.getMainRoomConstant("W3N4").initSlaveRoomConstantContainer(arrayOf("W3N3","W3N1"))  //M0
}