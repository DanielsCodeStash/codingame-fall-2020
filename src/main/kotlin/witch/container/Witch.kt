package witch.container

data class Witch(
        val score: Int,
        val inventory: Inventory,
        val spells: List<Spell>
) {
    fun debugString(): String {
        var out = "W($score ${inventory.debugString()} "
        out += spells.joinToString(" ") { it.debugString() }
        return "$out)"
    }
}