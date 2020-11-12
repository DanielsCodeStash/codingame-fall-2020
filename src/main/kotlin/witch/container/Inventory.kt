package project1.container

data class Inventory(
        val tier0: Int,
        val tier1 : Int,
        val tier2: Int,
        val tier3: Int) {

    fun getNumOfTier(tier: Int): Int {
        when(tier) {
            0 -> return tier0
            1 -> return tier1
            2 -> return tier2
            3 -> return tier3
        }
        throw RuntimeException("Faulty tier")
    }
}