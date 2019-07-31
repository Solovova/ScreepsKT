import screeps.api.*
import screeps.api.get

@Suppress("unused")
fun loop() {
    messenger("HEAD", "", "Current game tick is ${Game.time} _________________________________________", COLOR_WHITE)
    val mainContext = MainContext()

    //Testing functions
    for (room in mainContext.mainRooms.rooms.values) console.log(room.name)

    for (room in mainContext.mainRooms.rooms.values) {
        console.log("--------->we have spawns: ${room.structureSpawn.size}")
        for (spawn in room.structureSpawn) console.log("--------->1 id: ${spawn.key} energy: ${spawn.value.energy}")

        console.log("--------->we have extensions: ${room.structureExtension.size}")
        for (extension in room.structureExtension) console.log("--------->1 id: ${extension.key} energy: ${extension.value.energy}")
    }

//    var creepTask = CreepTask(TypeOfTask.Drop,idObject0 = "id001",posObject0 = RoomPosition(34,45,"W3N4"),resource = RESOURCE_ENERGY,quantity = 40)
//    mainContext.tasks.add("creep000001", creepTask)
//    creepTask = CreepTask(TypeOfTask.Drop,idObject0 = "id001",posObject0 = RoomPosition(37,49,"W4N4"),resource = RESOURCE_ENERGY,quantity = 40)
//    mainContext.tasks.add("creep000002", creepTask)
//    //console.log(mainContext.tasks.)
//    mainContext.tasks.toMemory()
    mainContext.tasks.fromMemory()
    //console.log((mainContext.tasks.tasks["creep000001"]?.resource == RESOURCE_ENERGY).toString())
    console.log(mainContext.mainRooms.rooms["W3N4"]!!.need[0].toString())
    console.log(mainContext.mainRooms.rooms["W3N4"]!!.need[1].toString())
    console.log(mainContext.mainRooms.rooms["W3N4"]!!.need[2].toString())
}