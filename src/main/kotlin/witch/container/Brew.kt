package witch.container

data class Brew(
        val id: Int,
        val costTier0: Int,
        val costTier1: Int,
        val costTier2: Int,
        val costTier3: Int,
        val price: Int
) {
    fun getCostOfTier(tier: Int): Int {
        when(tier) {
            0 -> return costTier0
            1 -> return costTier1
            2 -> return costTier2
            3 -> return costTier3
        }
        throw RuntimeException("Faulty tier")
    }
}