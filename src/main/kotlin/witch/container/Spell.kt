package witch.container

data class Spell(
        val id: Int,
        val delta0: Int,
        val delta1: Int,
        val delta2: Int,
        val delta3: Int,
        val isExhausted: Boolean,
        val repeatable: Boolean
) {
    fun deltaForTier(tier: Int): Int {
        when(tier) {
            0 -> return delta0
            1 -> return delta1
            2 -> return delta2
            3 -> return delta3
        }
        throw RuntimeException("Faulty tier")
    }

    fun neededTiers(): List<Int> {
        return getTierNums().filter { deltaForTier(it) < 0 }
    }

    fun producedTiers(): List<Int> {
        return getTierNums().filter { deltaForTier(it) > 0 }
    }

    fun debugString(): String {
        return "S(#$id [$delta0 $delta1 $delta2 $delta3] $isExhausted $repeatable)"
    }
}