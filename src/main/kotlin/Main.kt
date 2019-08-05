import screeps.api.*

var mainContext : MainContext? = null

fun loop() {
    messenger("HEAD", "", "Current game tick is ${Game.time} _________________________________________", COLOR_WHITE)
    // Initialisation and protect mainContext
    if (mainContext == null) mainContext = MainContext()

    val protectedMainContext = mainContext ?: return

    // Start tick functions
    protectedMainContext.runInStartOfTick()

    // Testing functions
    testingFunctions(protectedMainContext)

    // End tick functions
    protectedMainContext.runInEndOfTick()
}