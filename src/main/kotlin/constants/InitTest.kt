package constants

fun Constants.initTest() {
    this.initMainRoomConstantContainer( arrayOf("W3N4") )
    this.getMainRoomConstant("W3N4").initSlaveRoomConstantContainer(arrayOf("W3N3","W3N2"))  //M0

    s(0,0).autoBuildRoad = true
    s(0,1).autoBuildRoad = true
}