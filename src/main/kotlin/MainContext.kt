import screeps.api.Memory
import screeps.api.get
import screeps.api.set

class MainContext {
    val mainRooms: MainRooms
    val tasks: Tasks

    constructor() {
        Memory["Account"] = "test"
        var fMainRooms: Array<String> = arrayOf()
        if (Memory["Account"] === "test") fMainRooms = arrayOf("W3N4", "W5N6")

        this.mainRooms = MainRooms(fMainRooms)
        this.tasks = Tasks()
    }
}