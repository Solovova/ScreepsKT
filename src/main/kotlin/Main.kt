import screeps.api.*
import screeps.utils.unsafe.delete
import kotlin.reflect.KProperty1

var mainContext : MainContext? = null

fun loop() {
//    delete(Memory["rooms"])
//    delete(Memory["account"])
//    delete(Memory["mainRooms"])
//    delete(Memory["mainRoomsData"])
//    delete(Memory["dataCache"])
//    delete(Memory["mainRoomCollector"])
//    delete(Memory["test"])


    messenger("HEAD", "", "Current game tick is ${Game.time} _________________________________________", COLOR_WHITE)
    // Initialisation and protect mainContext
    if (mainContext == null) mainContext = MainContext()

    val protectedMainContext = mainContext ?: return

    // Start tick functions
    if (!protectedMainContext.initOnThisTick)   protectedMainContext.runInStartOfTick()

    // Testing functions
    testingFunctions(protectedMainContext)

    // End tick functions
    protectedMainContext.runInEndOfTick()
}