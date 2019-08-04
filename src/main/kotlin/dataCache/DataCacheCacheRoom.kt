package dataCache

class CacheRoom {
    var testData : Int
    fun toDynamic():dynamic {
        val d : dynamic = object {}
        d["testData"] = this.testData
        return d
    }

    constructor(testData : Int) {
        this.testData = testData
    }

    companion object {
        fun initFromDynamic(d: dynamic): CacheRoom {
            val testData: Int = if (d["testData"] != null) d["testData"] as Int else -100
            return CacheRoom(testData)
        }
    }
}