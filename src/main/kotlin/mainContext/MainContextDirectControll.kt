package mainContext

import screeps.api.*
import slaveRoom

fun MainContext.directControlTaskClearInRoom(nameSlaveRoom: String) {
    try {
        for (creep in Game.creeps.values)
            if (creep.memory.slaveRoom == nameSlaveRoom) tasks.deleteTask(creep.id)

    }catch (e: Exception) {
        messenger("ERROR", "Direct control","", COLOR_RED)
    }
    messenger("TEST", "Direct control"," Erase all tasks in slave room: $nameSlaveRoom", COLOR_RED)
}