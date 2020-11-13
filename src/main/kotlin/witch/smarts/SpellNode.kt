package witch.smarts

import witch.container.*

data class SpellNode(
        val inventory: Inventory,
        val spells: List<Spell>,
        val brew: Brew,
        val madeActions: List<Action>,
        val depth: Int
) {

    fun getStateHash(): String {
        val inventoryStatus =  "${inventory.tier0}${inventory.tier1}${inventory.tier2}${inventory.tier3}"
        val spellStatus = spells
                .map { it.isExhausted }
                .joinToString()

        return "$inventoryStatus$spellStatus"
    }
}
