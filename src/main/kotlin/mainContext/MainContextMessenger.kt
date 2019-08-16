package mainContext

import mainRoom.MainRoom
import screeps.api.*

fun MainContext.messenger(type: String, room: String, text: String, color: ColorConstant = COLOR_GREY,
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

//    val mainRoom: MainRoom? = if (this.constants !== undefined && this.constants.mainRooms.isNotEmpty())
//        this.mainRoomCollector.rooms[this.constants.mainRooms[0]]
//    else null
//
//    if (mainRoom!= null) {
//        if (mainRoom.room.find(FIND_FLAGS).none { it.color == COLOR_BROWN && it.secondaryColor == COLOR_RED }) {
//            if (type == "QUEUE") return
//        }
//    }

    if (type == "TASK") return
    if (type == "TEST") return


    val prefix: String = when(type) {
        "HEAD" -> "00"
        "PROD" -> "03"
        "QUEUE" -> "07"
        "PROFIT" -> "09"
        "TASK" -> "11"
        "TEST" -> "13"
        "INFO" -> "94"
        "ERROR" -> "95"
        else -> "99"

    }

    val typeForMM = "$prefix$type${messengerMap.size.toString().padStart(3,'0')}"


    val showText = "<font color=${colorToHTMLColor(color)} > $text </font>"
    val showTextBefore = "<font color=${colorToHTMLColor(colorBefore)} > $testBefore </font>"
    val showTextAfter = "<font color=${colorToHTMLColor(colorAfter)} > $testAfter </font>"

    //console.log(typeForMM)
    messengerMap[typeForMM] = "$type : $room $showTextBefore $showText $showTextAfter"
}

fun MainContext.messengerShow(){
    val sortedKeys = messengerMap.keys.toList().sortedBy { it }

    for (key in sortedKeys)
        console.log(messengerMap[key])
    messengerMap.clear()
}