import screeps.api.COLOR_RED
import screeps.api.Game
import screeps.api.get

class MainRooms(names: Array<String>) {
    val rooms: MutableMap<String, MainRoom> = mutableMapOf()

    init {
        for (name in names) {
            if (Game.rooms[name] == null) {
                messenger("ERROR", name, "Not room", COLOR_RED)
            } else {
                rooms[name] = MainRoom(name)
            }
        }
    }
}