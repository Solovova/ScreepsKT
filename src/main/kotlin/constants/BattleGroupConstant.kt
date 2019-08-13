package constants

class BattleGroupConstant {
    var testCashedBG: Int = 0 //Cashed
    var testSimpleBG: Int = 0 //Simple

    fun fromDynamic(d: dynamic) {
        if (d == null) return
        if (d["testCashedBG"] != null) this.testCashedBG = d["testCashedBG"] as Int
    }

    fun toDynamic(): dynamic {
        val result: dynamic = object {}
        result["testCashedBG"] = this.testCashedBG
        return result
    }
}