package productionController

data class MineralData(var quantity: Int = 0,
                       var quantityUp: Int = 0,
                       var quantityDown: Int = 0,
                       var priceMin: Double,
                       var priceMax: Double,
                       var marketSellExcess: Int,
                       var marketBuyLack: Int,
                       var marketSellAlways: Int,
                       var marketBuyAlways: Int,
                       var storeMin: Int,
                       var storeMax: Int
                       )