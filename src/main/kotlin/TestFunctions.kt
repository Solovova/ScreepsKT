import dataCache.CacheCarrier
import screeps.api.COLOR_GREEN

fun testingFunctions (mainContext : MainContext) {
    //Testing functions
//    for (room in mainContext.mainRoomCollector.rooms.values) console.log(room.name)
//
//    for (room in mainContext.mainRoomCollector.rooms.values) {
//        console.log("--------->we have spawns: ${room.structureSpawn.size}")
//        for (spawn in room.structureSpawn) console.log("--------->1 id: ${spawn.key} energy: ${spawn.value.energy}")
//
//        console.log("--------->we have extensions: ${room.structureExtension.size}")
//        for (extension in room.structureExtension) console.log("--------->1 id: ${extension.key} energy: ${extension.value.energy}")
//    }

    //    var creepTask = CreepTask(TypeOfTask.Drop,idObject0 = "id001",posObject0 = RoomPosition(34,45,"W3N4"),resource = RESOURCE_ENERGY,quantity = 40)
//    mainContext.tasks.add("creep000001", creepTask)
//    mainContext.tasks.add("creep000002", creepTask)
//    //console.log(mainContext.tasks.)
//    mainContext.tasks.toMemory()
    //mainContext.tasks.fromMemory()
    //console.log((mainContext.tasks.tasks["creep000001"]?.resource == RESOURCE_ENERGY).toString())
    val fMainRoom = mainContext.mainRoomCollector.rooms.values.firstOrNull()
    if (fMainRoom!=null) {
        console.log(fMainRoom.need[0].toString())
        console.log(fMainRoom.need[1].toString())
        console.log(fMainRoom.need[2].toString())
        console.log(fMainRoom.have.toString())
//        console.log(fMainRoom.structureContainerNearController[0]?.id)
//        console.log(fMainRoom.structureContainerNearSource[0]?.id)
//        console.log(fMainRoom.structureContainerNearSource[1]?.id)
//        console.log(fMainRoom.source[0]?.id)
//        console.log(fMainRoom.source[1]?.id)


//        val fSlaveRoom = fMainRoom.slaveRooms.values.firstOrNull()
//        if (fSlaveRoom != null) {
//            console.log("Slave: ${fSlaveRoom.name}")
//            console.log(fSlaveRoom.need[0].toString())
//            console.log(fSlaveRoom.need[1].toString())
//            console.log(fSlaveRoom.need[2].toString())
//        }
        //mainContext.dataCache.dataCacheRecordRoom["TestData"] = dataCache.CacheRoom(200)
//        delete(Memory["dataCache"])
//        val cacheRecordRoom:dataCache.CacheRoom? = mainContext.dataCache.dataCacheRecordRoom["TestData"]
//        if (cacheRecordRoom != null){
//            cacheRecordRoom.testData++
//            messenger("TEST","____","testData is: ${cacheRecordRoom.testData}", COLOR_GREEN)
//        }

        val carrierAuto : CacheCarrier? = mainContext.dataCache.getCacheRecordRoom("mainContainer0", "E54N37")
        //if (carrierAuto!=null)
        //    messenger("TEST","E54N37","carrierAuto is: ${carrierAuto.needBody}", COLOR_GREEN)
    }

    val mainRoom = mainContext.mainRoomCollector.rooms["E59N36"]
    if (mainRoom != null) {
        for (record in mainRoom.structureContainerNearSource)
            messenger("TEST",mainRoom.name,"structureContainerNearSource: ${record.key}  ${record.value.id}", COLOR_GREEN)

        for (record in mainRoom.source)
            messenger("TEST",mainRoom.name,"source: ${record.key}  ${record.value.id}", COLOR_GREEN)
    }


}