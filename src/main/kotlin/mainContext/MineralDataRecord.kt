package mainContext

class MineralDataRecord(var quantity: Int = 0,
                        var quantityUp: Int = 0,
                        var quantityDown: Int = 0,
                        var priceMin: Double = 0.0,
                        var priceMax: Double = 0.0,
                        var marketSellExcess: Int = 0,
                        var marketBuyLack: Int = 0,
                        var marketSellAlways: Int = 0,
                        var marketBuyAlways: Int = 0,
                        var storeMin: Int = 0,
                        var storeMax: Int = 0,
                        var buyToRoom: String = ""
)