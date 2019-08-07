package mainContext

import Tasks
import constants.Constants
import creep.doTask
import creep.newTask
import mainRoom.MainRoomCollector
import screeps.api.*
import screeps.utils.isEmpty
import screeps.utils.unsafe.delete

//mainContext.MainContext initial only then died
//in start of tick initial mainRoomCollector
//in start of tick initial Constant and assign it need place

class MainContext {
    var mainRoomCollector: MainRoomCollector = MainRoomCollector(this, arrayOf())
    val tasks: Tasks = Tasks(this)
    val constants: Constants = Constants(this)
    var initOnThisTick: Boolean = true
    private val messengerMap : MutableMap<String,String> = mutableMapOf()

    init {
        this.runInStartOfTick()
        this.constants.fromMemory()
    }

    fun runInStartOfTick() {
        this.mainRoomCollector = MainRoomCollector(this, this.constants.mainRooms)
        this.mainRoomCollector.runInStartOfTick()
        for (creep in Game.creeps.values) creep.newTask(this)
    }

    fun runNotEveryTick() {
        this.mainRoomCollector.runNotEveryTick()
        this.tasks.deleteTaskDiedCreep()
        this.houseKeeping()
    }

    fun runInEndOfTick() {
        this.messengerShow()
        this.initOnThisTick = false
        for (creep in Game.creeps.values) creep.doTask(this)
        this.tasks.toMemory()
        this.constants.toMemory()
    }

    private fun houseKeeping() {
        if (Game.creeps.isEmpty()) return
        for ((creepName, _) in Memory.creeps) {
            if (Game.creeps[creepName] == null) {
                delete(Memory.creeps[creepName])
            }
        }
    }

    fun messenger(type: String, room: String, text: String, color: ColorConstant = COLOR_GREY,
                  testBefore: String = "", colorBefore: ColorConstant = COLOR_WHITE,
                  testAfter: String = "", colorAfter: ColorConstant = COLOR_WHITE) {

        fun colorToHTMLColor(color: ColorConstant): String {
            return when(color) {
                COLOR_YELLOW -> "yellow"
                COLOR_RED -> "red"
                COLOR_GREEN -> "green"
                COLOR_BLUE -> "blue"
                COLOR_ORANGE -> "orange"
                COLOR_CYAN -> "cyan"
                COLOR_GREY -> "grey"
                else -> "white"

            }
        }

        if (type == "TASK") return
        //if (type == "TEST") return


        val prefix: String = when(type) {
            "HEAD" -> "00"
            "ERROR" -> "05"
            "QUEUE" -> "07"
            "PROFIT" -> "09"
            "TASK" -> "11"
            "TEST" -> "13"
            else -> "99"

        }

        val typeForMM = "$prefix$type${messengerMap.size.toString().padStart(3,'0')}"


        val showText = "<font color=${colorToHTMLColor(color)} > $text </font>"
        val showTextBefore = "<font color=${colorToHTMLColor(colorBefore)} > $testBefore </font>"
        val showTextAfter = "<font color=${colorToHTMLColor(colorAfter)} > $testAfter </font>"

        //console.log(typeForMM)
        messengerMap[typeForMM] = "$type : $room $showTextBefore $showText $showTextAfter"
    }

    private fun messengerShow(){
        val sortedKeys = messengerMap.keys.toList().sortedBy { it }

        for (key in sortedKeys)
            console.log(messengerMap[key])
        messengerMap.clear()
    }
}