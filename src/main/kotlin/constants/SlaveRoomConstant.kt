package constants

class SlaveRoomConstant {
    var model: Int = 0 //simple //0 - harvesting room, 1 - colonize room, 2 - danged harvesting room
    var autoBuildRoad: Boolean = false

    //Profit
    var profitUp: Int = 0 //cashed
    var profitDown: Int = 0 //cashed
    var profitStart: Int = 0 //cashed
    var profitPerTickPrevious: Int = 0 //cashed
    var roadBuild: Boolean = false //cashed

    //Room algorithm
    var roomRunNotEveryTickNextTickRun: Int = 0 //cashed



    fun fromDynamic(d: dynamic) {
        if (d == null) return
        if (d["profitUp"] != null) this.profitUp = d["profitUp"] as Int
        if (d["profitDown"] != null) this.profitDown = d["profitDown"] as Int
        if (d["profitStart"] != null) this.profitStart = d["profitStart"] as Int
        if (d["roadBuild"] != null) this.roadBuild = d["roadBuild"] as Boolean

        if (d["profitPerTickPrevious"] != null) this.profitPerTickPrevious = d["profitPerTickPrevious"] as Int
        if (d["roomRunNotEveryTickNextTickRun"] != null) this.roomRunNotEveryTickNextTickRun = d["roomRunNotEveryTickNextTickRun"] as Int
    }

    fun toDynamic(): dynamic {
        val result: dynamic = object {}
        result["profitUp"] = this.profitUp
        result["profitDown"] = this.profitDown
        result["profitStart"] = this.profitStart
        result["roadBuild"] = this.roadBuild

        result["profitPerTickPrevious"] = this.profitPerTickPrevious
        result["roomRunNotEveryTickNextTickRun"] = this.roomRunNotEveryTickNextTickRun
        return result
    }
}