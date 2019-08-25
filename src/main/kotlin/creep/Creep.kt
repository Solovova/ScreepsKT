package creep

import mainContext.MainContext
import mainRoom.MainRoom
import slaveRoom.SlaveRoom
import mainRoom
import mainRoom.setLabFillerTask
import mainRoom.setLogistTask
import role
import screeps.api.*
import screeps.utils.toMap
import slaveRoom
import upgrade

fun Creep.getDescribeForQueue(mainContext: MainContext):String {
    val mainRoom: MainRoom = mainContext.mainRoomCollector.rooms[this.memory.mainRoom] ?: return ""
    var slaveRoom: SlaveRoom? = null
    if (this.memory.role in 100..199 || this.memory.role in 1100..1199) {
        slaveRoom = mainRoom.slaveRooms[this.memory.slaveRoom] ?: return ""
    }
    return "(" +(slaveRoom?.describe ?: "" ).padEnd(7) + this.memory.role.toString().padEnd(3) +")"
}

fun Creep.newTask(mainContext: MainContext):Boolean {

    if (this.spawning) return false
    val mainRoom: MainRoom = mainContext.mainRoomCollector.rooms[this.memory.mainRoom] ?: return false
    var slaveRoom: SlaveRoom? = null
    if (this.memory.role in 100..199 || this.memory.role in 1100..1199) {
        slaveRoom = mainRoom.slaveRooms[this.memory.slaveRoom] ?: return false
    }

    this.endTask(mainContext)
    if (mainContext.tasks.isTaskForCreep(this)) return false

    var isTask = false
    val creepCarry: Int = this.carry.toMap().map { it.value }.sum()

    //00 starting creep, harvester, upgrader, builder to level 4
    if (this.memory.role == 0) {
        if (!isTask) isTask = this.takeDroppedEnergy(creepCarry,mainContext)
        if (!isTask) isTask = this.takeFromStorage(creepCarry, mainContext, mainRoom)
        if (!isTask) isTask = this.takeFromContainer(3,creepCarry, mainContext, mainRoom)
        if (!isTask) isTask = this.harvestFromSource(2, creepCarry, mainContext, mainRoom)
        if (!isTask) isTask = this.transferToFilling(creepCarry, mainContext, mainRoom)
        if (!isTask) isTask = this.upgradeNormalOrEmergency(1, creepCarry, mainContext, mainRoom)
        if (!isTask) isTask = this.buildStructure(creepCarry, mainContext, mainRoom)
        if (!isTask) isTask = this.upgradeNormalOrEmergency(0, creepCarry, mainContext, mainRoom)
    }

    if (this.memory.role == 1 || this.memory.role == 1001) {
        if ((this.memory.role == 1) && this.ticksToLive<100) this.memory.role = this.memory.role + 1000

        if (!isTask) isTask = this.harvestFromSource(0,creepCarry,mainContext,mainRoom)

        if (!isTask) isTask =
                if (mainRoom.constant.levelOfRoom < 2)
                    this.transferToContainer(0, creepCarry, mainContext, mainRoom)
                else
                    this.transferToLink(0,creepCarry,mainContext,mainRoom)
    }

    if (this.memory.role == 3 || this.memory.role == 1003) {
        if ((this.memory.role == 3) && this.ticksToLive<100) this.memory.role = this.memory.role + 1000

        if (!isTask) isTask = this.harvestFromSource(1,creepCarry,mainContext,mainRoom)
        if (!isTask) isTask =
                if (mainRoom.constant.levelOfRoom < 2)
                    this.transferToContainer(1, creepCarry, mainContext, mainRoom)
                else
                    this.transferToLink(1,creepCarry,mainContext,mainRoom)
    }


    if (this.memory.role == 2 || this.memory.role == 1002) {
        if ((this.memory.role == 2) && this.ticksToLive<100) this.memory.role = this.memory.role + 1000
        if (!isTask) isTask = this.takeFromContainer(0,creepCarry,mainContext,mainRoom)
        if (!isTask) isTask = this.transferToStorage(creepCarry,mainContext,mainRoom)
    }

    if (this.memory.role == 4 || this.memory.role == 1004) {
        if ((this.memory.role == 4) && this.ticksToLive<100) this.memory.role = this.memory.role + 1000
        if (!isTask) isTask = this.takeFromContainer(1,creepCarry,mainContext,mainRoom)
        if (!isTask) isTask = this.transferToStorage(creepCarry,mainContext,mainRoom)
    }

    if (this.memory.role == 5 || this.memory.role == 1005) {
        if ((this.memory.role == 5) && this.ticksToLive<150) this.memory.role = this.memory.role + 1000
        if (!isTask) isTask = this.takeFromStorage(creepCarry,mainContext,mainRoom)
        if (!isTask) isTask = this.transferToFilling(creepCarry,mainContext,mainRoom)
    }

    if (this.memory.role == 6) {
        if (!isTask) isTask = this.takeFromStorage(creepCarry,mainContext,mainRoom)
        if (!isTask) isTask = this.transferToContainer(2,creepCarry,mainContext,mainRoom)
    }

    if (this.memory.role == 7) {
        if (!isTask) isTask = this.upgradeCreep(mainContext,mainRoom)
        if (!isTask) isTask = this.signRoom(mainContext,mainRoom)
        if (!isTask) isTask = this.takeFromContainer(2,creepCarry,mainContext,mainRoom)
        if (!isTask) isTask = this.upgradeNormalOrEmergency(0,creepCarry,mainContext,mainRoom)
    }

    if (this.memory.role == 19) {
        if (!isTask) isTask = this.upgradeCreep(mainContext,mainRoom)
        if (!isTask) isTask = this.signRoom(mainContext,mainRoom)
        if (!isTask) isTask = this.takeFromContainer(4,creepCarry,mainContext,mainRoom)
        if (!isTask) isTask = this.upgradeNormalOrEmergency(0,creepCarry,mainContext,mainRoom)
    }

    if (this.memory.role == 8) {
        if (mainRoom.getResourceInStorage() > (mainRoom.constant.energyBuilder - 5000)) {
            if (!isTask) isTask = this.takeFromStorage(creepCarry, mainContext, mainRoom)
        }
        //if (!isTask) isTask = this.takeDroppedEnergy(creepCarry,mainContext,mainRoom)   //ToDo костиль
        //if (!isTask) isTask = this.takeFromContainer(3,creepCarry,mainContext,mainRoom) //ToDo костиль
        //if (!isTask) isTask = this.transferToFilling(creepCarry, mainContext, mainRoom) //ToDo костиль
        if (!isTask) isTask = this.buildStructure(creepCarry, mainContext, mainRoom)
        //if (!isTask) isTask = this.transferToStorage(creepCarry,mainContext,mainRoom) //ToDo костиль
    }

    if (this.memory.role == 9) {
        if (!isTask) isTask = this.takeFromStorage(creepCarry,mainContext,mainRoom)
        if (!isTask) isTask = this.transferToFilling(creepCarry, mainContext, mainRoom)
    }

    if (this.memory.role == 10) {
        if (!isTask) isTask = this.buildBigStructure(mainContext, mainRoom)
    }

    if (this.memory.role == 11) {
        if (mainRoom.getResourceInStorage() > (mainRoom.constant.energyBuilder - 5000)) {
            if (!isTask) isTask = this.takeFromStorage(creepCarry,mainContext,mainRoom)
        }
        if (!isTask) isTask = this.transferToBigBuilder(creepCarry,mainContext,mainRoom)
    }

    if (this.memory.role == 13) {
        if (!isTask) isTask = this.takeFromStorage(creepCarry, mainContext, mainRoom)
        if (!isTask) isTask = this.takeFromContainer(3,creepCarry, mainContext, mainRoom)
        if (!isTask) isTask = this.harvestFromSource(2, creepCarry, mainContext, mainRoom)
        if (!isTask) isTask = this.upgradeNormalOrEmergency(0, creepCarry, mainContext, mainRoom)
    }

    if (this.memory.role == 14 || this.memory.role == 1014) {
        if (this.memory.role == 14 && this.ticksToLive<44) this.memory.role = this.memory.role + 1000
        if (!isTask) mainRoom.setLogistTask(this)
    }

    if (this.memory.role == 15) {
        if (!isTask) isTask = this.gotoPosOfContainer(2,mainContext,mainRoom)
        if (!isTask) isTask = this.harvestFromMineral(creepCarry, mainContext, mainRoom)
    }

    if (this.memory.role == 16) {
        if (!isTask) isTask = this.transportMineralToStorage(mainContext,mainRoom)
    }

    if (this.memory.role == 17) {
        if (!isTask) isTask = this.clean(creepCarry,mainContext,mainRoom)
        if (!isTask) isTask = this.drop(creepCarry,mainContext)
        mainRoom.needCleanerCalculate()
    }

    if (this.memory.role == 18 || this.memory.role == 1018) {
        if (this.memory.role == 18 && this.ticksToLive<44) this.memory.role = this.memory.role + 1000
        if (!isTask) mainRoom.setLabFillerTask(this)
    }

    if (this.memory.role == 19 || this.memory.role == 1019) {
        if ((this.memory.role == 19) && this.ticksToLive<110) this.memory.role = this.memory.role + 1000
        if (!isTask) isTask = this.upgradeCreep(mainContext,mainRoom)
        if (!isTask) isTask = this.signRoom(mainContext,mainRoom)
        if (!isTask) isTask = this.takeFromContainer(4,creepCarry,mainContext,mainRoom)
        if (!isTask) isTask = this.upgradeNormalOrEmergency(0,creepCarry,mainContext,mainRoom)
    }

    if (this.memory.role == 20) {
        this.manualDefenceMile(mainContext)
    }

    if (this.memory.role == 21) {
        this.manualDefenceRanged(mainContext)
    }

    if (this.memory.role == 22) {
        this.manualDefenceHealer(mainContext)
    }

    if (this.memory.role == 100) {
        if (!isTask) isTask = this.slaveGoToRoom(mainContext)
        if (!isTask) isTask = this.slaveClaim(mainContext,slaveRoom)

    }

    if (this.memory.role == 101) {
        if (!isTask) isTask = this.slaveGoToRoom(mainContext)
        if (!isTask) isTask = this.slaveHarvest(3, creepCarry,mainContext,slaveRoom)
        if (!isTask) isTask = this.slaveUpgradeNormalOrEmergency(0,creepCarry,mainContext,slaveRoom)
        if (!isTask) isTask = this.slaveTransferToFilling(creepCarry, mainContext,slaveRoom)
        if (!isTask) isTask = this.slaveBuild(creepCarry,mainContext,slaveRoom)
        if (!isTask) isTask = this.slaveUpgradeNormalOrEmergency(1,creepCarry,mainContext,slaveRoom)
    }

    if (this.memory.role == 102) {
        if (!isTask) isTask = this.takeFromStorage(creepCarry,mainContext,mainRoom)
        if (!isTask) isTask = this.slaveTransferToStorageOrContainer(3, creepCarry,mainContext,slaveRoom)
    }

    if (this.memory.role == 103) {
        if (!isTask) isTask = this.slaveGoToRoom(mainContext)
        if (!isTask) isTask = this.slaveSignRoom(mainContext,slaveRoom)
        if (!isTask) isTask = this.slaveReserve(mainContext,slaveRoom)
    }

    if (this.memory.role == 104) {
        if (!isTask) isTask = this.goToPoint(mainContext, RoomPosition(25,25,this.memory.slaveRoom))
    }

    if (this.memory.role == 105 || this.memory.role == 1105) {
        if ((this.memory.role == 105) && this.ticksToLive < 200) this.memory.role = this.memory.role + 1000
        if (!isTask) isTask = this.slaveGoToPosOfContainer(0, mainContext, slaveRoom)
        if (!isTask) isTask = this.slaveHarvest(0, creepCarry, mainContext, slaveRoom)
        if (!isTask) isTask = this.slaveBuild(creepCarry, mainContext, slaveRoom, 2)
        if (!isTask) if (slaveRoom != null && slaveRoom.structureContainerNearSource[0] == null)
            this.room.createConstructionSite(this.pos, STRUCTURE_CONTAINER)
        if (!isTask) isTask = this.slaveRepairContainer(0, creepCarry, mainContext, slaveRoom)
        if (!isTask) isTask = this.slaveTransferToStorageOrContainer(0, creepCarry,mainContext,slaveRoom)

    }

    if (this.memory.role == 109) {
        if (!isTask) isTask = this.slaveGoToRoom(mainContext)
        if (!isTask) isTask = this.slaveTakeFromContainer(4, creepCarry, mainContext, slaveRoom)
        if (!isTask) isTask = this.slaveBuild(creepCarry,mainContext,slaveRoom)
    }

    if (this.memory.role == 107 || this.memory.role == 1107) {
        if ((this.memory.role == 107) && this.ticksToLive < 200) this.memory.role = this.memory.role + 1000
        if (!isTask) isTask = this.slaveGoToPosOfContainer(1, mainContext, slaveRoom)
        if (!isTask) isTask = this.slaveHarvest(1, creepCarry, mainContext, slaveRoom)
        if (!isTask) isTask = this.slaveBuild(creepCarry, mainContext, slaveRoom, 2)
        if (!isTask) if (slaveRoom != null && slaveRoom.structureContainerNearSource[0] == null)
            this.room.createConstructionSite(this.pos, STRUCTURE_CONTAINER)
        if (!isTask) isTask = this.slaveRepairContainer(1, creepCarry, mainContext, slaveRoom)
        if (!isTask) isTask = this.slaveTransferToStorageOrContainer(1, creepCarry, mainContext, slaveRoom)
    }

    if (this.memory.role == 106 || this.memory.role == 1006) {
        //if ((this.memory.role == 106) && this.ticksToLive<100) this.memory.role = this.memory.role + 1000
        if (!isTask) isTask = this.slaveTakeFromContainer(0,creepCarry,mainContext,slaveRoom)
        if (!isTask) isTask = this.slaveBuild(creepCarry, mainContext, slaveRoom, 2)
        if (!isTask) isTask = this.transferToStorage(creepCarry,mainContext,mainRoom)
    }

    if (this.memory.role == 108 || this.memory.role == 1008) {
        //if ((this.memory.role == 108) && this.ticksToLive<100) this.memory.role = this.memory.role + 1000
        if (!isTask) isTask = this.slaveTakeFromContainer(1,creepCarry,mainContext,slaveRoom)
        if (!isTask) isTask = this.slaveBuild(creepCarry, mainContext, slaveRoom, 2)
        if (!isTask) isTask = this.transferToStorage(creepCarry,mainContext,mainRoom)
    }

    if (this.memory.role == 110) {
        if (!isTask) isTask = this.slaveGoToRoom(mainContext)
        if (!isTask) isTask = this.slaveAttackRanged(mainContext, slaveRoom)
    }

    if (this.memory.role == 111) {
        if (!isTask) isTask = this.slaveGoToRoom(mainContext)
        if (!isTask) isTask = this.slaveAttack(mainContext, slaveRoom)
    }

    if (this.memory.role == 115 || this.memory.role == 1115) {
        if ((this.memory.role < 1000) && this.ticksToLive < 200) this.memory.role = this.memory.role + 1000
        if (!isTask) isTask = this.slaveGoToRoom(mainContext)
        if (!isTask) isTask = this.slaveEraser(mainContext, slaveRoom)
    }



    if (this.memory.role in arrayOf(120,122,124,1120,1122,1124)) {
        if ((this.memory.role < 1000) && this.ticksToLive < 200) this.memory.role = this.memory.role + 1000

        var indKl: Int = -1
        if (this.memory.role in arrayOf(120, 121, 1120, 1121)) indKl = 0
        if (this.memory.role in arrayOf(122, 123, 1122, 1123)) indKl = 1
        if (this.memory.role in arrayOf(124, 125, 1124, 1125)) indKl = 2


        //ToDo pick up energy and tomb carry ++ 600
        if (!isTask) isTask = this.slaveGoToRescueFlag(indKl, mainContext, slaveRoom)
        //if (!isTask) if (slaveRoom != null && slaveRoom.structureContainerNearSource[indKl] != null) {
        if (!isTask) isTask = this.takeDroppedEnergy(creepCarry, mainContext, 3)
        //}
        if (!isTask) isTask = this.slaveGoToPosOfContainer(indKl, mainContext, slaveRoom)
        if (!isTask) isTask = this.slaveGoToPosNearSource(indKl, mainContext, slaveRoom)
        if (!isTask) isTask = this.slaveHarvest(indKl, creepCarry, mainContext, slaveRoom)
        if (!isTask) isTask = this.slaveBuild(creepCarry, mainContext, slaveRoom, 3)
        if (!isTask) if (slaveRoom != null && slaveRoom.structureContainerNearSource[indKl] == null){
            val source = slaveRoom.source[indKl]
            if (source != null && this.pos.inRangeTo(source.pos,1))
                this.room.createConstructionSite(this.pos, STRUCTURE_CONTAINER)
        }

        if (!isTask) isTask = this.slaveRepairContainer(indKl, creepCarry, mainContext, slaveRoom)
        if (!isTask) isTask = this.slaveTransferToStorageOrContainer(indKl, creepCarry, mainContext, slaveRoom)
    }

    if (this.memory.role in arrayOf(121,123,125,1121,1123,1125)) {
        if ((this.memory.role < 1000) && this.ticksToLive < 200) this.memory.role = this.memory.role + 1000

        var indKl: Int = -1
        if (this.memory.role in arrayOf(120, 121, 1120, 1121)) indKl = 0
        if (this.memory.role in arrayOf(122, 123, 1122, 1123)) indKl = 1
        if (this.memory.role in arrayOf(124, 125, 1124, 1125)) indKl = 2


        if (!isTask) isTask = this.slaveGoToRescueFlag(indKl, mainContext, slaveRoom)
        if (!isTask) isTask = this.slaveTakeFromContainer(indKl,creepCarry,mainContext,slaveRoom)
        if (!isTask) isTask = this.slaveBuild(creepCarry, mainContext, slaveRoom, 2)
        if (!isTask) isTask = this.transferToStorage(creepCarry,mainContext,mainRoom)

    }

    if (this.memory.role == 126) {
        if (!isTask) isTask = this.slaveGoToRescueFlag(3, mainContext, slaveRoom)
        if (!isTask) isTask = this.slaveGoToPosOfMineral(mainContext, slaveRoom)
        if (!isTask) isTask = this.slaveHarvestFromMineral(creepCarry, mainContext, slaveRoom)
    }

    if (this.memory.role == 127) {
        if (!isTask) isTask = this.slaveGoToRescueFlag(3, mainContext, slaveRoom)
        if (!isTask) isTask = this.slaveTakeMineralFromMineralHarvester(creepCarry,mainContext,mainRoom,slaveRoom)
        if (!isTask) isTask = this.slaveTransferMineralToStorage(creepCarry,mainContext,mainRoom,slaveRoom)
    }

    return isTask

}

