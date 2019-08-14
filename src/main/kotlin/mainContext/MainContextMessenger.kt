package mainContext

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

    if (type == "TASK") return
    if (type == "TEST") return


    val prefix: String = when(type) {
        "HEAD" -> "00"
        "ERROR" -> "95"
        "INFO" -> "94"
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

fun MainContext.messengerShow(){
    val sortedKeys = messengerMap.keys.toList().sortedBy { it }

    for (key in sortedKeys)
        console.log(messengerMap[key])
    messengerMap.clear()
}