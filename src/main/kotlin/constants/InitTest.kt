package constants

import screeps.api.*

fun Constants.initTestHead() {
    this.initMainRoomConstantContainer( arrayOf("W5N3","W4N3","W7N3") )
    this.getMainRoomConstant("W5N3").initSlaveRoomConstantContainer(arrayOf("W5N2","W5N4")) //M0
    this.getMainRoomConstant("W4N3").initSlaveRoomConstantContainer(arrayOf("W4N4")) //M1
    this.getMainRoomConstant("W7N3").initSlaveRoomConstantContainer(arrayOf("W7N4")) //M2
}

fun Constants.initTest() {
    //m(1).sentEnergyToRoom="W5N3"
    //m(2).sentEnergyToRoom="W5N3"
    m(0).creepUseBigBuilder = true
    m(1).creepUseBigBuilder = true
    m(2).creepUseBigBuilder = true

    m(0).note = "room 0"
    m(1).note = "room 1"
    m(2).note = "room 2"

    s(1,0).profitPerTickPrevious = 0

    m(0).reactionActive = "ZK"
    m(1).reactionActive = "UL"
    m(2).reactionActive = "G"
}