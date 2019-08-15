package productionController

import mainContext.MainContext
import screeps.api.ResourceConstant
import toSecDigit

class ProductionController(val parent: MainContext) {
    val constResourcesAll:Array<ResourceConstant> = js("RESOURCES_ALL").unsafeCast<Array<ResourceConstant>>()
    val constReactionTime:dynamic = js("REACTION_TIME").unsafeCast<dynamic>()
    val mineralData: MutableMap<ResourceConstant, MineralData> = mutableMapOf()

    fun runInStartOfTick() {
        this.mineralDataFill()
    }

    fun runNotEveryTick() {
    }

    fun runInEndOfTick() {
        this.mineralInfoShow()
    }

    private fun mineralDataFill() {
        for (res in constResourcesAll)
            mineralData[res] = MineralData(quantity = parent.mainRoomCollector.rooms.values.sumBy {it.getResource(res)})
    }

    private fun mineralInfoShow() {
        val mineralInfo = MineralInfo()
        for (res in constResourcesAll)
            if ((mineralData[res]?.quantity ?: 0) != 0)
                mineralInfo.addColumn(arrayOf("$res", (mineralData[res]?.quantity ?: 0).toString().toSecDigit(),"_______________"))
        mineralInfo.show(parent)
    }
}