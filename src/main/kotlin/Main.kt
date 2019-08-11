import mainContext.MainContext
import screeps.api.*
import kotlin.math.roundToInt

var mainContextGlob : MainContext? = null

fun loop() {
    val cpuStart = Game.cpu.getUsed()
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

    console.log("Construction sites: ${Game.constructionSites.size}")
//    val countCS: MutableMap<String,Int> = mutableMapOf()
//    for (cs in Game.constructionSites.values)
//        if (countCS[cs.pos.roomName] == null) countCS[cs.pos.roomName] = 1
//        else countCS[cs.pos.roomName] = (countCS[cs.pos.roomName] as Int)+1
//    for (valCS in countCS) console.log("${valCS.key}  ${valCS.value}")


    console.log("CPU: ${(Game.cpu.getUsed() - cpuStart).roundToInt()}")

}