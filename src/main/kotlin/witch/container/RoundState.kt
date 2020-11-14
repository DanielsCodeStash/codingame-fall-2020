package witch.container

data class RoundState(
        val round: Int,
        val brews: List<Brew>,
        val tomes: List<Tome>,
        val me: Witch,
        val enemy: Witch
) {
    fun debugString(): String {

        var out = "R($round "
        out += me.debugString()
        out += " "
        out += brews.map { it.debugString() }.joinToString(" ")
        out += " "
        out += tomes.map { it.debugString() }.joinToString(" ")
        out += ")"
        return out
    }
}