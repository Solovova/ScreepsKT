package constants

fun Constants.initTest() {
    this.initMainRoomConstantContainer( arrayOf("W5N3","W4N3","W7N3") )
    this.getMainRoomConstant("W5N3").initSlaveRoomConstantContainer(arrayOf("W5N2"))

    s(0,0).autoBuildRoad = true

}