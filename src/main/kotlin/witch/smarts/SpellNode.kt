package witch.smarts

import witch.container.Action
import witch.container.Brew
import witch.container.Inventory
import witch.container.Spell

data class SpellNode(
        val inventory: Inventory,
        val spells: List<Spell>,
        val brew: Brew,
        val madeActions: List<Action>,
        val depth: Int
)