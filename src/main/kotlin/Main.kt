import mainContext.MainContext
import mainContext.messenger
import screeps.api.*
import screeps.utils.unsafe.delete
import kotlin.math.roundToInt

var mainContextGlob : MainContext? = null

fun loop() {

    val cpuStart = Game.cpu.getUsed()




    // Initialisation and protect mainContext
    if (mainContextGlob == null) mainContextGlob = MainContext()

    val protectedMainContext = mainContextGlob ?: return

    protectedMainContext.messenger("HEAD", "", "Current game tick is ${Game.time} _________________________________________", COLOR_WHITE)

    // Start tick functions
    protectedMainContext.runInStartOfTick()

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