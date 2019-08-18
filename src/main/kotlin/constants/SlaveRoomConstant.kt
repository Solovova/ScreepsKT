package constants

class SlaveRoomConstant {
    var model: Int = 0 //simple //0 - harvesting room, 1 - colonize room, 2 - danged harvesting room
    var autoBuildRoad: Boolean = false

    //Profit
    var profitUp: Int = 0 //cashed
    var profitDown: Int = 0 //cashed
    var profitStart: Int = 0 //cashed
    var profitPerTickPrevious: Int = 0 //cashed
    var profitPerTickPreviousArr: Array<Int> = arrayOf(0,0,0,0,0) //cashed
    var roadBuild: Boolean = false //cashed

    //Room algorithm
    var roomRunNotEveryTickNextTickRun: Int = 0 //cashed
    var roomHostile: Boolean = false
    var roomHostileType: Int = 0 // 1 -  mile, 2 - ranged
    var roomHostileNum: Int = 0
    var needCleaner: Boolean = false

    var creepIdMineralHarvester: String = ""






    fun fromDynamic(d: dynamic) {
        if (d == null) return
        if (d["profitUp"] != null) this.profitUp = d["profitUp"] as Int
        if (d["profitDown"] != null) this.profitDown = d["profitDown"] as Int
        if (d["profitStart"] != null) this.profitStart = d["profitStart"] as Int
        if (d["roadBuild"] != null) this.roadBuild = d["roadBuild"] as Boolean
        if (d["roomHostile"] != null) this.roomHostile = d["roomHostile"] as Boolean
        if (d["roomHostileType"] != null) this.roomHostileType = d["roomHostileType"] as Int
        if (d["roomHostileNum"] != null) this.roomHostileNum = d["roomHostileNum"] as Int
        if (d["needCleaner"] != null) this.needCleaner = d["needCleaner"] as Boolean
        if (d["profitPerTickPreviousArr"] != null) this.profitPerTickPreviousArr = d["profitPerTickPreviousArr"] as Array<Int>
        if (d["roomRunNotEveryTickNextTickRun"] != null) this.roomRunNotEveryTickNextTickRun = d["roomRunNotEveryTickNextTickRun"] as Int
    }

    fun toDynamic(): dynamic {
        val result: dynamic = object {}
        result["profitUp"] = this.profitUp
        result["profitDown"] = this.profitDown
        result["profitStart"] = this.profitStart
        result["roadBuild"] = this.roadBuild
        result["roomHostile"] = this.roomHostile
        result["roomHostileType"] = this.roomHostileType
        result["roomHostileNum"] = this.roomHostileNum
        result["needCleaner"] = this.needCleaner
        result["profitPerTickPreviousArr"] = this.profitPerTickPreviousArr
        result["roomRunNotEveryTickNextTickRun"] = this.roomRunNotEveryTickNextTickRun
        return result
    }
}