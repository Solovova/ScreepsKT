import screeps.api.*

fun messenger(type: String, room: String, text: String, color: ColorConstant) {
    //if (type === "FILLED") {return; }
    var fHTMLColor = ""
    if (color === COLOR_YELLOW) {
        fHTMLColor = "yellow"; }
    if (color === COLOR_RED) {
        fHTMLColor = "red"; }
    if (color === COLOR_GREEN) {
        fHTMLColor = "green"; }
    if (color === COLOR_BLUE) {
        fHTMLColor = "blue"; }
    if (color === COLOR_ORANGE) {
        fHTMLColor = "orange"; }
    var showText = text
    if (fHTMLColor !== "") showText = "<font color=$fHTMLColor > $text </font>"
    console.log("$type : $room  $showText")
}