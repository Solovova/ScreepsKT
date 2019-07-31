import logic_main_rooms.MainRooms
import utils.messenger
import screeps.api.*

@Suppress("unused")
fun loop() {
    messenger("HEAD", "", "Current game tick is ${Game.time} _________________________________________", COLOR_WHITE)

    Memory["Account"] = "test"
    var fMainRooms: Array<String> = arrayOf()
    if (Memory["Account"] === "test") fMainRooms = arrayOf("W3N4","W5N6")

    val mainRooms = MainRooms(fMainRooms)

    for (room in mainRooms.rooms.values) console.log(room.name)

    for (room in mainRooms.rooms.values) {
        console.log("--------->we have spawns: ${room.structureSpawn.size}")
        for (spawn in room.structureSpawn)  console.log("--------->1 id: ${spawn.key} energy: ${spawn.value.energy}")

        console.log("--------->we have extensions: ${room.structureExtension.size}")
        for (extension in room.structureExtension)  console.log("--------->1 id: ${extension.key} energy: ${extension.value.energy}")
    }

}