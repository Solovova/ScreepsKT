package constants

class SlaveRoomConstant {
    var model: Int = 0 //simple //0 - harvesting room, 1 - colonize room, 2 - danged harvesting room
    var autoBuildRoad: Boolean = false

    //Profit
    var profitUp: Int = 0 //cashed
    var profitDown: Int = 0 //cashed
    var profitStart: Int = 0 //cashed

    //Room algorithm
    var roomRunNotEveryTickNextTickRun: Int = 0 //cashed



    fun fromDynamic(d: dynamic) {
        if (d == null) return
        if (d["profitUp"] != null) this.profitUp = d["profitUp"] as Int
        if (d["profitDown"] != null) this.profitDown = d["profitDown"] as Int
        if (d["profitStart"] != null) this.profitStart = d["profitStart"] as Int
        if (d["roomRunNotEveryTickNextTickRun"] != null) this.roomRunNotEveryTickNextTickRun = d["roomRunNotEveryTickNextTickRun"] as Int
    }

    fun toDynamic(): dynamic {
        val result: dynamic = object {}
        result["profitUp"] = this.profitUp
        result["profitDown"] = this.profitDown
        result["profitStart"] = this.profitStart
        result["roomRunNotEveryTickNextTickRun"] = this.roomRunNotEveryTickNextTickRun
        return result
    }
}