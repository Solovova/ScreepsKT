package constants

class BattleGroupContainerConstant {
    var testCashedBGC: Int = 0 //Cashed
    var testSimpleBGC: Int = 0 //Simple

    fun fromDynamic(d: dynamic) {
        if (d == null) return
        if (d["testCashedBGC"] != null) this.testCashedBGC = d["testCashedBGC"] as Int
    }

    fun toDynamic(): dynamic {
        val result: dynamic = object {}
        result["testCashedBGC"] = this.testCashedBGC
        return result
    }
}