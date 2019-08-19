import mainContext.MainContext
import mainContext.messenger
import screeps.api.*
import screeps.utils.unsafe.delete
import kotlin.math.roundToInt

var mainContextGlob : MainContext? = null

fun loop() {
    Memory["account"] = ""
    if (Game.rooms["E54N37"] != null)  Memory["account"] = "main"
    if (Game.rooms["W5N3"] != null)    Memory["account"] = "test"

    val cpuStart = Game.cpu.getUsed()





    // Initialisation and protect mainContext
    if (mainContextGlob == null) mainContextGlob = MainContext()

    val protectedMainContext = mainContextGlob ?: return

    protectedMainContext.messenger("HEAD", "", "Current game tick is ${Game.time} _________________________________________", COLOR_WHITE)

    // Start tick functions
    var cpuStartMCStart = Game.cpu.getUsed()
    protectedMainContext.runInStartOfTick()
    cpuStartMCStart = Game.cpu.getUsed() - cpuStartMCStart

    var cpuStartMCNotEvery = Game.cpu.getUsed()
    protectedMainContext.runNotEveryTick()
    cpuStartMCNotEvery = Game.cpu.getUsed() - cpuStartMCNotEvery
    // Testing functions
    testingFunctions(protectedMainContext)

    // End tick functions
    var cpuStartMCEnd = Game.cpu.getUsed()
    protectedMainContext.runInEndOfTick()
    cpuStartMCEnd = Game.cpu.getUsed() - cpuStartMCEnd

    console.log("Construction sites: ${Game.constructionSites.size}")
//    val countCS: MutableMap<String,Int> = mutableMapOf()
//    for (cs in Game.constructionSites.values)
//        if (countCS[cs.pos.roomName] == null) countCS[cs.pos.roomName] = 1
//        else countCS[cs.pos.roomName] = (countCS[cs.pos.roomName] as Int)+1
//    for (valCS in countCS) console.log("${valCS.key}  ${valCS.value}")


    console.log("CPU: ${(Game.cpu.getUsed() - cpuStart).roundToInt()}   Creep: ${Memory["CPUCreep"]} McStart: ${cpuStartMCStart.roundToInt()} McNotEvery: ${cpuStartMCNotEvery.roundToInt()} McEnd: ${(cpuStartMCEnd - Game.cpu.getUsed() + cpuStart).roundToInt()}")

}