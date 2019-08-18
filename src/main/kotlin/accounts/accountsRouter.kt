package accounts

import accounts.serverMain.*
import accounts.serverTest.*
import constants.Constants
import mainContext.MineralDataRecord
import mainRoom.MainRoom
import screeps.api.Memory
import screeps.api.ResourceConstant
import screeps.api.get
import slaveRoom.SlaveRoom

fun Constants.initHead() {
    if (Memory["account"] == "main") this.initMainHead()
    if (Memory["account"] == "test") this.initTestHead()
}

fun Constants.initBody() {
    if (Memory["account"] == "main") this.initMainBody()
    if (Memory["account"] == "test") this.initTestBody()
}

fun constantMainRoomInit(mainRoom: MainRoom) {
    if (Memory["account"] == "main") constantMainRoomInitMain(mainRoom)
    if (Memory["account"] == "test") constantMainRoomInitTest(mainRoom)
}

fun constantSlaveRoomInit(slaveRoom: SlaveRoom) {
    if (Memory["account"] == "main") constantSlaveRoomInitMain(slaveRoom)
    if (Memory["account"] == "test") constantSlaveRoomInitTest(slaveRoom)

}

fun initMineralData(mineralData: MutableMap<ResourceConstant, MineralDataRecord>) {
    if (Memory["account"] == "main") initMineralMain(mineralData)
    if (Memory["account"] == "test") initMineralTest(mineralData)
}