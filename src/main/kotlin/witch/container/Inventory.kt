package witch.container

data class Inventory(
        val tier0: Int,
        val tier1: Int,
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

    fun totalNumIngredients() = getTierNums().map { getNumOfTier(it) }.sum()

    fun getInventoryAfterSpellCast(spell: Spell, times: Int): Inventory {
        return Inventory(
                tier0 + spell.delta0 * times,
                tier1 + spell.delta1 * times,
                tier2 + spell.delta2 * times,
                tier3 + spell.delta3 * times
        )
    }

    fun getInventoryAfterSpellCast(spell: Spell): Inventory {

        return Inventory(
                tier0 + spell.delta0,
                tier1 + spell.delta1,
                tier2 + spell.delta2,
                tier3 + spell.delta3
        )
    }

    fun getTotalSize(): Int {
        return getTierNums().map { getNumOfTier(it) }.sum()
    }

    fun getInventoryAfterBrewing(brew: Brew): Inventory {
        return Inventory(
                tier0 - brew.costTier0,
                tier1 - brew.costTier1,
                tier2 - brew.costTier2,
                tier3 - brew.costTier3
        )
    }

    fun debugString(): String {
        return "I($tier0 $tier1 $tier2 $tier3)"
    }
}