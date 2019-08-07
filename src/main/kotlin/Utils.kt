import constants.CacheCarrier
import mainRoom.MainRoom
import screeps.api.*
import kotlin.math.min
import kotlin.math.roundToInt

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

fun messenger(type: String, room: String, text: String, color: ColorConstant = COLOR_GREY,
              testBefore: String = "", colorBefore: ColorConstant = COLOR_WHITE,
              testAfter: String = "", colorAfter: ColorConstant = COLOR_WHITE) {
    if (type == "TASK") return
    //if (type == "TEST") return


    val showText = "<font color=${colorToHTMLColor(color)} > $text </font>"
    val showTextBefore = "<font color=${colorToHTMLColor(colorBefore)} > $testBefore </font>"
    val showTextAfter = "<font color=${colorToHTMLColor(colorAfter)} > $testAfter </font>"

    console.log("$type : $room $showTextBefore $showText $showTextAfter")
}

fun arrayCopy(ar0: Array<Int>, ar1: Array<Int>) {
    ar1.forEachIndexed { index, value -> ar0[index] = value }
}




