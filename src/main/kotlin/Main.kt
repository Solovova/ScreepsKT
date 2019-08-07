import mainContext.MainContext
import screeps.api.*

var mainContextGlob : MainContext? = null

fun loop() {
//    delete(Memory["rooms"])
//    delete(Memory["account"])
//    delete(Memory["mainRooms"])
//    delete(Memory["mainRoomsData"])
//    delete(Memory["dataCache"])
//    delete(Memory["mainRoomCollector"])
//    delete(Memory["test"])



    // Initialisation and protect mainContext
    if (mainContextGlob == null) mainContextGlob = MainContext()

    val protectedMainContext = mainContextGlob ?: return

    protectedMainContext.messenger("HEAD", "", "Current game tick is ${Game.time} _________________________________________", COLOR_WHITE)

    // Start tick functions
    if (!protectedMainContext.initOnThisTick)   protectedMainContext.runInStartOfTick()
    protectedMainContext.runNotEveryTick()

    // Testing functions
    testingFunctions(protectedMainContext)

    // End tick functions
    protectedMainContext.runInEndOfTick()
}