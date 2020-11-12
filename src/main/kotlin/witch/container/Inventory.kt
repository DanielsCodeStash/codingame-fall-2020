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

    fun getInventoryAfterSpellCast(spell: Spell): Inventory {
        val newInventory = Inventory(
                tier0 + spell.delta0,
                tier1 + spell.delta1,
                tier2 + spell.delta2,
                tier3 + spell.delta3
        )

        if(getTierNums().any { newInventory.getNumOfTier(it) < 0 }) {
            throw RuntimeException("Faulty spellcast! $spell $this")
        }

        return newInventory
    }

    fun toDebugString(): String {
        return "Inventory ($tier0, $tier1, $tier2, $tier3)"
    }
}