package productionController

import mainContext.MainContext
import mainContext.messenger
import screeps.api.COLOR_ORANGE
import screeps.api.COLOR_WHITE
import kotlin.math.min

class MineralInfo(private val numRows: Int = 3, val widthColumn: Int = 15, val numColumnInRow: Int = 10) {
    var info = Array(numRows) {""}

    fun addColumn(dataIn: Array<String>) {
        for (ind in 0 until min(dataIn.size,numRows))
            info[ind] += dataIn[ind].padEnd(widthColumn)
    }

    fun show(mainContext: MainContext) {
        for (bigRow in 0 .. ((info[0].length -1) / (widthColumn*numColumnInRow)))
            for (ind in 0 until numRows){
                val startIndex = bigRow*(widthColumn*numColumnInRow)
                val endIndex = min((bigRow + 1)*(widthColumn*numColumnInRow),this.info[ind].length)


                val tInfo = this.info[ind].subSequence( startIndex until  endIndex) as String
                mainContext.messenger("PROD","",tInfo, COLOR_ORANGE)
            }

    }
}